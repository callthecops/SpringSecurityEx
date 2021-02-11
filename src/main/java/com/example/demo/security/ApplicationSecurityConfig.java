package com.example.demo.security;

import com.example.demo.auth.ApplicationUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.util.concurrent.TimeUnit;

import static com.example.demo.security.ApplicationUserRole.*;


@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class ApplicationSecurityConfig extends WebSecurityConfigurerAdapter {

    private final PasswordEncoder passwordEncoder;
    private final ApplicationUserService applicationUserService;

    @Autowired
    public ApplicationSecurityConfig(PasswordEncoder passwordEncoder, ApplicationUserService applicationUserService) {
        this.passwordEncoder = passwordEncoder;
        this.applicationUserService = applicationUserService;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/", "index", "/css/*", "/js/*").permitAll()
                .antMatchers("/api/**").hasRole(STUDENT.name())
                .anyRequest()
                .authenticated()
                .and()
                .formLogin()
                .loginPage("/login")
                .permitAll()
                //We have to add this line and create a handler for the page in order to be redirected
                //to this page upon successfully login.
                .defaultSuccessUrl("/courses", true)
                //the values here have to be the same as the input attributes names in the login form.
                .passwordParameter("password")
                .usernameParameter("username")
                //If we want to enable the remember me function in spring security we have to add .and()
                //.rememberMe() after defaultsuccessUrl.This method works on with the rememberMe Cookie Token.When we
                //use the rememberMe function we have 2 cookies instead of one.Both are stored in memory.
                //The default period of remembarance is 2 weeks.
                .and()
                .rememberMe()
                //This option here makes the token validity to 3 weeks.
                .tokenValiditySeconds((int) TimeUnit.DAYS.toSeconds(21))
                //This is the key that it is used to hash the contents(username + expiration date md5 hash value).
                .key("somethingverysecured")
                //tthis has to be the same as the remember me html attribute form the page checkbox.
                .rememberMeParameter("remember-me")
                .and()
                .logout()
                //this is the default logout url
                .logoutUrl("/logout")
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout", "GET")) // https://docs.spring.io/spring-security/site/docs/4.2.12.RELEASE/apidocs/org/springframework/security/config/annotation/web/configurers/LogoutConfigurer.html
                .clearAuthentication(true)
                .invalidateHttpSession(true)
                //these deletes the cookies and the ones that we have created.
                .deleteCookies("JSESSIONID", "remember-me")
                .logoutSuccessUrl("/login");
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        //here we simply use the return value of the custom method that we have previously build.
        auth.authenticationProvider(daoAuthenticationProvider());
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        //here we have to provide a few things to the provider like passwordEncoder, the userDetailsService
        //that we just created.Once this is done we have to override the configure method that takes
        //the AuthenticationManagerBuilder as a parameter above.
        provider.setPasswordEncoder(passwordEncoder);
        provider.setUserDetailsService(applicationUserService);
        return provider;
    }
}

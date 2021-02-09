package com.example.SpringSecurity.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import static com.example.SpringSecurity.security.ApplicationUserPermission.*;
import static com.example.SpringSecurity.security.ApplicationUserRole.*;

//Here we configure everything that has to do with security.We have to extend WebSecurityConfigurerAdapter.
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class ApplicationSecurityConfig extends WebSecurityConfigurerAdapter {

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public ApplicationSecurityConfig(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    //1.Users
    //When we start with basic spring security , we get a default user and a default generated password by spring.
    //When we a bunch of users we want to store them in a database.
    //User MUST HAVE the following properties: Username(unique), Password(encoded), Role(this allows us to control
    //what endpoints the user is allowed to hit), Authorities (what they can perform inside those endpoints).
    //We have several ways of creating users - InMemory Database and Real Database.In order to create users we
    //have to override the userDetailsService method -> Next 2.Retrieving/Creating Users
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //Introduction - > Next 1.Users
        //This code here means that we want to authorize request that means user needs authorisation to
        //access the resources located at that url endpoint.anyRequest + authenticated means the client has to
        //specify the username and password in order to get authorisation to access the files.And the mechanism
        //for authentication is BasicAuth.BasicAuth makes a pop up window appear instead of the standard web form.
        //We cannot log out with BasicAuth by using localhost:8080/logout.This happens because the user and password
        //is used on every single request.After authorizeRequest we can add antMatchers.This allows us to allow some
        //request to not need authorisation.All resources paths that are specified with antMatchers patterns are white listed.
        //* Means everything that comes after.In order for antmatchers to work we have to use the permitAll method
        //right after.

        http
                //we need to disable cross site request forgery.By default Spring security tries to protect our api.
                //Recommandation is to use csrf protection for any request that could be preocessed by a browser
                // by normal users.If we are only creating a service that is used by non browser clients, we will
                //likely want to disable csrf protection.
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/", "index", "/css/*", "/js/*").permitAll()
                //7.Here we set the Role allowed for this path.In this case everything that comes after the
                //api/ path is accessed only by Student role.-> Next 8.Permissions based authentication
                .antMatchers("/api/**").hasRole(STUDENT.name())
                //9. Implementing permission based authentication
                //There are 2 ways of doing this, first is with antMatchers.In this case we pass in the
                //route and the http method that we want to allow for a certain Authority(Permission).We created the
                //permissions in ApplicationUserPermission.We do this for each method.For the Get method we assing
                //both admin and admintrainee to be able to access. Next -> 10.Adding authorities to users
//                .antMatchers(HttpMethod.DELETE,"management/api/**").hasAuthority(COURSE_WRITE.getPermission())
//                .antMatchers(HttpMethod.POST,"management/api/**").hasAuthority(COURSE_WRITE.getPermission())
//                .antMatchers(HttpMethod.PUT,"management/api/**").hasAuthority(COURSE_WRITE.getPermission())
//                .antMatchers(HttpMethod.GET,"management/api/**").hasAnyRole(ADMIN.name(), ADMINTRAINEE.name())
                .anyRequest()
                .authenticated()
                .and()
                .httpBasic();
    }

    //2.Retrieving/Creating Users
    //By using @Bean we are autoinstantiating the class.The UserDetailsService class represents the way we are retrieving
    //the users from the database.In this first example we use a builder method to build a user with a password and
    //assign it a role.In order to be able to use this successfully we have to also use a password Encoder.
    //next - >3.PasswordConfig and create the Bcrypt Encoder.
    @Override
    @Bean
    protected UserDetailsService userDetailsService() {
        UserDetails annaSmithUser = User.builder()
                .username("annasmith")
                //4.Encoding the password.After creating the PassWord Encoder and autowiring it in the constructor for
                //this class we can now use it to encode the password.Now we can finally log in with this user.
                //Next 5.Creating a second user and assigning roles.
                .password(passwordEncoder.encode("pass"))
                //We try roles first()
                //.roles(ApplicationUserRole.STUDENT.name())//6.Assigning the roles.Next 7. Securing the API
                // Here we use the enum roles that we created previously at point 5.
                //We try authorities second(AFTER POINT 9.)then we go and implement in ApplicationUserRole

                //10.Passing arguments to authorities after we have created the permissions.
                //We do the same with linda.Once all the users have authorities set, we have successfully implemented
                //permission based authentication.
                .authorities(STUDENT.getGrantedAuthorities())
                .build();

        //5.Creating a second user and assigning roles
        //We create roles with enums.We create the Enum ApplicationUserRole where we declare the roles we want.
        //We do the same thing for permissions in ApplicationUserPermission.
        UserDetails lindaUser = User.builder()
                .username("linda")
                .password(passwordEncoder.encode("pass"))
//                .roles(ApplicationUserRole.ADMIN.name())//6.Assigning the roles.Next 7. Securing the API
                // Here we use the enum roles that we created previously at point 5.
                .authorities(ADMIN.getGrantedAuthorities())

                .build();


        //8.Permissions based authentication.Here we create another user tom with an ADminTrainee permission.
        //He will be able to only read at path management/api/v1/students.His role is in ApplicationUserRole.
        // and the Controller methods are in StudentManagementController.Here tom has only the possibility to
        //GetALlStudents() (read only permission), while linda is able to register, update and delete a new student(write permission).
        //Next -> 9. Implementing permission based authentication.
        UserDetails tomUser = User.builder()
                .username("tom")
                .password(passwordEncoder.encode("pass"))
//                .roles(ApplicationUserRole.ADMINTRAINEE.name())//6.Assigning the roles.Next 7. Securing the API
                // Here we use the enum roles that we created previously at point 5.
                .authorities(ADMINTRAINEE.getGrantedAuthorities())
                .build();


        return new InMemoryUserDetailsManager(annaSmithUser, lindaUser,tomUser);
    }
}

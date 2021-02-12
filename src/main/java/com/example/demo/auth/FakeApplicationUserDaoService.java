package com.example.demo.auth;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.example.demo.security.ApplicationUserRole.*;

//in this class we need to retrieve the database users.For now we are using an in memory database so we have
// to manually create the users in the getApplicationUser method.In order to create the users we also need a
//password encoder which instance we have already defined so we can autowire it in the constructor so it is
//instantiated as a field variable.

//This is a fake repository.@Repository tells spring that this class needs to be instantiated and the name "fake"
//is what we use when we autowired in case we have more than one implementation of the same class.
@Repository("fake")
public class FakeApplicationUserDaoService implements ApplicationUserDao {
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public FakeApplicationUserDaoService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    //This method here returns the user as optional  if we pass it a string username.In this case because we do not
    //have a real database from which to retrieve users, we use the return value of the getApplicationUser method which
    //we just created to fetch the 3 User types that we have created, and filter them based on username;
    //Once everything is set in this class we can go and adapt the ApplicationSeucirityConfig with the new
    //Users - >daoAuthenticationProvider() in the class;
    @Override
    public Optional<ApplicationUser> selectApplicationUserByUsername(String username) {
        return getApplicationUser()
                .stream()
                .filter(applicationUser -> username.equals(applicationUser.getUsername()))
                .findFirst();
    }

    private List<ApplicationUser> getApplicationUser() {
        List<ApplicationUser> applicationUsers = Lists.newArrayList(
                new ApplicationUser("anna", passwordEncoder.encode("pass"),
                        STUDENT.getGrantedAuthorities(), true, true, true,
                        true), new ApplicationUser("linda", passwordEncoder.encode("pass"),
                        ADMIN.getGrantedAuthorities(), true, true, true,
                        true), new ApplicationUser("tom", passwordEncoder.encode("pass"),
                        ADMINTRAINEE.getGrantedAuthorities(), true, true, true,
                        true));
        return applicationUsers;
    }
}

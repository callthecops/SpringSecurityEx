package com.example.demo.auth;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


//This class is used to fetch stuff from our database.It is following the n tier arhitecture.After we create
//the ApplicationUserDao interface with that specific method, we can implement it here in the loadUserByUsername
//method;We are using interfaces because we can use dependency injection to switch mplementations if we want
// to do it so.For example we can use postgres instead of mongodb we don't have to change more than 1 line of code.
//Further we have to create a class that implements the method of the ApplicationUserDao interface.
@Service
public class ApplicationUserService implements UserDetailsService {

    private final ApplicationUserDao applicationUserDao;

    public ApplicationUserService(ApplicationUserDao applicationUserDao) {
        this.applicationUserDao = applicationUserDao;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return applicationUserDao
                .selectApplicationUserByUsername(username)
                .orElseThrow(
                ()-> new UsernameNotFoundException(String.format("User %s not found",username)));
    }
}

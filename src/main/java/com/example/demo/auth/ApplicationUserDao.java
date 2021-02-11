package com.example.demo.auth;

import java.util.Optional;

//The scope of this interface is to load any user by username.We can load users from any datasource with this
//interface.Then we have to use it as an field in our ApplicationUserService class.
public interface ApplicationUserDao {

    Optional<ApplicationUser> selectApplicationUserByUsername(String username);

}

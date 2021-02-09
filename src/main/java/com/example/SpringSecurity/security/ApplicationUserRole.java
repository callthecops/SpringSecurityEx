package com.example.SpringSecurity.security;

import com.google.common.collect.Sets;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Set;
import java.util.stream.Collectors;

import static com.example.SpringSecurity.security.ApplicationUserPermission.*;

public enum ApplicationUserRole {
    //This makes an empty set for the Student, because it has no permissions.The Admin has all the permissions we
    //created.We add a static import to do that.
    STUDENT(Sets.newHashSet()),
    ADMIN(Sets.newHashSet(COURSE_READ, COURSE_WRITE,
            STUDENT_READ, STUDENT_WRITE)),
    //This is the permission for tomUser
    ADMINTRAINEE(Sets.newHashSet(COURSE_READ,
            STUDENT_READ));
    //We said that each Role can have several permissions, and we have to assign these permissions in the constructor.
    private final Set<ApplicationUserPermission> permissions;

    ApplicationUserRole(Set<ApplicationUserPermission> permissions) {
        this.permissions = permissions;
    }

    //roles method build GrantedAuthorities from the roles we pass by appending ROLE_ to the role we pass in
    //UserDetails is an interface and it what we need for each user.It has no concept of roles or permissions
    //so everything that we need in order to use roles and permissions is bundled in the return value of the
    //getAuthorities method wich returns a collection of <? extends GrantedAuthoritites>.
    // This means we need to build the roles ourselves which is what we do here by instantiating the
    //implementation of a GrantedAuthority interface namely SimpleGrantedAuthority.Once this method is
    //returning the correct data we can pass arguments to the authorities method.Next 10.Passing arguments to
    //authorities method.
    public Set<SimpleGrantedAuthority> getGrantedAuthorities() {
        Set<SimpleGrantedAuthority> permissions = getPermissions().stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
                .collect(Collectors.toSet());
        permissions.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
        return permissions;
    }

    public Set<ApplicationUserPermission> getPermissions() {
        return permissions;
    }
}

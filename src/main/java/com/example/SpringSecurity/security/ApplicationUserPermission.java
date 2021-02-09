package com.example.SpringSecurity.security;
//WE create permissions here with the help of the enum constructor.These are the same as Authority.
public enum ApplicationUserPermission {
    STUDENT_READ("student:read"),
    STUDENT_WRITE("student:write"),
    COURSE_READ("course:read"),
    COURSE_WRITE("course:write");

    private final String permission;

    ApplicationUserPermission(String permission){
        this.permission=permission;
    }

    public String getPermission() {
        return permission;
    }
}

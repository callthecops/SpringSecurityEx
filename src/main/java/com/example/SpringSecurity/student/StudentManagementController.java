package com.example.SpringSecurity.student;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("management/api/v1/students")
public class StudentManagementController {

    private static final List<Student> STUDENT_LIST = Arrays.asList(new Student(1,"james")
            ,new Student(2,"maria")
            ,new Student(3,"Anna"));

    @GetMapping
    //to use permission based authentication on a method level we have to use @PreAuthorize.This takes arguments
    //like : hasRole('ROLE_) hasAnyRole('ROLE_) hasAuthority('permission') hasAnyAuthority('permission')
    //if we use this annotation the antmatchers in ApplicationSecurityConfig are not needed anymore.We can choose either way
    // of the 2 ways of configuration.Now we have to
    //tell our configuration classes that we want to use these annotations for permission/role based authentication.
    //So the way we do it is inside the configuration in ApplicationSecurityConfig we add @EnableGlobalMethodSecurity(prePostEnabled = true).
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_ADMINTRAINEE')")
    public List<Student> getAllStudents() {
        System.out.println("getAllStudents");
        return STUDENT_LIST;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('student:write')")
    public void registerNewStudent(@RequestBody Student student){
        System.out.println("registerNewStudent");
        System.out.println(student);
    }

    @DeleteMapping(path = "{studentId}")
    @PreAuthorize("hasAuthority('student:write')")
    public void deleteStudent(@PathVariable("studentId") Integer studentId){
        System.out.println("deleteStudent");
        System.out.println(studentId);
    }

    @PutMapping(path = "{studentId}")
    @PreAuthorize("hasAuthority('student:write')")
    public void updateStudent(@PathVariable("studentId") Integer studentId,@RequestBody Student student){
        System.out.println("updateStudent");
        System.out.println(String.format("%s %s",studentId,student));
    }
}

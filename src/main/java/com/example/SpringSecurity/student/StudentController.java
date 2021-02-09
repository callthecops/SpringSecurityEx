package com.example.SpringSecurity.student;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("api/v1/students")
public class StudentController {

    private static final List<Student> STUDENT_LIST = Arrays.asList(new Student(1,"james")
                                     ,new Student(2,"maria")
                                     ,new Student(3,"Anna"));


    @GetMapping(path = "{studentId}")
    public Student getStundent(@PathVariable("studentId") Integer studentId){
        return STUDENT_LIST.stream()
                .filter(student -> studentId.equals(student.getStudentId()))
                .findFirst()
                .orElseThrow(()-> new  IllegalStateException("Student " +studentId + "does not exist"));

    }
}

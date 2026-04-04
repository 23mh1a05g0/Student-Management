package com.student.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.student.model.Student;
import com.student.service.S3Service;
import com.student.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;
    private final S3Service s3Service;
    
    @PostMapping(consumes = "multipart/form-data")
    public Student addStudent(
            @RequestParam("student") String studentJson,
            @RequestParam("file") MultipartFile file
    ) throws IOException {

        ObjectMapper mapper = new ObjectMapper();
        Student student = mapper.readValue(studentJson, Student.class);

        String imageUrl = s3Service.uploadFile(file);
        student.setImageUrl(imageUrl);

        return studentService.addStudent(student);
    }

    @GetMapping
    public List<Student> getAllStudents() {
        return studentService.getAllStudents();
    }

    @GetMapping("/{id}")
    public Student getStudentById(@PathVariable Long id) {
        return studentService.getStudentById(id);
    }

    @PutMapping(value = "/{id}", consumes = "multipart/form-data")
    public Student updateStudent(
            @PathVariable Long id,
            @RequestParam("student") String studentJson,
            @RequestParam(value = "file", required = false) MultipartFile file
    ) throws IOException {

        ObjectMapper mapper = new ObjectMapper();
        Student updatedStudent = mapper.readValue(studentJson, Student.class);

        return studentService.updateStudent(id, updatedStudent, file);
    }

    @DeleteMapping("/{id}")
    public String deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return "Student deleted successfully";
    }
}
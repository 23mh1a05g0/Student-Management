package com.student.service;

import com.student.model.Student;
import com.student.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;
    private final S3Service s3Service;

    // ✅ CREATE
    public Student addStudent(Student student) {
        return studentRepository.save(student);
    }

    // ✅ READ ALL
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    // ✅ READ BY ID
    public Student getStudentById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found"));
    }

    // ✅ UPDATE
    public Student updateStudent(Long id, Student updatedStudent, MultipartFile file) throws IOException {

        Student existingStudent = getStudentById(id);

        existingStudent.setName(updatedStudent.getName());
        existingStudent.setRollNumber(updatedStudent.getRollNumber());
        existingStudent.setBranch(updatedStudent.getBranch());
        existingStudent.setCollege(updatedStudent.getCollege());
        existingStudent.setSkills(updatedStudent.getSkills());
        existingStudent.setEmail(updatedStudent.getEmail());
        existingStudent.setPhone(updatedStudent.getPhone());
        existingStudent.setAge(updatedStudent.getAge());
        existingStudent.setGender(updatedStudent.getGender());

        // If new image uploaded
        if (file != null && !file.isEmpty()) {

            // Delete old image from S3
            if (existingStudent.getImageUrl() != null) {
                s3Service.deleteFile(existingStudent.getImageUrl());
            }

            // Upload new image
            String imageUrl = s3Service.uploadFile(file);
            existingStudent.setImageUrl(imageUrl);
        }

        return studentRepository.save(existingStudent);
    }

    // ✅ DELETE
    public void deleteStudent(Long id) {
        Student student = getStudentById(id);

        if (student.getImageUrl() != null) {
            s3Service.deleteFile(student.getImageUrl());
        }

        studentRepository.deleteById(id);
    }

    // 🔥 NEW: FILTER + PAGINATION
    public Page<Student> getStudents(String skills, String gender, Integer age, int page, int size) {

        // Default pagination → 4 per page
        Pageable pageable = PageRequest.of(page, size);

        // 🔥 Skills filter → ONLY TOP 3
        if (skills != null && !skills.isEmpty()) {
            return studentRepository.findBySkillsContainingIgnoreCase(
                    skills,
                    PageRequest.of(0, 3) // always return top 3
            );
        }

        // 🔥 Gender filter
        if (gender != null && !gender.isEmpty()) {
            return studentRepository.findByGenderIgnoreCase(gender, pageable);
        }

        // 🔥 Age filter
        if (age != null) {
            return studentRepository.findByAge(age, pageable);
        }

        // 🔥 Default → all students with pagination
        return studentRepository.findAll(pageable);
    }
}
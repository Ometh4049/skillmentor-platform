package com.skill_mentor.skillmentor.services;

import com.skill_mentor.skillmentor.entities.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface StudentService {

    Student createNewStudent(Student student);

    Page<Student> getAllStudents(String name, Pageable pageable);

    Student getStudentById(Long id);

    Student updateStudentById(Long id, Student updatedStudent);

    void deleteStudent(Long id);

}

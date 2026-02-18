package com.skill_mentor.skillmentor.controllers;

import com.skill_mentor.skillmentor.dto.StudentDTO;
import com.skill_mentor.skillmentor.entities.Student;
import com.skill_mentor.skillmentor.services.StudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path= "api/v1/students")
@RequiredArgsConstructor
@Validated
public class StudentController extends AbstractController {

    private final ModelMapper modelMapper;
    private final StudentService studentService;


    @GetMapping
    public ResponseEntity<Page<Student>> getAllStudents(
            @RequestParam(name = "name", required = false)
            String name, Pageable pageable) {
        Page<Student> students = studentService.getAllStudents(name, pageable);
        return sendOkResponse(students);
    }

    @GetMapping("{id}")
    public ResponseEntity<Student> getStudentById(@PathVariable Long id) {
        Student foundStudent = studentService.getStudentById(id);
        return sendOkResponse(foundStudent);
    }

    @PostMapping
    public ResponseEntity<Student> createStudent(@Valid @RequestBody StudentDTO studentDTO) {

        Student student = modelMapper.map(studentDTO, Student.class);

        Student createdStudent = studentService.createNewStudent(student);

        return sendCreatedResponse(createdStudent);
    }

    @PutMapping("{id}")
    public ResponseEntity<Student> updateStudentById(@PathVariable Long id, @Valid @RequestBody StudentDTO studentDTO) {

        Student student = modelMapper.map(studentDTO, Student.class);
        Student updatedStudent = studentService.updateStudentById(id, student);

        return sendOkResponse(updatedStudent);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Student> deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return sendNoContentResponse();
    }

}

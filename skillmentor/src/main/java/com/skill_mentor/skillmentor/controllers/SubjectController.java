package com.skill_mentor.skillmentor.controllers;

import com.skill_mentor.skillmentor.dto.SubjectDTO;
import com.skill_mentor.skillmentor.entities.Subject;
import com.skill_mentor.skillmentor.services.SubjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path= "api/v1/subjects")
@RequiredArgsConstructor
@Validated
public class SubjectController {

    private final ModelMapper modelMapper;

    private final SubjectService subjectService;

    @GetMapping
    public Page<Subject> getAllSubjects(@RequestParam(name="name" , defaultValue = "all") String name , Pageable pageable){
        return subjectService.getAllSubjects(name, pageable);
    }

    @GetMapping("{id}")
    public Subject getSubjectsById(@PathVariable Long id){
        return subjectService.getSubjectsById(id);
    }

    @PostMapping
    public Subject createSubject(@Valid @RequestBody SubjectDTO subjectDTO){

        Subject subject = modelMapper.map(subjectDTO, Subject.class);
        subject.setId(null);
        return subjectService.createSubject(subjectDTO.getMentorId(),subject);
    }

    @PutMapping("{id}")
    public Subject updateSubject(@PathVariable Long id,@Valid @RequestBody SubjectDTO subjectDTO){


        Subject updatedSubject = modelMapper.map(subjectDTO,Subject.class);
        return subjectService.updateSubjectById(id,updatedSubject);

    }

    @DeleteMapping("{id}")
    public void deleteSubject(@PathVariable Long id){
        subjectService.deleteSubject(id);
    }
}

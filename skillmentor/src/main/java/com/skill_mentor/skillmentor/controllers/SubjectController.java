package com.skill_mentor.skillmentor.controllers;

import com.skill_mentor.skillmentor.dto.SubjectDTO;
import com.skill_mentor.skillmentor.entities.Subject;
import com.skill_mentor.skillmentor.services.SubjectService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(path= "api/v1/subjects")
public class SubjectController {

    private final ModelMapper modelMapper;

    private final SubjectService  subjectService;

    public SubjectController(ModelMapper modelMapper, SubjectService subjectService) {
        this.modelMapper = modelMapper;
        this.subjectService = subjectService;
    }

    @GetMapping
    public List<Subject> getAllSubjects(@RequestParam(name="name" , defaultValue = "all") String name){
        return subjectService.getAllSubjects();
    }

    @GetMapping("{id}")
    public Subject getSubjectsById(@PathVariable Long id){
        return subjectService.getSubjectsById(id);
    }

    @PostMapping
    public Subject createSubject(@Valid @RequestBody SubjectDTO subjectDTO){

//        mapping subject DTO to subject
//        Subject subject = new Subject();
//        subject .setSubjectName(subjectDTO.getSubjectName());
//        subject.setDescription(subjectDTO.getDescription());

        // do that mapping using model mapper
        Subject subject = modelMapper.map(subjectDTO, Subject.class);

        return subjectService.createSubject(subject);
    }

    @PutMapping("{id}")
    public Subject updateSubject(@PathVariable Long id,@Valid @RequestBody SubjectDTO subjectDTO){


        Subject updatedSubject = modelMapper.map(subjectDTO,Subject.class);
        return subjectService.updateSubject(id,updatedSubject);

    }

    @DeleteMapping("{id}")
    public void deleteSubject(@PathVariable Long id){
        subjectService.deleteSubject(id);
    }
}

package com.skill_mentor.skillmentor.controllers;

import com.skill_mentor.skillmentor.dto.SubjectDTO;
import com.skill_mentor.skillmentor.entities.Subject;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(path= "api/v1/subjects")
public class SubjectController {

    private final ModelMapper modelMapper;

    private final List<Subject> subjects = new ArrayList<>((
            List.of(new Subject("Maths" , "MT 001"),
                    new Subject("Computer Science" , "CS 001"))
    ));

    public SubjectController(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @GetMapping
    public List<Subject> getAllSubjects(@RequestParam(name="name" , defaultValue = "all") String name){
        return subjects;
    }

//    including path parameter & query parameter
//    @GetMapping("{id}")
//    public List<Subject> getAllSubjectsById(@PathVariable int id){
//        return subjects.get(id);
//    }

    @PostMapping
    public List<Subject> createSubject(@Valid @RequestBody SubjectDTO subjectDTO){

//        mapping subject DTO to subject
//        Subject subject = new Subject();
//        subject .setSubjectName(subjectDTO.getSubjectName());
//        subject.setDescription(subjectDTO.getDescription());

//        do that mapping using model mapper
        Subject subject = modelMapper.map(subjectDTO, Subject.class);

        subjects.add(subject);
        return subjects;
    }

//    @PutMapping("{id}")
//    public Subject updateSubject(@PathVariable int id,@RequestBody String subject){
//        subjects.set(id ,subject);
//        return "subject updated";
//    }
//
//    @DeleteMapping("{id}")
//    public Subject deleteSubject(@PathVariable int id){
//        subjects.remove(id);
//
//        return subjects.toString();
//    }
}

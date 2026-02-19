package com.skill_mentor.skillmentor.controllers;

import com.skill_mentor.skillmentor.dto.SubjectDTO;
import com.skill_mentor.skillmentor.entities.Mentor;
import com.skill_mentor.skillmentor.entities.Subject;
import com.skill_mentor.skillmentor.services.SubjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path= "api/v1/subjects")
@RequiredArgsConstructor
@Validated
public class SubjectController extends AbstractController{

    private final ModelMapper modelMapper;

    private final SubjectService subjectService;

    @GetMapping
    public ResponseEntity<Page<Subject>> getAllSubjects(@RequestParam(name="name" , defaultValue = "all") String name , Pageable pageable){
        Page<Subject> subjects =  subjectService.getAllSubjects(name, pageable);
        return sendOkResponse(subjects);
    }

    @GetMapping("{id}")
    public ResponseEntity<Subject> getSubjectsById(@PathVariable Long id){
        Subject subjectSubject = subjectService.getSubjectsById(id);
        return sendOkResponse(subjectSubject);
    }

    @PostMapping
    public ResponseEntity<Subject> createSubject(@Valid @RequestBody SubjectDTO subjectDTO){

        Subject subject = modelMapper.map(subjectDTO, Subject.class);
        subject.setId(null);
        Subject createdSubject = subjectService.createSubject(subjectDTO.getMentorId(),subject);

        return sendOkResponse(createdSubject);
    }

    @PutMapping("{id}")
    public ResponseEntity<Subject> updateSubject(@PathVariable Long id,@Valid @RequestBody SubjectDTO subjectDTO){


        Subject updatedSubject = modelMapper.map(subjectDTO,Subject.class);
        Subject subject =  subjectService.updateSubjectById(id,updatedSubject);

        return sendOkResponse(subject);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Subject> deleteSubject(@PathVariable Long id){
        subjectService.deleteSubject(id);
        return sendNoContentResponse();
    }
}

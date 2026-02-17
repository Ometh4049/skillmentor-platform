package com.skill_mentor.skillmentor.controllers;

import com.skill_mentor.skillmentor.dto.MentorDTO;
import com.skill_mentor.skillmentor.entities.Mentor;
import com.skill_mentor.skillmentor.services.impl.MentorServiceImpl;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path= "api/v1/mentors")
public class MentorController extends AbstractController{

    private final ModelMapper modelMapper;

    private final MentorServiceImpl mentorServiceImpl;

    public MentorController(ModelMapper modelMapper, MentorServiceImpl mentorServiceImpl) {
        this.modelMapper = modelMapper;
        this.mentorServiceImpl = mentorServiceImpl;
    }

    @GetMapping
    public ResponseEntity<List<Mentor>> getAllMentors(){
        List<Mentor> mentors =  mentorServiceImpl.getAllMentors();
        return sendOkResponse(mentors);
    }

    @GetMapping("{id}")
    public ResponseEntity<Mentor> getMentorById(@PathVariable Long id){
        Mentor foundMentor = mentorServiceImpl.getMentorById(id);
        return sendOkResponse(foundMentor);
    }

    @PostMapping
    public ResponseEntity<Mentor> createMentor(@Valid @RequestBody MentorDTO mentorDTO){

        Mentor mentor = modelMapper.map(mentorDTO,Mentor.class);

        Mentor createdMentor = mentorServiceImpl.createMentor(mentor);

        return sendCreatedResponse(createdMentor);
    }

    @PutMapping("{id}")
    public ResponseEntity<Mentor> updateMentorById(@PathVariable Long id,@Valid @RequestBody MentorDTO mentorDTO){

        Mentor mentor = modelMapper.map(mentorDTO,Mentor.class);
        Mentor updatedMentor = mentorServiceImpl.updateMentorById(id,mentor);

        return sendOkResponse(updatedMentor);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Mentor> deleteMentor(@PathVariable Long id){
        mentorServiceImpl.deleteMentor(id);
        return sendNoContentResponse();
    }

}

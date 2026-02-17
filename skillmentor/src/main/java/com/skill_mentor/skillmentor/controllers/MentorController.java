package com.skill_mentor.skillmentor.controllers;

import com.skill_mentor.skillmentor.dto.MentorDTO;
import com.skill_mentor.skillmentor.entities.Mentor;
import com.skill_mentor.skillmentor.services.MentorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path= "api/v1/mentors")
@RequiredArgsConstructor
@Validated
public class MentorController extends AbstractController{

    private final ModelMapper modelMapper;

    private final MentorService mentorService;


    @GetMapping
    public ResponseEntity<List<Mentor>> getAllMentors(){
        List<Mentor> mentors =  mentorService.getAllMentors();
        return sendOkResponse(mentors);
    }

    @GetMapping("{id}")
    public ResponseEntity<Mentor> getMentorById(@PathVariable Long id){
        Mentor foundMentor = mentorService.getMentorById(id);
        return sendOkResponse(foundMentor);
    }

    @PostMapping
    public ResponseEntity<Mentor> createMentor(@Valid @RequestBody MentorDTO mentorDTO){

        Mentor mentor = modelMapper.map(mentorDTO,Mentor.class);

        Mentor createdMentor = mentorService.createMentor(mentor);

        return sendCreatedResponse(createdMentor);
    }

    @PutMapping("{id}")
    public ResponseEntity<Mentor> updateMentorById(@PathVariable Long id,@Valid @RequestBody MentorDTO mentorDTO){

        Mentor mentor = modelMapper.map(mentorDTO,Mentor.class);
        Mentor updatedMentor = mentorService.updateMentorById(id,mentor);

        return sendOkResponse(updatedMentor);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Mentor> deleteMentor(@PathVariable Long id){
        mentorService.deleteMentor(id);
        return sendNoContentResponse();
    }

}

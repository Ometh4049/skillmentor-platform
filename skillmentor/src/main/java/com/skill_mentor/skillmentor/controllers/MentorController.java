package com.skill_mentor.skillmentor.controllers;

import com.skill_mentor.skillmentor.dto.MentorDTO;
import com.skill_mentor.skillmentor.dto.SubjectDTO;
import com.skill_mentor.skillmentor.entities.Mentor;
import com.skill_mentor.skillmentor.services.MentorService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path= "api/v1/mentors")
public class MentorController extends AbstractController{

    private final ModelMapper modelMapper;

    private final MentorService mentorService;

    public MentorController(ModelMapper modelMapper, MentorService mentorService) {
        this.modelMapper = modelMapper;
        this.mentorService = mentorService;
    }

    @GetMapping
    public List<Mentor> getAllMentors(){
        return mentorService.getAllMentors();
    }

    @GetMapping("{id}")
    public Mentor getMentorById(@PathVariable Long id){
        return mentorService.getMentorById(id);
    }

    @PostMapping
    public Mentor createMentor(@Valid @RequestBody MentorDTO mentorDTO){

        Mentor mentor = modelMapper.map(mentorDTO,Mentor.class);

        return mentorService.createMentor(mentor);
    }

    @PutMapping("{id}")
    public Mentor updateMentor(@PathVariable Long id,@Valid @RequestBody MentorDTO mentorDTO){

        Mentor updatedMentor = modelMapper.map(mentorDTO,Mentor.class);
        return mentorService.updateMentor(id,updatedMentor);
    }

    @DeleteMapping("{id}")
    public void deleteMentor(@PathVariable Long id){
        mentorService.deleteMentor(id);
    }
}

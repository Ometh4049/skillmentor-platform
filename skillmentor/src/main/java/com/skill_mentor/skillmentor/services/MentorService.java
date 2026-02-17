package com.skill_mentor.skillmentor.services;

import com.skill_mentor.skillmentor.entities.Mentor;
import com.skill_mentor.skillmentor.exception.SkillMentorException;
import com.skill_mentor.skillmentor.repositories.MentorRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MentorService {

    private final MentorRepository mentorRepository;
    private final ModelMapper modelMapper;

    public List<Mentor> getAllMentors(){
        return mentorRepository.findAll();
    }

    public Mentor getMentorById(Long id){

        try {
            return mentorRepository.findById(id).get();
        } catch (Exception exception) {
            System.err.println("Error getting Mentor " + exception.getMessage());
            throw new SkillMentorException("Failed to get mentor", HttpStatus.NOT_FOUND);
        }


    }

    public Mentor createMentor(Mentor mentor){

        try {
            return mentorRepository.save(mentor);
        } catch (Exception exception) {
            System.err.println("Error creating Mentor " + exception.getMessage());
            throw new SkillMentorException("Failed to Create new mentor", HttpStatus.CONFLICT);
        }

    }

    public Mentor updateMentor(Long id, Mentor mentor){

        try {
            Mentor existingMentor = mentorRepository.findById(id).get();

            modelMapper.map(mentor,existingMentor);

            return mentorRepository.save(existingMentor);

        }catch (Exception exception){
            System.err.println("Error Update Mentor " + exception.getMessage());
            throw new SkillMentorException("Failed to update mentor", HttpStatus.NOT_FOUND);
        }



    }

    public void deleteMentor(Long id){
//        Mentor mentor = mentorRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("Mentor not found"));

        try {
            Mentor existingMentor = mentorRepository.findById(id).get();
            mentorRepository.delete(existingMentor);
        } catch (Exception exception) {
            System.err.println("Error delete Mentor " + exception.getMessage());
            throw new SkillMentorException("Failed to delete mentor", HttpStatus.NOT_FOUND);
        }

    }
}

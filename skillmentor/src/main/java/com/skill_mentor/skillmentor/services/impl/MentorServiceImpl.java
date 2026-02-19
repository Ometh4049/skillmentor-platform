package com.skill_mentor.skillmentor.services.impl;

import com.skill_mentor.skillmentor.entities.Mentor;
import com.skill_mentor.skillmentor.exception.SkillMentorException;
import com.skill_mentor.skillmentor.repositories.MentorRepository;
import com.skill_mentor.skillmentor.services.MentorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MentorServiceImpl implements MentorService {

    private final MentorRepository mentorRepository;
    private final ModelMapper modelMapper;

    public Page<Mentor> getAllMentors(String name , Pageable pageable){
        try {
            if (name != null &&  !name.isEmpty() && !name.equalsIgnoreCase("all")) {
                return mentorRepository.findByName(name, pageable);
            }
            return mentorRepository.findAll(pageable);
        } catch (Exception exception) {
            log.error("Failed to get all mentors", exception);
            throw new SkillMentorException("Failed to get all mentors", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public Mentor getMentorById(Long id){

        try {

            Mentor mentor = mentorRepository.findById(id).orElseThrow(
                    ()->new SkillMentorException("Mentor Not Found",HttpStatus.NOT_FOUND)
            );
            log.info("Successfully fetched mentor {}", id);
            return mentor;
        } catch (SkillMentorException skillMentorException) {
            log.error("Mentor Not Found " , skillMentorException);
            throw new SkillMentorException("Mentor Not Found", HttpStatus.NOT_FOUND);
        } catch (Exception exception) {
            log.error("Error getting Mentor " , exception);
            throw new SkillMentorException("Failed to get mentor", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public Mentor createMentor(Mentor mentor){

        try {
            return mentorRepository.save(mentor);
        } catch (DataIntegrityViolationException e) {
            log.error("Data integrity violation while creating mentor: {}", e.getMessage());
            throw new SkillMentorException("Mentor with this email already exists", HttpStatus.CONFLICT);
        }
        catch (Exception exception) {
            log.error("Failed to create new mentor", exception);
            throw new SkillMentorException("Failed to Create new mentor", HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    public Mentor updateMentorById(Long id, Mentor mentor){

        try {
            Mentor existingMentor = mentorRepository.findById(id).orElseThrow(
                    ()->new SkillMentorException("Mentor Not Found",HttpStatus.NOT_FOUND));

            modelMapper.map(mentor,existingMentor);
            return mentorRepository.save(existingMentor);

        } catch (SkillMentorException skillMentorException) {
            log.warn("Mentor not found with id: {} to update", id, skillMentorException);
            throw new SkillMentorException("Mentor Not Found", HttpStatus.NOT_FOUND);
        }
        catch (Exception exception){
            log.error("Error Update Mentor ", exception);
            throw new SkillMentorException("Failed to update mentor", HttpStatus.INTERNAL_SERVER_ERROR);
        }



    }

    public void deleteMentor(Long id){
//        Mentor mentor = mentorRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("Mentor not found"));

        try {
            Mentor existingMentor = mentorRepository.findById(id).orElseThrow(
                    ()->new SkillMentorException("Mentor Not Found",HttpStatus.NOT_FOUND));
            mentorRepository.delete(existingMentor);
        }catch (SkillMentorException skillMentorException) {
            log.warn("Mentor not found with id: {} to delete", id, skillMentorException);
            throw new SkillMentorException("Mentor Not Found", HttpStatus.NOT_FOUND);
        }catch (Exception exception) {
            log.error("Error delete Mentor " , exception);
            throw new SkillMentorException("Failed to delete mentor", HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }
}

package com.skill_mentor.skillmentor.services.impl;

import com.skill_mentor.skillmentor.entities.Mentor;
import com.skill_mentor.skillmentor.entities.Subject;
import com.skill_mentor.skillmentor.exception.SkillMentorException;
import com.skill_mentor.skillmentor.repositories.MentorRepository;
import com.skill_mentor.skillmentor.repositories.SubjectRepository;
import com.skill_mentor.skillmentor.services.SubjectService;
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
public class SubjectServiceImpl implements SubjectService {

    private final SubjectRepository subjectRepository;
    private final ModelMapper modelMapper;
    private final MentorRepository mentorRepository;


    public Page<Subject> getAllSubjects(String name , Pageable pageable){

        try {
            if (name != null && !name.equalsIgnoreCase("all") && !name.isEmpty()) {
                return subjectRepository.findByName(name, pageable);
            }
            return subjectRepository.findAll(pageable);
        } catch (Exception exception) {
            log.error("Failed to get all subjects", exception);
            throw new SkillMentorException("Failed to get all subjects", HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    public Subject createSubject(Long MentorId, Subject subject){
        try {

            Mentor mentor = mentorRepository.findById(MentorId)
                    .orElseThrow(()-> new SkillMentorException("Error find Mentor", HttpStatus.NOT_FOUND)
            );
            subject.setMentor(mentor);
            return subjectRepository.save(subject);
        } catch (SkillMentorException skillMentorException) {
            log.error("Mentor Not Found " , skillMentorException);
            throw new SkillMentorException("Mentor Not Found", HttpStatus.NOT_FOUND);
        }catch (DataIntegrityViolationException exception) {
            log.error("Data integrity violation while adding subject: {}", exception.getMessage());
            throw new SkillMentorException("Subject already exists or database constraint violation", HttpStatus.CONFLICT);
        }catch (Exception exception) {
            log.error("Error Creating Subject", exception);
            throw new SkillMentorException("Error Creating Subject",HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public Subject getSubjectsById(Long id){

        try {
            return subjectRepository.findById(id)
                    .orElseThrow(() -> new SkillMentorException("Subject not found",HttpStatus.NOT_FOUND));
        }catch (SkillMentorException exception){
            log.warn("Subject Not Found " , exception);
            throw new SkillMentorException("Subject Not Found", HttpStatus.NOT_FOUND);
        }catch (Exception exception) {
            log.error("Error Getting Subject", exception);
            throw new SkillMentorException("Subject Not Found",HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    public Subject updateSubjectById(Long id , Subject updatedSubject){

        try {
            Subject existingSubject = subjectRepository.findById(id)
                    .orElseThrow(() ->new SkillMentorException("Subject Not Found",HttpStatus.NOT_FOUND)
            );

            modelMapper.map(updatedSubject,existingSubject);
            return subjectRepository.save(existingSubject);

        } catch (SkillMentorException exception){
            log.warn("Subject Not Found " , exception);
            throw new SkillMentorException("Subject Not Found", HttpStatus.NOT_FOUND);
        } catch (DataIntegrityViolationException exception) {
            log.error("Data integrity violation while updating subject: {}", exception.getMessage());
            throw new SkillMentorException("Database constraint violation", HttpStatus.CONFLICT);
        } catch (Exception exception) {
            log.error("Error updating subject", exception);
            throw new SkillMentorException("Error update Subject",HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public void deleteSubject(Long id){

        try {
            subjectRepository.deleteById(id);
        } catch (Exception exception) {
            log.error("Failed to delete subject with id {}", id, exception);
            throw new SkillMentorException("Failed to delete subject", HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }
}

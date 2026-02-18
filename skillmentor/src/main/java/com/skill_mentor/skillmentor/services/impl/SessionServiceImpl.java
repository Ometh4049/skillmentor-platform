package com.skill_mentor.skillmentor.services.impl;

import com.skill_mentor.skillmentor.dto.SessionDTO;
import com.skill_mentor.skillmentor.entities.Mentor;
import com.skill_mentor.skillmentor.entities.Session;
import com.skill_mentor.skillmentor.entities.Student;
import com.skill_mentor.skillmentor.entities.Subject;
import com.skill_mentor.skillmentor.exception.SkillMentorException;
import com.skill_mentor.skillmentor.repositories.MentorRepository;
import com.skill_mentor.skillmentor.repositories.SessionRepository;
import com.skill_mentor.skillmentor.repositories.StudentRepository;
import com.skill_mentor.skillmentor.repositories.SubjectRepository;
import com.skill_mentor.skillmentor.services.SessionService;
import com.skill_mentor.skillmentor.utils.ValidationUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SessionServiceImpl implements SessionService {

    private final ModelMapper modelMapper;
    private final SessionRepository sessionRepository;
    private final StudentRepository studentRepository;
    private final MentorRepository mentorRepository;
    private final SubjectRepository subjectRepository;


    public Session createNewSession(SessionDTO sessionDTO){

        try {
            Student student = studentRepository.findById(sessionDTO.getStudentId()).orElseThrow(
                    () -> new SkillMentorException("Student not found", HttpStatus.NOT_FOUND)
            );
            Mentor mentor = mentorRepository.findById(sessionDTO.getMentorId()).orElseThrow(
                    () -> new SkillMentorException("Mentor not found", HttpStatus.NOT_FOUND)
            );
            Subject subject = subjectRepository.findById(sessionDTO.getSubjectId()).orElseThrow(
                    () -> new SkillMentorException("Subject not found", HttpStatus.NOT_FOUND)
            );

            // Checking availability
            ValidationUtils.validateMentorAvailability(mentor, sessionDTO.getSessionAt(), sessionDTO.getDurationMinutes());
            ValidationUtils.validateStudentAvailability(student, sessionDTO.getSessionAt(), sessionDTO.getDurationMinutes());

            Session session = modelMapper.map(sessionDTO, Session.class);
            session.setStudent(student);
            session.setMentor(mentor);
            session.setSubject(subject);

            return sessionRepository.save(session);

        } catch (SkillMentorException skillMentorException) {
            log.error("Dependencies not found to map: {}, Failed to create new session", skillMentorException.getMessage());
            throw skillMentorException;
        } catch (Exception exception) {
            log.error("Failed to create session", exception);
            throw new SkillMentorException("Failed to create new session", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public Page<Session> getAllSessions(Pageable pageable){

        try {
            return sessionRepository.findAll(pageable);
        } catch (Exception exception) {
            log.error("Failed to get all sessions", exception);
            throw new SkillMentorException("Failed to get all sessions", HttpStatus.INTERNAL_SERVER_ERROR);
        }


    }

    public Session getSessionById(Long id){

        try {
            Session session = sessionRepository.findById(id)
                    .orElseThrow(()->new SkillMentorException("Session Not Found" , HttpStatus.NOT_FOUND)
            );
            log.info("Successfully fetched Session for Id {}" , id);
            return session;
        } catch (SkillMentorException skillMentorException) {
            log.error("Session Not Found with Id {} " , id, skillMentorException);
            throw new SkillMentorException("Session Not Found", HttpStatus.NOT_FOUND);
        } catch (Exception exception) {
            log.error("Error getting Session " , exception);
            throw new SkillMentorException("Failed to get session", HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    public Session updateSessionById(Long id, SessionDTO updatedSessionDTO){
        try {

            Session session = sessionRepository.findById(id).orElseThrow(
                    ()->new SkillMentorException("Session Not Found",HttpStatus.NOT_FOUND));

            modelMapper.map(updatedSessionDTO, session);

            // Update the related entities
            if (updatedSessionDTO.getStudentId() != null) {
                Student student = studentRepository.findById(updatedSessionDTO.getStudentId())
                        .orElseThrow(() -> new SkillMentorException("Student not found", HttpStatus.NOT_FOUND));
                session.setStudent(student);
            }
            if (updatedSessionDTO.getMentorId() != null) {
                Mentor mentor = mentorRepository.findById(updatedSessionDTO.getMentorId())
                        .orElseThrow(() -> new SkillMentorException("Student not found", HttpStatus.NOT_FOUND));
                session.setMentor(mentor);
            }
            if (updatedSessionDTO.getSubjectId() != null) {
                Subject subject = subjectRepository.findById(updatedSessionDTO.getSubjectId())
                        .orElseThrow(() -> new SkillMentorException("Student not found", HttpStatus.NOT_FOUND));
                session.setSubject(subject);
            }

            return sessionRepository.save(session);
        }catch (SkillMentorException skillMentorException) {
            log.warn("Session not found with id: {} to update", id, skillMentorException);
            throw new SkillMentorException("Session Not Found", HttpStatus.NOT_FOUND);
        }catch (Exception exception) {
            log.error("Failed to update session", exception);
            throw new SkillMentorException("Failed to update session", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public void deleteSession(Long id)  {
        try {
            Session existingSession = sessionRepository.findById(id).orElseThrow(
                    ()->new SkillMentorException("Session Not Found",HttpStatus.NOT_FOUND));
            sessionRepository.delete(existingSession);
        }catch (SkillMentorException skillMentorException) {
            log.warn("Session not found with id: {} to delete", id, skillMentorException);
            throw new SkillMentorException("Session Not Found", HttpStatus.NOT_FOUND);
        }catch (Exception exception) {
            log.error("Error delete Session " , exception);
            throw new SkillMentorException("Failed to delete session", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

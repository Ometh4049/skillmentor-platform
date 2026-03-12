package com.stemlink.skillmentor.services.impl;

import com.stemlink.skillmentor.dto.response.MentorProfileResponseDTO;
import com.stemlink.skillmentor.dto.response.MentorReviewResponseDTO;
import com.stemlink.skillmentor.dto.response.MentorSubjectProfileResponseDTO;
import com.stemlink.skillmentor.entities.Session;
import com.stemlink.skillmentor.entities.Subject;
import com.stemlink.skillmentor.entities.Mentor;
import com.stemlink.skillmentor.exceptions.SkillMentorException;
import com.stemlink.skillmentor.respositories.MentorRepository;
import com.stemlink.skillmentor.respositories.SessionRepository;
import com.stemlink.skillmentor.services.MentorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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
    private final SessionRepository sessionRepository;
    private final ModelMapper modelMapper;

    @CacheEvict(value = "mentors", allEntries = true)
    public Mentor createNewMentor(Mentor mentor) {
        try {
            return mentorRepository.save(mentor);
        } catch (DataIntegrityViolationException e) {
            log.error("Data integrity violation while creating mentor: {}", e.getMessage());
            throw new SkillMentorException("Mentor with this email already exists", HttpStatus.CONFLICT);
        } catch (Exception exception) {
            log.error("Failed to create new mentor", exception);
            throw new SkillMentorException("Failed to create new mentor", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Cacheable(value = "mentors", key = "(#name ?: '') + '_' + #pageable.pageNumber + '_' + #pageable.pageSize")
    public Page<Mentor> getAllMentors(String name, Pageable pageable) {
        try {
            log.debug("getting mentors with name: {}", name);
            if (name != null && !name.isEmpty()) {
                return mentorRepository.findByName(name, pageable);
            }
            return mentorRepository.findAll(pageable); // SELECT * FROM mentor
        } catch (Exception exception) {
            log.error("Failed to get all mentors", exception);
            throw new SkillMentorException("Failed to get all mentors", HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @Cacheable(value = "mentors", key = "#id")
    public Mentor getMentorById(Long id) {
        try {

            Mentor mentor = mentorRepository.findById(id).orElseThrow(
                    () -> new SkillMentorException("Mentor Not found", HttpStatus.NOT_FOUND)
            );
            log.info("Successfully fetched mentor {}", id);
            return mentor;
        } catch (SkillMentorException skillMentorException) {
            //System.err.println("Mentor not found " + skillMentorException.getMessage());
            // LOG LEVELS
            // DEBUG, INFO, WARN, ERROR
            // env - dev, prod
            log.warn("Mentor not found with id: {} to fetch", id, skillMentorException);
            throw new SkillMentorException("Mentor Not found", HttpStatus.NOT_FOUND);
        } catch (Exception exception) {
            log.error("Error getting mentor", exception);
            throw new SkillMentorException("Failed to get mentor", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public MentorProfileResponseDTO getMentorProfileByMentorId(String mentorId) {
        Mentor mentor = mentorRepository.findByMentorId(mentorId)
                .orElseThrow(() -> new SkillMentorException("Mentor not found", HttpStatus.NOT_FOUND));

        MentorProfileResponseDTO profile = modelMapper.map(mentor, MentorProfileResponseDTO.class);

        long sessionCount = sessionRepository.countByMentor_Id(mentor.getId());
        long reviewCount = sessionRepository.countByMentor_IdAndStudentRatingIsNotNull(mentor.getId());
        Double averageRating = sessionRepository.findAverageRatingByMentorId(mentor.getId());

        profile.setSessionsCount(sessionCount);
        profile.setReviewsCount(reviewCount);
        profile.setAverageRating(averageRating != null ? Math.round(averageRating * 10.0) / 10.0 : null);
        profile.setSubjectsCount(mentor.getSubjects() != null ? mentor.getSubjects().size() : 0);

        if (mentor.getSubjects() != null) {
            profile.setSubjects(mentor.getSubjects().stream().map(subject -> toSubjectProfile(subject)).toList());
        }

        profile.setReviews(sessionRepository
                .findTop10ByMentor_IdAndStudentReviewIsNotNullAndStudentRatingIsNotNullOrderBySessionAtDesc(mentor.getId())
                .stream()
                .map(this::toReview)
                .toList());

        return profile;
    }

    @CacheEvict(value = "mentors", allEntries = true)
    public Mentor updateMentorById(Long id, Mentor updatedMentor) {
        try {
            Mentor mentor = mentorRepository.findById(id).orElseThrow(
                    () -> new SkillMentorException("Mentor Not found", HttpStatus.NOT_FOUND)
            );
            modelMapper.map(updatedMentor, mentor);
            return mentorRepository.save(mentor);
        } catch (SkillMentorException skillMentorException) {
            log.warn("Mentor not found with id: {} to update", id, skillMentorException);
            throw new SkillMentorException("Mentor Not found", HttpStatus.NOT_FOUND);
        } catch (Exception exception) {
            log.error("Error updating mentor", exception);
            throw new SkillMentorException("Failed to update mentor", HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    public void deleteMentor(Long id) {
        try {
            mentorRepository.deleteById(id);
        } catch (Exception exception) {
            log.error("Failed to delete mentor with id {}", id, exception);
            throw new SkillMentorException("Failed to delete mentor", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private MentorSubjectProfileResponseDTO toSubjectProfile(Subject subject) {
        MentorSubjectProfileResponseDTO dto = new MentorSubjectProfileResponseDTO();
        dto.setId(subject.getId());
        dto.setSubjectName(subject.getSubjectName());
        dto.setDescription(subject.getDescription());
        dto.setCourseImageUrl(subject.getCourseImageUrl());
        dto.setEnrollmentCount(sessionRepository.countBySubject_Id(subject.getId()));
        return dto;
    }

    private MentorReviewResponseDTO toReview(Session session) {
        MentorReviewResponseDTO dto = new MentorReviewResponseDTO();
        dto.setSessionId(session.getId());
        dto.setStudentName(session.getStudent().getFirstName() + " " + session.getStudent().getLastName());
        dto.setRating(session.getStudentRating());
        dto.setReview(session.getStudentReview());
        dto.setSessionAt(session.getSessionAt());
        return dto;
    }

}

package com.skill_mentor.skillmentor.services;

import com.skill_mentor.skillmentor.entities.Mentor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MentorService {

    Page<Mentor> getAllMentors(String name, Pageable pageable);

    Mentor getMentorById(Long id);

    Mentor createMentor(Mentor mentor);

    Mentor updateMentorById(Long id, Mentor mentor);

    void deleteMentor(Long id);
}

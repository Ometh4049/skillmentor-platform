package com.skill_mentor.skillmentor.services;

import com.skill_mentor.skillmentor.entities.Mentor;

import java.util.List;

public interface MentorService {

    List<Mentor> getAllMentors();

    Mentor getMentorById(Long id);

    Mentor createMentor(Mentor mentor);

    Mentor updateMentorById(Long id, Mentor mentor);

    void deleteMentor(Long id);
}

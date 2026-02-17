package com.skill_mentor.skillmentor.services;

import com.skill_mentor.skillmentor.entities.Mentor;

import java.util.List;

public interface MentorService {

    public List<Mentor> getAllMentors();

    public Mentor getMentorById(Long id);

    public Mentor createMentor(Mentor mentor);

    public Mentor updateMentorById(Long id, Mentor mentor);

    public void deleteMentor(Long id);
}

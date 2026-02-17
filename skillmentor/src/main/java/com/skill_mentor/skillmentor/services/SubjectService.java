package com.skill_mentor.skillmentor.services;

import com.skill_mentor.skillmentor.entities.Subject;

import java.util.List;

public interface SubjectService {


    List<Subject> getAllSubjects();

    Subject createSubject(Subject subject);

    Subject getSubjectsById(Long id);

    Subject updateSubject(Long id , Subject subject);

    void deleteSubject(Long id);
}

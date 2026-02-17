package com.skill_mentor.skillmentor.services;

import com.skill_mentor.skillmentor.entities.Subject;

import java.util.List;

public interface SubjectService {

    public List<Subject> getAllSubjects();

    public Subject createSubject(Subject subject);

    public Subject getSubjectsById(Long id);

    public Subject updateSubject(Long id , Subject subject);

    public void deleteSubject(Long id);
}

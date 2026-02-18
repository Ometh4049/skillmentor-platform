package com.skill_mentor.skillmentor.services;

import com.skill_mentor.skillmentor.entities.Subject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface SubjectService {


    Page<Subject> getAllSubjects(String name,Pageable pageable);

    Subject createSubject(Long MentorId, Subject subject);

    Subject getSubjectsById(Long id);

    Subject updateSubjectById(Long id , Subject subject);

    void deleteSubject(Long id);
}

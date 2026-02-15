package com.skill_mentor.skillmentor.services;

import com.skill_mentor.skillmentor.entities.Subject;
import com.skill_mentor.skillmentor.repositories.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor

public class SubjectService {

    private final SubjectRepository subjectRepository;


    public List<Subject> getAllSubjects(){
        return subjectRepository.findAll();
    }

    public Subject createSubject(Subject subject){
        return subjectRepository.save(subject);

    }

    public Subject getSubjectsById(Long id){
        return subjectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subject not found"));
    }

    public Subject updateSubject(Long id , Subject subject){

        Subject existingSubject = subjectRepository.findById(id)
                .orElseThrow(() ->new RuntimeException("Subject Not Found"));

        existingSubject.setSubjectName(subject.getSubjectName());
        existingSubject.setDescription(subject.getDescription());

        return subjectRepository.save(existingSubject);
    }

    public void deleteSubject(Long id){
        subjectRepository.deleteById(id);
    }
}

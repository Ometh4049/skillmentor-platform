package com.skill_mentor.skillmentor.services.impl;

import com.skill_mentor.skillmentor.entities.Subject;
import com.skill_mentor.skillmentor.repositories.SubjectRepository;
import com.skill_mentor.skillmentor.services.SubjectService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubjectServiceImpl implements SubjectService {

    private final SubjectRepository subjectRepository;
    private final ModelMapper modelMapper;


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

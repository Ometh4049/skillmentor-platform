package com.skill_mentor.skillmentor.services;

import com.skill_mentor.skillmentor.entities.Subject;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SubjectService {

    private final List<Subject> subjects = new ArrayList<>((
            List.of(new Subject("Maths" , "MT 001"),
                    new Subject("Computer Science" , "CS 001"))
    ));

    public List<Subject> getAllSubjects(){
        return subjects;
    }

    public List<Subject> createSubject(Subject subject){
        subjects.add(subject);
        return subjects;
    }

    public Subject getSubjectsById(int id){
        return subjects.get(id);
    }

    public Subject updateSubject(int id , Subject subject){
        subjects.set(id,subject);
        return subject;
    }

    public List<Subject> deleteSubject(int id){
        subjects.remove(id);
        return subjects;
    }
}

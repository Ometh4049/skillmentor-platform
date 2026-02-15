package com.skill_mentor.skillmentor.repositories;

import com.skill_mentor.skillmentor.entities.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long > {

}

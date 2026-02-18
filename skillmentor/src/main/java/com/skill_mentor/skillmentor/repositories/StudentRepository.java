package com.skill_mentor.skillmentor.repositories;

import com.skill_mentor.skillmentor.entities.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepository extends JpaRepository<Student,Long> {


    @Query()
    Page<Student> findByName(String name, Pageable pageable);
}

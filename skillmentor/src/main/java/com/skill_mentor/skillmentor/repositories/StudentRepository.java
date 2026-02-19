package com.skill_mentor.skillmentor.repositories;

import com.skill_mentor.skillmentor.entities.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepository extends JpaRepository<Student,Long> {


    @Query("""
       SELECT s FROM Student s
       WHERE LOWER(s.firstName) LIKE LOWER(CONCAT('%', :name, '%'))
       OR LOWER(s.lastName) LIKE LOWER(CONCAT('%', :name, '%'))
       """)
    Page<Student> findByName(@Param("name") String name, Pageable pageable);

}

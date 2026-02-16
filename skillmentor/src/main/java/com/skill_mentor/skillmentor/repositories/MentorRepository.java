package com.skill_mentor.skillmentor.repositories;

import com.skill_mentor.skillmentor.entities.Mentor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MentorRepository extends JpaRepository<Mentor, Long> {
}

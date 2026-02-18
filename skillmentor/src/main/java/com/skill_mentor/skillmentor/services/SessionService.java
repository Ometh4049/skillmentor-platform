package com.skill_mentor.skillmentor.services;

import com.skill_mentor.skillmentor.dto.SessionDTO;
import com.skill_mentor.skillmentor.entities.Session;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SessionService {

    Session createNewSession(SessionDTO sessionDTO);

    Page<Session> getAllSessions(Pageable pageable);

    Session getSessionById(Long id);

    Session updateSessionById(Long id, SessionDTO updatedSessionDTO);

    void deleteSession(Long id);

}

package com.skill_mentor.skillmentor.services.impl;

import com.skill_mentor.skillmentor.dto.SessionDTO;
import com.skill_mentor.skillmentor.entities.Session;
import com.skill_mentor.skillmentor.repositories.SessionRepository;
import com.skill_mentor.skillmentor.services.SessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SessionServiceImpl implements SessionService {

    private final ModelMapper modelMapper;
    private final SessionRepository sessionRepository;

    public Session createNewSession(Session session){

    }

    public List<Session> getAllSessions(){

    }

    public Session getSessionById(Long id){

    }

    public Session updateSessionById(Long id, Session session){

    }

    public void deleteSession(Long id){

    }
}

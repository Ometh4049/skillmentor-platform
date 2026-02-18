package com.skill_mentor.skillmentor.controllers;

import com.skill_mentor.skillmentor.dto.SessionDTO;
import com.skill_mentor.skillmentor.entities.Session;
import com.skill_mentor.skillmentor.services.SessionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(path = "/api/v1/sessions")
@RequiredArgsConstructor
@Validated
public class SessionController extends AbstractController {

    private final SessionService sessionService;
    private final ModelMapper modelMapper;

    @GetMapping
    public ResponseEntity<Page<Session>> getAlSessions(Pageable pageable) {
        Page<Session> sessions = sessionService.getAllSessions(pageable);
        return sendOkResponse(sessions);
    }

    @GetMapping("{id}")
    public ResponseEntity<Session> getSessionById(@PathVariable Long id) {
        Session foundSession = sessionService.getSessionById(id);
        return sendOkResponse(foundSession);
    }

    @PostMapping
    public ResponseEntity<Session> createSession(@Valid @RequestBody SessionDTO sessionDTO) {

        Session session = modelMapper.map(sessionDTO, Session.class);

        Session createdSession = sessionService.createNewSession(session);

        return sendCreatedResponse(createdSession);
    }

    @PutMapping("{id}")
    public ResponseEntity<Session> updateSessionById(@PathVariable Long id, @Valid @RequestBody SessionDTO sessionDTO) {

        Session session = modelMapper.map(sessionDTO, Session.class);
        Session updatedSession = sessionService.updateSessionById(id, session);

        return sendOkResponse(updatedSession);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Session> deleteSession(@PathVariable Long id) {
        sessionService.deleteSession(id);
        return sendNoContentResponse();
    }
}

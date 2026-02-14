package com.skill_mentor.skillmentor.controllers;

import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(path= "api/v1/subjects")
public class SubjectController {

    private final List<String> subjects = new ArrayList<>((
            List.of("Maths","Physics","Chemistry")
    ));

    @GetMapping
    public String getAllSubjects(@RequestParam(name="name" , defaultValue = "all") String name){
        return subjects.toString();
    }

//    including path parameter & query parameter
    @GetMapping("{id}")
    public String getAllSubjectsById(@PathVariable int id){
        return subjects.get(id);
    }

    @PostMapping
    public String createSubject(@RequestBody String subject){
        subjects.add(subject);
        System.out.println("Subject created ");
        return subjects.toString();
    }

    @PutMapping("{id}")
    public String updateSubject(@PathVariable int id,@RequestBody String subject){
        subjects.set(id ,subject);
        return "subject updated";
    }

    @DeleteMapping("{id}")
    public String deleteSubject(@PathVariable int id){
        subjects.remove(id);

        return subjects.toString();
    }
}

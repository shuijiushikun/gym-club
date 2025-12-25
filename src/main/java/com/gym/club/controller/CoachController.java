package com.gym.club.controller;

import com.gym.club.entity.Coach;
import com.gym.club.service.CoachService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/coach")
public class CoachController {
    @Autowired
    private CoachService coachService;

    @PostMapping("/add")
    public Coach addCoach(@RequestBody Coach coach) {
        return coachService.addCoach(coach);
    }

    @GetMapping("/{id}")
    public Coach getById(@PathVariable Integer id) {
        return coachService.getById(id);
    }

    @GetMapping("/all")
    public List<Coach> getAll() {
        return coachService.getAll();
    }

    @PutMapping("/update")
    public Coach updateCoach(@RequestBody Coach coach) {
        return coachService.updateCoach(coach);
    }

    @DeleteMapping("/delete/{id}")
    public boolean deleteById(@PathVariable Integer id) {
        return coachService.deleteById(id);
    }

    @GetMapping("/{id}/students")
    public List<com.gym.club.entity.Member> getStudents(@PathVariable Integer id) {
        return coachService.getStudents(id);
    }
}
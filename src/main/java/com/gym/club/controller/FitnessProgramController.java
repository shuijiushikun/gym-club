package com.gym.club.controller;

import com.gym.club.entity.FitnessProgram;
import com.gym.club.service.FitnessProgramService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/fitness-program")
public class FitnessProgramController {
    @Autowired
    private FitnessProgramService fitnessProgramService;

    @PostMapping("/add")
    public FitnessProgram addProgram(@RequestBody FitnessProgram program) {
        return fitnessProgramService.addProgram(program);
    }

    @GetMapping("/{id}")
    public FitnessProgram getById(@PathVariable Integer id) {
        return fitnessProgramService.getById(id);
    }

    @GetMapping("/all")
    public List<FitnessProgram> getAll() {
        return fitnessProgramService.getAll();
    }

    @GetMapping("/type/{type}")
    public List<FitnessProgram> getByType(@PathVariable Integer type) {
        return fitnessProgramService.getByType(type);
    }

    @GetMapping("/coach/{coachId}")
    public List<FitnessProgram> getByCoachId(@PathVariable Integer coachId) {
        return fitnessProgramService.getByCoachId(coachId);
    }

    @GetMapping("/status/{status}")
    public List<FitnessProgram> getByStatus(@PathVariable Integer status) {
        return fitnessProgramService.getByStatus(status);
    }

    @PutMapping("/update")
    public FitnessProgram updateProgram(@RequestBody FitnessProgram program) {
        return fitnessProgramService.updateProgram(program);
    }

    @DeleteMapping("/delete/{id}")
    public boolean deleteById(@PathVariable Integer id) {
        return fitnessProgramService.deleteById(id);
    }
}
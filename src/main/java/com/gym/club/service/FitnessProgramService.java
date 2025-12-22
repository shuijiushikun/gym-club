package com.gym.club.service;

import com.gym.club.entity.FitnessProgram;
import com.gym.club.mapper.FitnessProgramMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class FitnessProgramService {
    @Autowired
    private FitnessProgramMapper fitnessProgramMapper;

    public FitnessProgram addProgram(FitnessProgram program) {
        fitnessProgramMapper.insert(program);
        return fitnessProgramMapper.selectById(program.getId());
    }

    public FitnessProgram getById(Integer id) {
        return fitnessProgramMapper.selectById(id);
    }

    public List<FitnessProgram> getAll() {
        return fitnessProgramMapper.selectAll();
    }

    public List<FitnessProgram> getByType(Integer type) {
        return fitnessProgramMapper.selectByType(type);
    }

    public List<FitnessProgram> getByCoachId(Integer coachId) {
        return fitnessProgramMapper.selectByCoachId(coachId);
    }

    public List<FitnessProgram> getByStatus(Integer status) {
        return fitnessProgramMapper.selectByStatus(status);
    }

    public FitnessProgram updateProgram(FitnessProgram program) {
        fitnessProgramMapper.update(program);
        return fitnessProgramMapper.selectById(program.getId());
    }

    public boolean deleteById(Integer id) {
        return fitnessProgramMapper.deleteById(id) > 0;
    }
}
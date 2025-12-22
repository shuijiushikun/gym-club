package com.gym.club.mapper;

import com.gym.club.entity.FitnessProgram;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface FitnessProgramMapper {
    int insert(FitnessProgram program);

    FitnessProgram selectById(Integer id);

    List<FitnessProgram> selectAll();

    List<FitnessProgram> selectByType(Integer type);

    List<FitnessProgram> selectByCoachId(Integer coachId);

    List<FitnessProgram> selectByStatus(Integer status);

    int update(FitnessProgram program);

    int deleteById(Integer id);
}
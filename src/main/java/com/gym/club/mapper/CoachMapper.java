package com.gym.club.mapper;

import com.gym.club.entity.Coach;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface CoachMapper {
    int insert(Coach coach);

    Coach selectById(Integer id);

    Coach selectByUsername(String username);

    List<Coach> selectAll();

    int update(Coach coach);

    int deleteById(Integer id);
}
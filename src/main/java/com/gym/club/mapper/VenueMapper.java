package com.gym.club.mapper;

import com.gym.club.entity.Venue;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface VenueMapper {
    int insert(Venue venue);

    Venue selectById(Integer id);

    Venue selectByName(String name);

    List<Venue> selectAll();

    List<Venue> selectByType(Integer type);

    List<Venue> selectByStatus(Integer status);

    int update(Venue venue);

    int deleteById(Integer id);
}
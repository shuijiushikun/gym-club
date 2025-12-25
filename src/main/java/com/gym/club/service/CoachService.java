package com.gym.club.service;

import com.gym.club.entity.Coach;
import com.gym.club.mapper.CoachMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CoachService {
    @Autowired
    private CoachMapper coachMapper;

    @Autowired
    private com.gym.club.mapper.MemberMapper memberMapper;

    public Coach addCoach(Coach coach) {
        coachMapper.insert(coach);
        return coach;
    }

    public Coach getById(Integer id) {
        return coachMapper.selectById(id);
    }

    public List<Coach> getAll() {
        return coachMapper.selectAll();
    }

    public Coach updateCoach(Coach coach) {
        coachMapper.update(coach);
        return coach;
    }

    public boolean deleteById(Integer id) {
        return coachMapper.deleteById(id) > 0;
    }

    public List<com.gym.club.entity.Member> getStudents(Integer coachId) {
        return memberMapper.selectByCoachId(coachId);
    }
}
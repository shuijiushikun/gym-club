package com.gym.club.service;

import com.gym.club.entity.Member;
import com.gym.club.mapper.MemberMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class MemberService {
    @Autowired
    private MemberMapper memberMapper;

    @Autowired
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    public Member register(Member member) {
        member.setPassword(passwordEncoder.encode(member.getPassword()));
        memberMapper.insert(member);
        return member;
    }

    public void update(Member member) {
        memberMapper.update(member);
    }

    public void delete(Integer id) {
        memberMapper.deleteById(id);
    }

    public Member getById(Integer id) {
        return memberMapper.selectById(id);
    }

    public List<Member> getAll() {
        return memberMapper.selectAll();
    }
}
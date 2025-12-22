package com.gym.club.controller;

import com.gym.club.entity.Member;
import com.gym.club.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/member")
public class MemberController {
    @Autowired
    private MemberService memberService;

    @PostMapping("/register")
    public Member register(@RequestBody Member member) {
        return memberService.register(member);
    }

    @GetMapping("/{id}")
    public Member getById(@PathVariable Integer id) {
        return memberService.getById(id);
    }

    @GetMapping("/all")
    public List<Member> getAll() {
        return memberService.getAll();
    }
}
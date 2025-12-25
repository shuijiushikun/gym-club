package com.gym.club.controller;

import com.gym.club.entity.MemberCard;
import com.gym.club.service.MemberCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/member-card")
public class MemberCardController {
    @Autowired
    private MemberCardService memberCardService;

    @PostMapping("/create")
    public MemberCard createMemberCard(@RequestBody MemberCard memberCard) {
        return memberCardService.createMemberCard(memberCard);
    }

    @GetMapping("/{id}")
    public MemberCard getById(@PathVariable Integer id) {
        return memberCardService.getById(id);
    }

    @GetMapping("/member/{memberId}")
    public List<MemberCard> getByMemberId(@PathVariable Integer memberId) {
        return memberCardService.getByMemberId(memberId);
    }

    @GetMapping("/all")
    public List<MemberCard> getAll() {
        return memberCardService.getAll();
    }

    @GetMapping("/valid/{memberId}")
    public MemberCard getValidCardByMemberId(@PathVariable Integer memberId) {
        return memberCardService.getValidCardByMemberId(memberId);
    }

    @PostMapping("/pay/{id}")
    public boolean payCard(@PathVariable Integer id, @RequestParam BigDecimal amount) {
        return memberCardService.payCard(id, amount);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        memberCardService.delete(id);
    }
}
package com.gym.club.controller;

import com.gym.club.entity.CardType;
import com.gym.club.service.CardTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/card-type")
public class CardTypeController {
    @Autowired
    private CardTypeService cardTypeService;

    @PostMapping("/add")
    public CardType addCardType(@RequestBody CardType cardType) {
        return cardTypeService.addCardType(cardType);
    }

    @GetMapping("/{id}")
    public CardType getById(@PathVariable Integer id) {
        return cardTypeService.getById(id);
    }

    @GetMapping("/all")
    public List<CardType> getAll() {
        return cardTypeService.getAll();
    }

    @GetMapping("/status/{status}")
    public List<CardType> getByStatus(@PathVariable Integer status) {
        return cardTypeService.getByStatus(status);
    }

    @PutMapping("/update")
    public CardType updateCardType(@RequestBody CardType cardType) {
        return cardTypeService.updateCardType(cardType);
    }

    @DeleteMapping("/delete/{id}")
    public boolean deleteById(@PathVariable Integer id) {
        return cardTypeService.deleteById(id);
    }
}
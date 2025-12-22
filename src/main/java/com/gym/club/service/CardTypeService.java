package com.gym.club.service;

import com.gym.club.entity.CardType;
import com.gym.club.mapper.CardTypeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CardTypeService {
    @Autowired
    private CardTypeMapper cardTypeMapper;

    public CardType addCardType(CardType cardType) {
        cardTypeMapper.insert(cardType);
        return cardType;
    }

    public CardType getById(Integer id) {
        return cardTypeMapper.selectById(id);
    }

    public List<CardType> getAll() {
        return cardTypeMapper.selectAll();
    }

    public List<CardType> getByStatus(Integer status) {
        return cardTypeMapper.selectByStatus(status);
    }

    public CardType updateCardType(CardType cardType) {
        cardTypeMapper.update(cardType);
        return cardType;
    }

    public boolean deleteById(Integer id) {
        return cardTypeMapper.deleteById(id) > 0;
    }
}
package com.gym.club.mapper;

import com.gym.club.entity.CardType;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface CardTypeMapper {
    int insert(CardType cardType);

    CardType selectById(Integer id);

    List<CardType> selectAll();

    List<CardType> selectByStatus(Integer status); // 按状态查询

    int update(CardType cardType);

    int deleteById(Integer id);
}
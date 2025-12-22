package com.gym.club.mapper;

import com.gym.club.entity.MemberCard;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface MemberCardMapper {
    int insert(MemberCard memberCard);

    MemberCard selectById(Integer id);

    MemberCard selectByCardNumber(String cardNumber);

    List<MemberCard> selectByMemberId(Integer memberId);

    List<MemberCard> selectAll();

    int update(MemberCard memberCard);

    int deleteById(Integer id);

    int updatePaymentStatus(@Param("id") Integer id, @Param("paymentStatus") Integer paymentStatus);

    int updateCardStatus(@Param("id") Integer id, @Param("cardStatus") Integer cardStatus);

    // 查询会员的有效卡
    MemberCard selectValidCardByMemberId(Integer memberId);
}
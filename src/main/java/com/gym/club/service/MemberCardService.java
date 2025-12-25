package com.gym.club.service;

import com.gym.club.entity.CardType;
import com.gym.club.entity.Member;
import com.gym.club.entity.MemberCard;
import com.gym.club.mapper.CardTypeMapper;
import com.gym.club.mapper.MemberCardMapper;
import com.gym.club.mapper.MemberMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class MemberCardService {
    @Autowired
    private MemberCardMapper memberCardMapper;

    @Autowired
    private MemberMapper memberMapper;

    @Autowired
    private CardTypeMapper cardTypeMapper;

    // 生成唯一卡号
    private String generateCardNumber() {
        return "GYM" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    @Transactional
    public MemberCard createMemberCard(MemberCard memberCard) {
        // 验证会员是否存在
        Member member = memberMapper.selectById(memberCard.getMemberId());
        if (member == null) {
            throw new RuntimeException("会员不存在");
        }

        // Check if member already has a valid card
        MemberCard existingCard = memberCardMapper.selectValidCardByMemberId(memberCard.getMemberId());
        if (existingCard != null) {
            throw new RuntimeException("Member already has a valid card. Please wait for it to expire or delete it.");
        }

        // 验证卡类型是否存在
        CardType cardType = cardTypeMapper.selectById(memberCard.getCardTypeId());
        if (cardType == null || cardType.getStatus() != 1) {
            throw new RuntimeException("卡类型不存在或已停售");
        }

        // 生成卡号
        memberCard.setCardNumber(generateCardNumber());

        // 设置日期
        LocalDate startDate = memberCard.getStartDate() != null ? memberCard.getStartDate() : LocalDate.now();
        memberCard.setStartDate(startDate);
        memberCard.setEndDate(startDate.plusDays(cardType.getDurationDays()));
        memberCard.setRemainingDays(cardType.getDurationDays());

        // 设置金额
        memberCard.setTotalAmount(cardType.getPrice());
        if (memberCard.getPaidAmount() == null) {
            memberCard.setPaidAmount(BigDecimal.ZERO);
        }

        // 默认状态
        if (memberCard.getPaymentStatus() == null) {
            if (memberCard.getPaidAmount().compareTo(memberCard.getTotalAmount()) >= 0) {
                memberCard.setPaymentStatus(1); // 已支付
                memberCard.setCardStatus(1); // 有效
            } else {
                memberCard.setPaymentStatus(0); // 未支付
                memberCard.setCardStatus(0); // 无效 (未支付)
            }
        }
        // Fallback if manually set (should be rare/handled above)
        if (memberCard.getCardStatus() == null) {
            memberCard.setCardStatus(memberCard.getPaymentStatus() == 1 ? 1 : 0);
        }

        // 插入数据库
        memberCardMapper.insert(memberCard);
        return memberCardMapper.selectById(memberCard.getId());
    }

    public MemberCard getById(Integer id) {
        return memberCardMapper.selectById(id);
    }

    public List<MemberCard> getByMemberId(Integer memberId) {
        return memberCardMapper.selectByMemberId(memberId);
    }

    public List<MemberCard> getAll() {
        return memberCardMapper.selectAll();
    }

    public MemberCard getValidCardByMemberId(Integer memberId) {
        return memberCardMapper.selectValidCardByMemberId(memberId);
    }

    @Transactional
    public boolean payCard(Integer id, BigDecimal amount) {
        MemberCard card = memberCardMapper.selectById(id);
        if (card == null) {
            throw new RuntimeException("会员卡不存在");
        }

        BigDecimal newPaidAmount = card.getPaidAmount().add(amount);

        // 更新支付状态
        MemberCard updateCard = new MemberCard();
        updateCard.setId(id);
        updateCard.setPaidAmount(newPaidAmount);

        // 如果支付金额 >= 总金额，标记为已支付并激活
        if (newPaidAmount.compareTo(card.getTotalAmount()) >= 0) {
            // Check for existing valid card BEFORE activating this one
            MemberCard existingValid = memberCardMapper.selectValidCardByMemberId(card.getMemberId());
            if (existingValid != null && !existingValid.getId().equals(id)) {
                throw new RuntimeException("Member already has a valid card. Cannot activate another one.");
            }

            updateCard.setPaymentStatus(1); // 已支付
            updateCard.setCardStatus(1); // 激活
        }

        memberCardMapper.update(updateCard);
        return true;
    }

    @Transactional
    public boolean recharge(Integer cardId, Integer days, BigDecimal amount) {
        MemberCard card = memberCardMapper.selectById(cardId);
        if (card == null) {
            throw new RuntimeException("Card not found");
        }

        MemberCard updateCard = new MemberCard();
        updateCard.setId(cardId);

        // Extend validity
        LocalDate newEndDate = card.getEndDate().plusDays(days);
        updateCard.setEndDate(newEndDate);
        updateCard.setRemainingDays(card.getRemainingDays() + days);

        // Update financial records
        updateCard.setTotalAmount(card.getTotalAmount().add(amount));
        updateCard.setPaidAmount(card.getPaidAmount().add(amount));

        // Ensure status is valid
        if (card.getCardStatus() != 1) {
            updateCard.setCardStatus(1);
        }

        memberCardMapper.update(updateCard);
        return true;
    }

    @Transactional
    public void delete(Integer id) {
        MemberCard card = memberCardMapper.selectById(id);
        if (card == null) {
            throw new RuntimeException("Member card not found");
        }
        memberCardMapper.deleteById(id);
    }
}
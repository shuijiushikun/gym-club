package com.gym.club.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class MemberCard {
    private Integer id;
    private Integer memberId;
    private Integer cardTypeId;
    private String cardNumber;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer remainingDays;
    private BigDecimal totalAmount;
    private BigDecimal paidAmount;
    private Integer paymentStatus; // 0未支付，1已支付，2已退款
    private Integer cardStatus; // 0无效，1有效，2已过期，3已挂失
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    // 关联信息（用于查询时显示）
    private Member member;
    private CardType cardType;
}
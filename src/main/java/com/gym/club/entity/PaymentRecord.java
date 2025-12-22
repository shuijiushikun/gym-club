package com.gym.club.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PaymentRecord {
    private Integer id;
    private Integer memberId;
    private String orderNumber;
    private Integer paymentType; // 1会员卡，2课程，3私教
    private Integer relatedId; // 关联ID
    private BigDecimal amount;
    private Integer paymentMethod; // 1微信，2支付宝，3现金，4银行卡
    private Integer paymentStatus; // 0待支付，1支付成功，2支付失败，3已退款
    private String transactionId; // 第三方交易ID
    private LocalDateTime payTime;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    // 关联信息
    private Member member;
}
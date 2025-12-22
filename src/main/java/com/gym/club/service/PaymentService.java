package com.gym.club.service;

import com.gym.club.entity.MemberCard;
import com.gym.club.entity.PaymentRecord;
import com.gym.club.mapper.MemberCardMapper;
import com.gym.club.mapper.PaymentRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.UUID;

@Service
public class PaymentService {
    @Autowired
    private PaymentRecordMapper paymentRecordMapper;

    @Autowired
    private MemberCardMapper memberCardMapper;

    @Autowired
    private MemberCardService memberCardService;

    // 生成订单号
    private String generateOrderNumber() {
        return "PAY" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }

    /**
     * 创建支付订单
     */
    public PaymentRecord createPaymentOrder(PaymentRecord paymentRecord) {
        // 生成订单号
        paymentRecord.setOrderNumber(generateOrderNumber());

        // 默认状态
        if (paymentRecord.getPaymentStatus() == null) {
            paymentRecord.setPaymentStatus(0); // 待支付
        }

        paymentRecordMapper.insert(paymentRecord);
        return paymentRecordMapper.selectById(paymentRecord.getId());
    }

    /**
     * 模拟支付接口（核心功能）
     */
    @Transactional
    public PaymentRecord simulatePayment(String orderNumber, Integer paymentMethod) {
        PaymentRecord paymentRecord = paymentRecordMapper.selectByOrderNumber(orderNumber);
        if (paymentRecord == null) {
            throw new RuntimeException("订单不存在");
        }

        if (paymentRecord.getPaymentStatus() == 1) {
            throw new RuntimeException("订单已支付，请勿重复支付");
        }

        // 模拟支付成功
        String transactionId = "SIM" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8);

        // 更新支付状态
        paymentRecordMapper.updatePaymentStatus(orderNumber, 1, transactionId);

        // 如果支付类型是会员卡，更新会员卡支付状态
        if (paymentRecord.getPaymentType() == 1 && paymentRecord.getRelatedId() != null) {
            memberCardService.payCard(paymentRecord.getRelatedId(), paymentRecord.getAmount());
        }

        return paymentRecordMapper.selectByOrderNumber(orderNumber);
    }

    /**
     * 会员卡续费支付
     */
    @Transactional
    public PaymentRecord payMemberCard(Integer memberCardId, Integer paymentMethod) {
        MemberCard memberCard = memberCardMapper.selectById(memberCardId);
        if (memberCard == null) {
            throw new RuntimeException("会员卡不存在");
        }

        // 计算待支付金额
        BigDecimal remainingAmount = memberCard.getTotalAmount().subtract(memberCard.getPaidAmount());
        if (remainingAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("会员卡已支付完成");
        }

        // 创建支付记录
        PaymentRecord paymentRecord = new PaymentRecord();
        paymentRecord.setMemberId(memberCard.getMemberId());
        paymentRecord.setPaymentType(1); // 会员卡支付
        paymentRecord.setRelatedId(memberCardId);
        paymentRecord.setAmount(remainingAmount);
        paymentRecord.setPaymentMethod(paymentMethod);
        paymentRecord.setRemark("会员卡续费，卡号：" + memberCard.getCardNumber());

        return createPaymentOrder(paymentRecord);
    }

    /**
     * 获取会员支付记录
     */
    public java.util.List<PaymentRecord> getPaymentRecordsByMemberId(Integer memberId) {
        return paymentRecordMapper.selectByMemberId(memberId);
    }

    /**
     * 统计会员总消费
     */
    public BigDecimal getTotalConsumption(Integer memberId) {
        return paymentRecordMapper.sumAmountByMemberId(memberId);
    }
}
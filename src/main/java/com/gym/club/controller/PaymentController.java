package com.gym.club.controller;

import com.gym.club.entity.PaymentRecord;
import com.gym.club.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {
    @Autowired
    private PaymentService paymentService;

    /**
     * 创建支付订单
     */
    @PostMapping("/create-order")
    public PaymentRecord createPaymentOrder(@RequestBody PaymentRecord paymentRecord) {
        return paymentService.createPaymentOrder(paymentRecord);
    }

    /**
     * 模拟支付接口
     */
    @PostMapping("/simulate")
    public PaymentRecord simulatePayment(@RequestParam String orderNumber,
            @RequestParam(defaultValue = "1") Integer paymentMethod) {
        return paymentService.simulatePayment(orderNumber, paymentMethod);
    }

    /**
     * 会员卡续费支付
     */
    @PostMapping("/pay-member-card")
    public PaymentRecord payMemberCard(@RequestParam Integer memberCardId,
            @RequestParam(defaultValue = "1") Integer paymentMethod) {
        return paymentService.payMemberCard(memberCardId, paymentMethod);
    }

    /**
     * 获取会员支付记录
     */
    @GetMapping("/member/{memberId}")
    public List<PaymentRecord> getPaymentRecordsByMemberId(@PathVariable Integer memberId) {
        return paymentService.getPaymentRecordsByMemberId(memberId);
    }

    /**
     * 统计会员总消费
     */
    @GetMapping("/total-consumption/{memberId}")
    public BigDecimal getTotalConsumption(@PathVariable Integer memberId) {
        return paymentService.getTotalConsumption(memberId);
    }

    /**
     * 查询订单详情
     */
    @GetMapping("/order/{orderNumber}")
    public PaymentRecord getByOrderNumber(@PathVariable String orderNumber) {
        // 这里需要一个新方法，我们先简单实现
        return null; // 稍后完善
    }
}
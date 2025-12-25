package com.gym.club.mapper;

import com.gym.club.entity.PaymentRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface PaymentRecordMapper {
        int insert(PaymentRecord paymentRecord);

        PaymentRecord selectById(Integer id);

        PaymentRecord selectByOrderNumber(String orderNumber);

        List<PaymentRecord> selectByMemberId(Integer memberId);

        List<PaymentRecord> selectAll();

        int update(PaymentRecord paymentRecord);

        int updatePaymentStatus(@Param("orderNumber") String orderNumber,
                        @Param("paymentStatus") Integer paymentStatus,
                        @Param("transactionId") String transactionId);

        PaymentRecord selectByRelatedId(@Param("relatedId") Integer relatedId,
                        @Param("paymentType") Integer paymentType);

        List<PaymentRecord> selectByRelatedIds(@Param("relatedIds") List<Integer> relatedIds);

        // 统计
        BigDecimal sumAmountByMemberId(Integer memberId);

        BigDecimal sumAmountByDateRange(@Param("startDate") String startDate,
                        @Param("endDate") String endDate);
}
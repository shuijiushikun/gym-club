package com.gym.club.mapper;

import com.gym.club.entity.PaymentRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

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

        // 按日期统计收入
        List<Map<String, Object>> sumRevenueByDate(@Param("startDate") String startDate,
                        @Param("endDate") String endDate);

        // 按支付类型统计收入
        List<Map<String, Object>> sumRevenueByType(@Param("startDate") String startDate,
                        @Param("endDate") String endDate);

        // 按支付方式统计
        List<Map<String, Object>> sumRevenueByMethod(@Param("startDate") String startDate,
                        @Param("endDate") String endDate);

        // 按日统计收入趋势

        List<Map<String, Object>> sumRevenueDaily(@Param("startDate") String startDate,
                        @Param("endDate") String endDate);

        BigDecimal sumRevenueByCoachId(Integer coachId);
}
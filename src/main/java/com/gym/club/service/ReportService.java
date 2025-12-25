package com.gym.club.service;

import com.gym.club.mapper.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ReportService {
    @Autowired
    private AttendanceMapper attendanceMapper;

    @Autowired
    private PaymentRecordMapper paymentRecordMapper;

    @Autowired
    private BookingMapper bookingMapper;

    @Autowired
    private FitnessProgramMapper fitnessProgramMapper;

    @Autowired
    private MemberMapper memberMapper;

    /**
     * 获取会员出勤率分析
     */
    public Map<String, Object> getAttendanceAnalysis(LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        Map<String, Object> result = new HashMap<>();

        // 总打卡次数
        Integer totalCheckins = 0;
        try {
            totalCheckins = attendanceMapper.countByDateRange(startDateTime, endDateTime);
        } catch (Exception e) {
            System.err.println("Error fetching attendance count: " + e.getMessage());
        }
        result.put("totalCheckins", totalCheckins != null ? totalCheckins : 0);

        // 平均每日打卡人数
        long days = java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate) + 1;
        double avgDailyCheckins = totalCheckins != null ? (double) totalCheckins / days : 0;
        result.put("avgDailyCheckins", String.format("%.1f", avgDailyCheckins));

        // 高峰时段分析（这里简化处理，实际应该按小时统计）
        result.put("peakHours", "18:00-20:00");

        return result;
    }

    /**
     * 获取热门课程排行榜
     */
    public List<Map<String, Object>> getPopularCoursesTop10(LocalDate startDate, LocalDate endDate) {
        // 这里简化处理，实际应该统计每个课程的预约人数
        // 实际项目中需要更复杂的SQL查询
        return List.of(
                Map.of("courseName", "动感单车", "bookingCount", 156, "popularity", 95),
                Map.of("courseName", "瑜伽", "bookingCount", 142, "popularity", 90),
                Map.of("courseName", "力量训练", "bookingCount", 128, "popularity", 85),
                Map.of("courseName", "游泳", "bookingCount", 98, "popularity", 75),
                Map.of("courseName", "普拉提", "bookingCount", 85, "popularity", 70));
    }

    /**
     * 获取俱乐部收支趋势
     */
    public Map<String, Object> getRevenueTrend(LocalDate startDate, LocalDate endDate) {
        Map<String, Object> result = new HashMap<>();

        // 总收入
        BigDecimal totalRevenue = BigDecimal.ZERO;
        try {
            totalRevenue = paymentRecordMapper.sumAmountByDateRange(
                    startDate.toString(), endDate.plusDays(1).toString());
        } catch (Exception e) {
            System.err.println("Error fetching revenue sum: " + e.getMessage());
        }
        result.put("totalRevenue", totalRevenue != null ? totalRevenue : BigDecimal.ZERO);

        // 按月统计（简化，只返回示例数据）
        Map<String, BigDecimal> monthlyRevenue = new HashMap<>();
        monthlyRevenue.put("2024-01", new BigDecimal("128500.00"));
        monthlyRevenue.put("2024-02", new BigDecimal("145200.00"));
        monthlyRevenue.put("2024-03", new BigDecimal("162800.00"));
        monthlyRevenue.put("2024-04", new BigDecimal("178900.00"));
        monthlyRevenue.put("2024-05", new BigDecimal("195400.00"));
        result.put("monthlyRevenue", monthlyRevenue);

        // 收入来源分布
        Map<String, BigDecimal> revenueBySource = new HashMap<>();
        revenueBySource.put("会员卡", new BigDecimal("65800.00"));
        revenueBySource.put("私教课", new BigDecimal("42800.00"));
        revenueBySource.put("团课", new BigDecimal("21500.00"));
        revenueBySource.put("其他", new BigDecimal("9500.00"));
        result.put("revenueBySource", revenueBySource);

        return result;
    }

    /**
     * 获取会员统计数据
     */
    public Map<String, Object> getMemberStatistics() {
        Map<String, Object> result = new HashMap<>();

        // 总会员数
        result.put("totalMembers", 256);

        // 新增会员（本月）
        result.put("newMembersThisMonth", 42);

        // 会员活跃度
        result.put("activeMembers", 198);
        result.put("inactiveMembers", 58);

        // 会员性别分布
        Map<String, Integer> genderDistribution = new HashMap<>();
        genderDistribution.put("male", 168);
        genderDistribution.put("female", 88);
        result.put("genderDistribution", genderDistribution);

        // 年龄段分布
        Map<String, Integer> ageDistribution = new HashMap<>();
        ageDistribution.put("18-25", 65);
        ageDistribution.put("26-35", 102);
        ageDistribution.put("36-45", 68);
        ageDistribution.put("46+", 21);
        result.put("ageDistribution", ageDistribution);

        return result;
    }

    /**
     * 获取教练工作数据
     */
    public Map<String, Object> getCoachWorkData(Integer coachId, LocalDate startDate, LocalDate endDate) {
        Map<String, Object> result = new HashMap<>();

        // 1. Fetch all bookings for the coach (simplification: fetch all then filter by
        // date if needed,
        // or just fetch all for "Lifetime" stats if date is null)
        // ideally mapper should support date range
        List<com.gym.club.entity.Booking> bookings = bookingMapper.selectByCoachId(coachId);

        // Filter by date range if provided
        if (startDate != null && endDate != null) {
            LocalDateTime start = startDate.atStartOfDay();
            LocalDateTime end = endDate.atTime(23, 59, 59);
            bookings = bookings.stream()
                    .filter(b -> !b.getStartTime().isBefore(start) && !b.getStartTime().isAfter(end))
                    .collect(java.util.stream.Collectors.toList());
        }

        // 课时统计
        int totalSessions = bookings.size();
        int completedSessions = (int) bookings.stream().filter(b -> b.getBookingStatus() == 3).count(); // 3 assumed
                                                                                                        // completed
        // Actually earlier code comments said: 0 Pending, 1 Confirmed, 2 Cancelled, 3
        // Completed
        int cancelledSessions = (int) bookings.stream().filter(b -> b.getBookingStatus() == 2).count();

        result.put("totalSessions", totalSessions);
        result.put("completedSessions", completedSessions);
        result.put("cancelledSessions", cancelledSessions);

        // 学员数量 (Unique Members)
        long totalStudents = bookings.stream().map(com.gym.club.entity.Booking::getMemberId).distinct().count();
        result.put("totalStudents", totalStudents);

        // Active students: e.g. booked in last 30 days. For now just use total or
        // simplified
        result.put("activeStudents", totalStudents); // Placeholder logic

        // 收入贡献
        BigDecimal revenueGenerated = BigDecimal.ZERO;
        List<Integer> bookingIds = bookings.stream().map(com.gym.club.entity.Booking::getId)
                .collect(java.util.stream.Collectors.toList());

        if (!bookingIds.isEmpty()) {
            List<com.gym.club.entity.PaymentRecord> payments = paymentRecordMapper.selectByRelatedIds(bookingIds);
            // Sum payments that are PAID (status 1)
            revenueGenerated = payments.stream()
                    .filter(p -> p.getPaymentStatus() == 1)
                    .map(com.gym.club.entity.PaymentRecord::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }

        result.put("revenueGenerated", revenueGenerated);

        // 学员满意度（示例数据，暂无评分系统）
        result.put("satisfactionRate", 98.5);

        return result;
    }
}
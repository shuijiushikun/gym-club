package com.gym.club.service;

import com.gym.club.mapper.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
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

    @Autowired
    private CoachMapper coachMapper;

    @Autowired
    private MemberCardMapper memberCardMapper;

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("yyyy-MM");

    /**
     * 获取会员出勤率分析 - 使用真实数据
     */
    public Map<String, Object> getAttendanceAnalysis(LocalDate startDate, LocalDate endDate) {
        Map<String, Object> result = new HashMap<>();

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        // 1. 总打卡次数
        Integer totalCheckins = attendanceMapper.countByDateRange(startDateTime, endDateTime);
        result.put("totalCheckins", totalCheckins != null ? totalCheckins : 0);

        // 2. 平均每日打卡人数
        long days = java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate) + 1;
        double avgDailyCheckins = totalCheckins != null ? (double) totalCheckins / days : 0;
        result.put("avgDailyCheckins", String.format("%.1f", avgDailyCheckins));

        // 3. 按日期统计打卡趋势
        List<Map<String, Object>> dailyTrend = attendanceMapper.countAttendanceByDate(
                startDate.toString(), endDate.plusDays(1).toString());
        result.put("dailyTrend", dailyTrend);

        // 4. 高峰时段分析（取最近一天的数据）
        String today = LocalDate.now().toString();
        List<Map<String, Object>> hourlyData = attendanceMapper.countAttendanceByHour(today);
        result.put("hourlyData", hourlyData);

        // 5. 计算高峰时段
        if (hourlyData != null && !hourlyData.isEmpty()) {
            Optional<Map<String, Object>> maxHour = hourlyData.stream()
                    .max(Comparator.comparing(m -> ((Number) m.get("count")).intValue()));

            if (maxHour.isPresent()) {
                int hour = ((Number) maxHour.get().get("hour")).intValue();
                String peakTime = String.format("%02d:00-%02d:00", hour, hour + 1);
                result.put("peakTime", peakTime);
            }
        }

        return result;
    }

    /**
     * 获取热门课程排行榜 - 使用真实数据
     */
    public List<Map<String, Object>> getPopularCoursesTop10(LocalDate startDate, LocalDate endDate) {
        // 统计课程预约量
        List<Map<String, Object>> courseBookings = bookingMapper.countBookingsByProgram(
                startDate.toString(), endDate.plusDays(1).toString());

        if (courseBookings == null || courseBookings.isEmpty()) {
            return Collections.emptyList();
        }

        // 获取课程详细信息并计算热度
        List<Map<String, Object>> result = new ArrayList<>();
        int maxCount = 0;

        // 找出最大预约量用于计算百分比
        for (Map<String, Object> booking : courseBookings) {
            Integer count = ((Number) booking.get("count")).intValue();
            if (count > maxCount)
                maxCount = count;
        }

        for (Map<String, Object> booking : courseBookings) {
            Integer programId = ((Number) booking.get("program_id")).intValue();
            Integer count = ((Number) booking.get("count")).intValue();

            // 获取课程详情
            com.gym.club.entity.FitnessProgram program = fitnessProgramMapper.selectById(programId);

            if (program != null) {
                Map<String, Object> courseData = new HashMap<>();
                courseData.put("courseId", programId);
                courseData.put("courseName", program.getName());
                courseData.put("courseType", getProgramTypeName(program.getType()));
                courseData.put("bookingCount", count);
                courseData.put("popularity", maxCount > 0 ? (count * 100 / maxCount) : 0);

                result.add(courseData);
            }
        }

        // 按预约量排序，取前10
        result.sort((a, b) -> ((Integer) b.get("bookingCount")).compareTo((Integer) a.get("bookingCount")));

        return result.size() > 10 ? result.subList(0, 10) : result;
    }

    private String getProgramTypeName(Integer type) {
        if (type == null)
            return "未知";
        switch (type) {
            case 1:
                return "团课";
            case 2:
                return "私教课";
            case 3:
                return "自由训练";
            default:
                return "其他";
        }
    }

    /**
     * 获取俱乐部收支趋势 - 使用真实数据
     */
    public Map<String, Object> getRevenueTrend(LocalDate startDate, LocalDate endDate) {
        Map<String, Object> result = new HashMap<>();

        // 1. 总收入
        BigDecimal totalRevenue = paymentRecordMapper.sumAmountByDateRange(
                startDate.toString(), endDate.plusDays(1).toString());
        result.put("totalRevenue", totalRevenue != null ? totalRevenue : BigDecimal.ZERO);

        // 2. 按月统计收入
        List<Map<String, Object>> monthlyRevenue = paymentRecordMapper.sumRevenueByDate(
                startDate.toString(), endDate.plusDays(1).toString());

        // 转换格式
        Map<String, BigDecimal> monthlyMap = new LinkedHashMap<>();
        if (monthlyRevenue != null) {
            for (Map<String, Object> monthData : monthlyRevenue) {
                String month = (String) monthData.get("month");
                BigDecimal amount = (BigDecimal) monthData.get("amount");
                monthlyMap.put(month, amount != null ? amount : BigDecimal.ZERO);
            }
        }
        result.put("monthlyRevenue", monthlyMap);

        // 3. 收入来源分布
        List<Map<String, Object>> revenueByType = paymentRecordMapper.sumRevenueByType(
                startDate.toString(), endDate.plusDays(1).toString());

        Map<String, BigDecimal> revenueBySource = new HashMap<>();
        if (revenueByType != null) {
            for (Map<String, Object> typeData : revenueByType) {
                Integer type = ((Number) typeData.get("type")).intValue();
                BigDecimal amount = (BigDecimal) typeData.get("amount");

                String typeName = getPaymentTypeName(type);
                revenueBySource.put(typeName, amount != null ? amount : BigDecimal.ZERO);
            }
        }
        result.put("revenueBySource", revenueBySource);

        // 4. 支付方式分布
        List<Map<String, Object>> revenueByMethod = paymentRecordMapper.sumRevenueByMethod(
                startDate.toString(), endDate.plusDays(1).toString());

        Map<String, BigDecimal> paymentMethodDistribution = new HashMap<>();
        if (revenueByMethod != null) {
            for (Map<String, Object> methodData : revenueByMethod) {
                Integer method = ((Number) methodData.get("method")).intValue();
                BigDecimal amount = (BigDecimal) methodData.get("amount");

                String methodName = getPaymentMethodName(method);
                paymentMethodDistribution.put(methodName, amount != null ? amount : BigDecimal.ZERO);
            }
        }
        result.put("paymentMethodDistribution", paymentMethodDistribution);

        return result;
    }

    private String getPaymentTypeName(Integer type) {
        if (type == null)
            return "其他";
        switch (type) {
            case 1:
                return "会员卡";
            case 2:
                return "课程";
            case 3:
                return "私教";
            default:
                return "其他";
        }
    }

    private String getPaymentMethodName(Integer method) {
        if (method == null)
            return "其他";
        switch (method) {
            case 1:
                return "微信";
            case 2:
                return "支付宝";
            case 3:
                return "现金";
            case 4:
                return "银行卡";
            default:
                return "其他";
        }
    }

    /**
     * 获取会员统计数据 - 使用真实数据
     */
    /**
     * 获取会员统计数据 - 使用真实数据
     */
    public Map<String, Object> getMemberStatistics() {
        Map<String, Object> result = new HashMap<>();

        // 1. 总会员数
        List<com.gym.club.entity.Member> allMembers = memberMapper.selectAll();
        int totalMembers = allMembers != null ? allMembers.size() : 0;
        result.put("totalMembers", totalMembers);

        // 2. 新增会员（本月）
        LocalDate now = LocalDate.now();
        LocalDate firstDayOfMonth = now.withDayOfMonth(1);

        long newMembersThisMonth = allMembers != null ? allMembers.stream()
                .filter(member -> member.getCreateTime() != null &&
                        member.getCreateTime().toLocalDate().isAfter(firstDayOfMonth.minusDays(1)))
                .count() : 0;
        result.put("newMembersThisMonth", newMembersThisMonth);

        // 3. 活跃会员数（持有有效会员卡）
        int activeMembers = memberCardMapper.countActiveMembers(now.toString());
        result.put("activeMembers", activeMembers);
        result.put("inactiveMembers", totalMembers - activeMembers);

        // 4. 会员性别分布
        Map<String, Integer> genderDistribution = new HashMap<>();
        if (allMembers != null) {
            long maleCount = allMembers.stream()
                    .filter(m -> m.getGender() != null && m.getGender() == 1)
                    .count();
            long femaleCount = allMembers.stream()
                    .filter(m -> m.getGender() != null && m.getGender() == 0)
                    .count();
            long unknownCount = allMembers.size() - maleCount - femaleCount;

            genderDistribution.put("male", (int) maleCount);
            genderDistribution.put("female", (int) femaleCount);
            genderDistribution.put("unknown", (int) unknownCount);
        }
        result.put("genderDistribution", genderDistribution);

        // 5. 会员年龄分布
        List<Map<String, Object>> ageDistribution = memberMapper.countMembersByAgeGroup();
        result.put("ageDistribution", ageDistribution);

        // 6. 会员卡类型分布
        List<Map<String, Object>> cardTypeDistribution = memberMapper.countMembersByCardType();
        result.put("cardTypeDistribution", cardTypeDistribution);

        // 7. 会员注册趋势（最近12个月）
        LocalDate oneYearAgo = now.minusMonths(12);
        List<Map<String, Object>> registrationTrend = memberMapper.countMembersRegisteredByMonth(
                oneYearAgo.toString(), now.toString());
        result.put("registrationTrend", registrationTrend);

        return result;
    }

    /**
     * 获取教练工作数据 - 使用真实数据
     */
    public Map<String, Object> getCoachWorkData(Integer coachId, LocalDate startDate, LocalDate endDate) {
        Map<String, Object> result = new HashMap<>();

        // 1. 获取教练信息
        com.gym.club.entity.Coach coach = coachMapper.selectById(coachId);
        if (coach == null) {
            result.put("error", "教练不存在");
            return result;
        }

        // 2. 统计教练的课程预约
        List<com.gym.club.entity.Booking> coachBookings = bookingMapper.selectByCoachId(coachId);

        if (coachBookings != null) {
            // 总课时
            result.put("totalSessions", coachBookings.size());

            // 已完成课时
            long completedSessions = coachBookings.stream()
                    .filter(b -> b.getBookingStatus() == 3) // 已完成
                    .count();
            result.put("completedSessions", completedSessions);

            // 已取消课时
            long cancelledSessions = coachBookings.stream()
                    .filter(b -> b.getBookingStatus() == 2) // 已取消
                    .count();
            result.put("cancelledSessions", cancelledSessions);

            // 不同学员数量
            long uniqueStudents = coachBookings.stream()
                    .map(com.gym.club.entity.Booking::getMemberId)
                    .distinct()
                    .count();
            result.put("totalStudents", uniqueStudents);
        }

        // 3. 教练收入贡献（私教课收入）
        BigDecimal totalRevenue = paymentRecordMapper.sumRevenueByCoachId(coachId);
        result.put("revenueGenerated", totalRevenue != null ? totalRevenue : BigDecimal.ZERO);

        return result;
    }
}
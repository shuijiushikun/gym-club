package com.gym.club.service;

import com.gym.club.entity.Attendance;
import com.gym.club.entity.Booking;
import com.gym.club.entity.MemberCard;
import com.gym.club.mapper.AttendanceMapper;
import com.gym.club.mapper.BookingMapper;
import com.gym.club.mapper.MemberCardMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AttendanceService {
    @Autowired
    private AttendanceMapper attendanceMapper;

    @Autowired
    private MemberCardMapper memberCardMapper;

    @Autowired
    private BookingMapper bookingMapper;

    /**
     * 签到打卡
     */
    @Transactional
    public Attendance checkIn(Integer memberId, Integer bookingId, String notes) {
        // 1. 验证会员是否有有效会员卡
        MemberCard validCard = memberCardMapper.selectValidCardByMemberId(memberId);
        if (validCard == null) {
            throw new RuntimeException("会员没有有效的会员卡或会员卡已过期");
        }

        // 2. 如果是预约打卡，验证预约信息
        if (bookingId != null) {
            Booking booking = bookingMapper.selectById(bookingId);
            if (booking == null) {
                throw new RuntimeException("预约记录不存在");
            }
            if (!booking.getMemberId().equals(memberId)) {
                throw new RuntimeException("不能为其他会员的预约打卡");
            }
            if (booking.getBookingStatus() != 1 && booking.getBookingStatus() != 3) {
                throw new RuntimeException("只有已确认或已完成的预约可以打卡");
            }
        }

        // 3. 创建打卡记录
        Attendance attendance = new Attendance();
        attendance.setMemberId(memberId);
        attendance.setBookingId(bookingId);
        attendance.setCheckInTime(LocalDateTime.now());
        attendance.setAttendanceType(bookingId != null ? 2 : 1); // 2预约打卡，1正常打卡
        attendance.setNotes(notes);

        attendanceMapper.insert(attendance);
        return attendanceMapper.selectById(attendance.getId());
    }

    /**
     * 签出打卡
     */
    @Transactional
    public Attendance checkOut(Integer attendanceId) {
        Attendance attendance = attendanceMapper.selectById(attendanceId);
        if (attendance == null) {
            throw new RuntimeException("打卡记录不存在");
        }

        if (attendance.getCheckOutTime() != null) {
            throw new RuntimeException("已经签出过了");
        }

        LocalDateTime checkOutTime = LocalDateTime.now();
        attendance.setCheckOutTime(checkOutTime);

        // 计算锻炼时长（分钟）
        Duration duration = Duration.between(attendance.getCheckInTime(), checkOutTime);
        int durationMinutes = (int) duration.toMinutes();
        attendance.setDurationMinutes(durationMinutes);

        // 估算消耗卡路里（平均每分钟8-10卡路里）
        int caloriesBurned = durationMinutes * 8 + (int) (Math.random() * 3 * durationMinutes);
        attendance.setCaloriesBurned(caloriesBurned);

        attendanceMapper.updateCheckOutTime(attendanceId, checkOutTime, durationMinutes);
        return attendanceMapper.selectById(attendanceId);
    }

    /**
     * 获取打卡记录
     */
    public Attendance getById(Integer id) {
        return attendanceMapper.selectById(id);
    }

    public List<Attendance> getByMemberId(Integer memberId) {
        return attendanceMapper.selectByMemberId(memberId);
    }

    public List<Attendance> getByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return attendanceMapper.selectByDateRange(startDate, endDate);
    }

    public List<Attendance> getByMemberAndDateRange(Integer memberId, LocalDateTime startDate, LocalDateTime endDate) {
        return attendanceMapper.selectByMemberAndDateRange(memberId, startDate, endDate);
    }

    /**
     * 统计功能
     */
    public Integer getAttendanceCountByMember(Integer memberId) {
        return attendanceMapper.countByMemberId(memberId);
    }

    public Integer getTotalDurationByMember(Integer memberId) {
        return attendanceMapper.sumDurationByMemberId(memberId);
    }

    public Integer getAttendanceCountByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return attendanceMapper.countByDateRange(startDate, endDate);
    }

    /**
     * 获取会员出勤率（最近30天）
     */
    public double getAttendanceRate(Integer memberId) {
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minusDays(30);

        // 获取会员的有效卡
        MemberCard validCard = memberCardMapper.selectValidCardByMemberId(memberId);
        if (validCard == null) {
            return 0.0;
        }

        // 计算卡的有效天数（最大30天）
        long cardDays = Math.min(30,
                Duration.between(startDate.toLocalDate().atStartOfDay(),
                        endDate.toLocalDate().atStartOfDay()).toDays() + 1);

        // 获取打卡次数
        Integer attendanceCount = attendanceMapper.countByDateRange(startDate, endDate);

        // 出勤率 = 打卡天数 / 卡有效天数
        return attendanceCount != null ? (double) attendanceCount / cardDays * 100 : 0.0;
    }
}
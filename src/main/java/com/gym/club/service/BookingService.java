package com.gym.club.service;

import com.gym.club.entity.Booking;
import com.gym.club.entity.FitnessProgram;
import com.gym.club.entity.MemberCard;
import com.gym.club.mapper.BookingMapper;
import com.gym.club.mapper.FitnessProgramMapper;
import com.gym.club.mapper.MemberCardMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class BookingService {
    @Autowired
    private BookingMapper bookingMapper;

    @Autowired
    private MemberCardMapper memberCardMapper;

    @Autowired
    private FitnessProgramMapper fitnessProgramMapper;

    // 生成预约单号
    private String generateBookingNumber() {
        return "BK" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }

    /**
     * 创建预约（带冲突检测）
     */
    @Transactional
    public Booking createBooking(Booking booking) {
        // 1. 验证会员是否有有效会员卡
        MemberCard validCard = memberCardMapper.selectValidCardByMemberId(booking.getMemberId());
        if (validCard == null) {
            throw new RuntimeException("会员没有有效的会员卡或会员卡已过期");
        }

        // 2. 如果是课程预约，验证课程信息
        if (booking.getBookingType() == 1 || booking.getBookingType() == 2) {
            FitnessProgram program = fitnessProgramMapper.selectById(booking.getRelatedId());
            if (program == null || program.getStatus() != 1) {
                throw new RuntimeException("课程不存在或已下架");
            }

            // 如果是私教课，验证教练
            if (booking.getBookingType() == 2 && program.getCoachId() == null) {
                throw new RuntimeException("私教课程必须指定教练");
            }

            // 设置教练ID
            if (booking.getCoachId() == null && program.getCoachId() != null) {
                booking.setCoachId(program.getCoachId());
            }

            // 设置时长
            if (booking.getDurationMinutes() == null) {
                booking.setDurationMinutes(program.getDurationMinutes());
            }
        }

        // 3. 冲突检测
        List<Booking> conflictingBookings = bookingMapper.selectConflictingBookings(
                booking.getVenueId(),
                booking.getCoachId(),
                booking.getStartTime(),
                booking.getEndTime(),
                null);

        if (!conflictingBookings.isEmpty()) {
            StringBuilder conflictMsg = new StringBuilder("预约冲突：");
            for (Booking conflict : conflictingBookings) {
                if (conflict.getVenueId() != null && conflict.getVenueId().equals(booking.getVenueId())) {
                    conflictMsg.append("场地已被占用；");
                }
                if (conflict.getCoachId() != null && conflict.getCoachId().equals(booking.getCoachId())) {
                    conflictMsg.append("教练已有其他预约；");
                }
            }
            throw new RuntimeException(conflictMsg.toString());
        }

        // 4. 生成预约单号并保存
        booking.setBookingNumber(generateBookingNumber());

        // 默认状态
        if (booking.getBookingStatus() == null) {
            booking.setBookingStatus(1); // 默认已确认
        }
        if (booking.getParticipantsCount() == null) {
            booking.setParticipantsCount(1);
        }
        if (booking.getDurationMinutes() == null) {
            booking.setDurationMinutes(60); // 默认1小时
        }

        // 5. 计算结束时间
        if (booking.getEndTime() == null && booking.getStartTime() != null && booking.getDurationMinutes() != null) {
            booking.setEndTime(booking.getStartTime().plusMinutes(booking.getDurationMinutes()));
        }

        bookingMapper.insert(booking);
        return bookingMapper.selectById(booking.getId());
    }

    /**
     * 取消预约
     */
    @Transactional
    public boolean cancelBooking(Integer id, String cancelReason) {
        Booking booking = bookingMapper.selectById(id);
        if (booking == null) {
            throw new RuntimeException("预约不存在");
        }

        // 只能取消待确认或已确认的预约
        if (booking.getBookingStatus() != 0 && booking.getBookingStatus() != 1) {
            throw new RuntimeException("只有待确认或已确认的预约可以取消");
        }

        // 开始前2小时内不能取消
        if (booking.getStartTime().minusHours(2).isBefore(LocalDateTime.now())) {
            throw new RuntimeException("预约开始前2小时内不能取消");
        }

        return bookingMapper.updateBookingStatus(id, 2, cancelReason) > 0;
    }

    /**
     * 签到
     */
    @Transactional
    public boolean checkIn(Integer id) {
        Booking booking = bookingMapper.selectById(id);
        if (booking == null) {
            throw new RuntimeException("预约不存在");
        }

        // 只能签到自己预约的课程
        if (booking.getBookingStatus() != 1 && booking.getBookingStatus() != 3) {
            throw new RuntimeException("只有已确认或已完成的预约可以签到");
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startTime = booking.getStartTime();

        Integer attendanceStatus = 1; // 已签到

        // 判断是否迟到（开始后15分钟内算准时，超过算迟到）
        if (now.isAfter(startTime.plusMinutes(15))) {
            attendanceStatus = 2; // 迟到
        }

        // 如果预约已过期，不能签到
        if (now.isAfter(booking.getEndTime())) {
            throw new RuntimeException("预约已过期，不能签到");
        }

        // 提前15分钟才能签到
        if (now.isBefore(startTime.minusMinutes(15))) {
            throw new RuntimeException("提前15分钟才能签到");
        }

        return bookingMapper.updateAttendanceStatus(id, attendanceStatus) > 0;
    }

    /**
     * 查询预约
     */
    public Booking getById(Integer id) {
        return bookingMapper.selectById(id);
    }

    public List<Booking> getByMemberId(Integer memberId) {
        return bookingMapper.selectByMemberId(memberId);
    }

    public List<Booking> getByCoachId(Integer coachId) {
        return bookingMapper.selectByCoachId(coachId);
    }

    public List<Booking> getByVenueId(Integer venueId) {
        return bookingMapper.selectByVenueId(venueId);
    }

    public List<Booking> getAll() {
        return bookingMapper.selectAll();
    }

    /**
     * 检测时间段是否有空
     */
    public boolean checkAvailability(Integer venueId, Integer coachId,
            LocalDateTime startTime, LocalDateTime endTime) {
        List<Booking> conflicts = bookingMapper.selectConflictingBookings(
                venueId, coachId, startTime, endTime, null);
        return conflicts.isEmpty();
    }

    /**
     * 更新预约状态
     */
    public boolean updateBookingStatus(Integer id, Integer status, String cancelReason) {
        return bookingMapper.updateBookingStatus(id, status, cancelReason) > 0;
    }
}
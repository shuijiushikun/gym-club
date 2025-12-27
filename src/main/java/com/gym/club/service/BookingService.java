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

    @Autowired
    private PaymentService paymentService; // Inject PaymentService

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
        Booking newBooking = bookingMapper.selectById(booking.getId());

        // 6. 如果课程有价格，生成消费记录
        if (booking.getBookingType() != null && booking.getRelatedId() != null) {
            FitnessProgram program = fitnessProgramMapper.selectById(booking.getRelatedId());
            if (program != null && program.getPrice() != null
                    && program.getPrice().compareTo(java.math.BigDecimal.ZERO) > 0) {
                com.gym.club.entity.PaymentRecord payment = new com.gym.club.entity.PaymentRecord();
                payment.setMemberId(booking.getMemberId());
                payment.setOrderNumber("BKPAY" + System.currentTimeMillis());
                // 1:Card, 2:Course, 3:Coach
                // If type is 1 (Group) -> 2 (Course), if 2 (Private) -> 3 (Coach)
                payment.setPaymentType(program.getType() == 2 ? 3 : 2);
                // User Request: Set relatedId to CoachId for easier stats
                payment.setRelatedId(booking.getCoachId());
                payment.setAmount(program.getPrice());
                payment.setPaymentMethod(1); // Default to method 1
                payment.setPaymentStatus(1); // Auto-paid for now
                payment.setRemark("课程预约消费: " + program.getName());
                paymentService.createPaymentOrder(payment);
            }
        }

        return newBooking;
    }

    @Autowired
    private com.gym.club.mapper.PaymentRecordMapper paymentRecordMapper;

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

        boolean success = bookingMapper.updateBookingStatus(id, 2, cancelReason) > 0;

        if (success) {
            // Find related payment record and set to Refunded (3)
            // Payment type for courses/private coach is 2 or 3. We try both or check logic.
            // Earlier logic: Program type 2 (Private) -> Payment Type 3, else 2.
            // Since we don't easily know program type here without fetching, checking both
            // is safest or we assume.
            // Let's just try to find by relatedId = bookingId.
            // In mapper I added paymentType param.

            // Try fetching with type 2 (Course) first
            com.gym.club.entity.PaymentRecord record = paymentRecordMapper.selectByRelatedId(id, 2);
            if (record == null) {
                record = paymentRecordMapper.selectByRelatedId(id, 3); // Try Coach type
            }

            if (record != null) {
                record.setPaymentStatus(3); // Refunded
                record.setRemark(record.getRemark() + " [已退款: " + cancelReason + "]");
                paymentRecordMapper.update(record);
            }
        }

        return success;
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
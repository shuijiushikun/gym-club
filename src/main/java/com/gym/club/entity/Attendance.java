package com.gym.club.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Attendance {
    private Integer id;
    private Integer memberId;
    private Integer bookingId;
    private LocalDateTime checkInTime;
    private LocalDateTime checkOutTime;
    private Integer durationMinutes;
    private Integer caloriesBurned;
    private Integer attendanceType; // 1正常打卡，2预约打卡
    private String notes;
    private LocalDateTime createTime;

    // 关联信息
    private Member member;
    private Booking booking;
}
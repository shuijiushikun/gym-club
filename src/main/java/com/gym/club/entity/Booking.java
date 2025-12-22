package com.gym.club.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Booking {
    private Integer id;
    private String bookingNumber;
    private Integer memberId;
    private Integer bookingType; // 1团课，2私教课，3自由训练，4器械预约，5场地预约
    private Integer relatedId; // 关联ID（项目ID、教练ID、场地ID等）
    private Integer coachId; // 教练ID
    private Integer venueId; // 场地ID
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer durationMinutes;
    private Integer participantsCount;
    private Integer bookingStatus; // 0待确认，1已确认，2已取消，3已完成，4已过期
    private Integer attendanceStatus; // 0未签到，1已签到，2迟到，3缺席
    private String notes;
    private String cancelReason;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    // 关联信息
    private Member member;
    private Coach coach;
    private Venue venue;
    private FitnessProgram fitnessProgram;
}
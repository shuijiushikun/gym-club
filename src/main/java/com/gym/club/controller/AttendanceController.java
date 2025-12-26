package com.gym.club.controller;

import com.gym.club.entity.Attendance;
import com.gym.club.service.AttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {
    @Autowired
    private AttendanceService attendanceService;

    /**
     * 签到打卡
     */
    @PostMapping("/checkin")
    public Attendance checkIn(@RequestParam Integer memberId,
            @RequestParam(required = false) Integer bookingId,
            @RequestParam(required = false) String notes) {
        return attendanceService.checkIn(memberId, bookingId, notes);
    }

    /**
     * 签出打卡
     */
    @PostMapping("/checkout/{id}")
    public Attendance checkOut(@PathVariable Integer id) {
        return attendanceService.checkOut(id);
    }

    /**
     * 查询单个打卡记录
     */
    @GetMapping("/{id}")
    public Attendance getById(@PathVariable Integer id) {
        return attendanceService.getById(id);
    }

    /**
     * 查询会员的打卡记录
     */
    @GetMapping("/member/{memberId}")
    public List<Attendance> getByMemberId(@PathVariable Integer memberId) {
        return attendanceService.getByMemberId(memberId);
    }

    /**
     * 按时间段查询打卡记录
     */
    @GetMapping("/date-range")
    public List<Attendance> getByDateRange(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return attendanceService.getByDateRange(startDate, endDate);
    }

    /**
     * 查询会员在时间段的打卡记录
     */
    @GetMapping("/member-date-range")
    public List<Attendance> getByMemberAndDateRange(
            @RequestParam Integer memberId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return attendanceService.getByMemberAndDateRange(memberId, startDate, endDate);
    }

    /**
     * 统计会员打卡次数
     */
    @GetMapping("/count/{memberId}")
    public Integer getAttendanceCount(@PathVariable Integer memberId) {
        return attendanceService.getAttendanceCountByMember(memberId);
    }

    /**
     * 统计会员总锻炼时长
     */
    @GetMapping("/total-duration/{memberId}")
    public Integer getTotalDuration(@PathVariable Integer memberId) {
        return attendanceService.getTotalDurationByMember(memberId);
    }

    /**
     * 统计时间段内打卡次数
     */
    @GetMapping("/count-date-range")
    public Integer getAttendanceCountByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return attendanceService.getAttendanceCountByDateRange(startDate, endDate);
    }

    /**
     * 获取会员出勤率
     */
    @GetMapping("/attendance-rate/{memberId}")
    public double getAttendanceRate(@PathVariable Integer memberId) {
        return attendanceService.getAttendanceRate(memberId);
    }
}
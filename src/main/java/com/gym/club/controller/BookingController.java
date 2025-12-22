package com.gym.club.controller;

import com.gym.club.entity.Booking;
import com.gym.club.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/booking")
public class BookingController {
    @Autowired
    private BookingService bookingService;

    /**
     * 创建预约
     */
    @PostMapping("/create")
    public Booking createBooking(@RequestBody Booking booking) {
        return bookingService.createBooking(booking);
    }

    /**
     * 取消预约
     */
    @PostMapping("/cancel/{id}")
    public boolean cancelBooking(@PathVariable Integer id,
            @RequestParam(required = false) String cancelReason) {
        return bookingService.cancelBooking(id, cancelReason);
    }

    /**
     * 签到
     */
    @PostMapping("/checkin/{id}")
    public boolean checkIn(@PathVariable Integer id) {
        return bookingService.checkIn(id);
    }

    /**
     * 查询单个预约
     */
    @GetMapping("/{id}")
    public Booking getById(@PathVariable Integer id) {
        return bookingService.getById(id);
    }

    /**
     * 查询会员的预约
     */
    @GetMapping("/member/{memberId}")
    public List<Booking> getByMemberId(@PathVariable Integer memberId) {
        return bookingService.getByMemberId(memberId);
    }

    /**
     * 查询教练的预约
     */
    @GetMapping("/coach/{coachId}")
    public List<Booking> getByCoachId(@PathVariable Integer coachId) {
        return bookingService.getByCoachId(coachId);
    }

    /**
     * 查询场地的预约
     */
    @GetMapping("/venue/{venueId}")
    public List<Booking> getByVenueId(@PathVariable Integer venueId) {
        return bookingService.getByVenueId(venueId);
    }

    /**
     * 查询所有预约
     */
    @GetMapping("/all")
    public List<Booking> getAll() {
        return bookingService.getAll();
    }

    /**
     * 检测时间段是否可用
     */
    @GetMapping("/check-availability")
    public boolean checkAvailability(
            @RequestParam(required = false) Integer venueId,
            @RequestParam(required = false) Integer coachId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        return bookingService.checkAvailability(venueId, coachId, startTime, endTime);
    }

    /**
     * 更新预约状态（管理员用）
     */
    @PostMapping("/update-status/{id}")
    public boolean updateBookingStatus(@PathVariable Integer id,
            @RequestParam Integer status,
            @RequestParam(required = false) String cancelReason) {
        return bookingService.updateBookingStatus(id, status, cancelReason);
    }
}
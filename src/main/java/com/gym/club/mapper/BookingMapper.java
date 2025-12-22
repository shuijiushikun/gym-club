package com.gym.club.mapper;

import com.gym.club.entity.Booking;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface BookingMapper {
    int insert(Booking booking);

    Booking selectById(Integer id);

    Booking selectByBookingNumber(String bookingNumber);

    List<Booking> selectByMemberId(Integer memberId);

    List<Booking> selectByCoachId(Integer coachId);

    List<Booking> selectByVenueId(Integer venueId);

    List<Booking> selectAll();

    // 冲突检测：查询指定时间段的预约
    List<Booking> selectConflictingBookings(@Param("venueId") Integer venueId,
            @Param("coachId") Integer coachId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            @Param("excludeBookingId") Integer excludeBookingId);

    // 按状态查询
    List<Booking> selectByBookingStatus(Integer bookingStatus);

    // 更新预约状态
    int updateBookingStatus(@Param("id") Integer id,
            @Param("bookingStatus") Integer bookingStatus,
            @Param("cancelReason") String cancelReason);

    // 更新出勤状态
    int updateAttendanceStatus(@Param("id") Integer id,
            @Param("attendanceStatus") Integer attendanceStatus);

    int update(Booking booking);

    int deleteById(Integer id);
}
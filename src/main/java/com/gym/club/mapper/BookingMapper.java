package com.gym.club.mapper;

import com.gym.club.entity.Booking;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

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

        // 统计课程预约量
        List<Map<String, Object>> countBookingsByProgram(@Param("startDate") String startDate,
                        @Param("endDate") String endDate);

        // 统计教练预约量
        List<Map<String, Object>> countBookingsByCoach(@Param("startDate") String startDate,
                        @Param("endDate") String endDate);

        // 统计场地使用率
        List<Map<String, Object>> countBookingsByVenue(@Param("startDate") String startDate,
                        @Param("endDate") String endDate);
        List<Map<String, Object>> countBookingsByStatus(@Param("startDate") String startDate, 
                                                   @Param("endDate") String endDate);
    
        List<Map<String, Object>> countBookingsMonthly(@Param("startDate") String startDate, 
                                                  @Param("endDate") String endDate);
}
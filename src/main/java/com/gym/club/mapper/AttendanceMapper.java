package com.gym.club.mapper;

import com.gym.club.entity.Attendance;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface AttendanceMapper {
    int insert(Attendance attendance);

    Attendance selectById(Integer id);

    List<Attendance> selectByMemberId(Integer memberId);

    List<Attendance> selectByDateRange(@Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    List<Attendance> selectByMemberAndDateRange(@Param("memberId") Integer memberId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    int updateCheckOutTime(@Param("id") Integer id,
            @Param("checkOutTime") LocalDateTime checkOutTime,
            @Param("durationMinutes") Integer durationMinutes);

    // 统计
    Integer countByMemberId(Integer memberId);

    Integer sumDurationByMemberId(Integer memberId);

    Integer countByDateRange(@Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
}
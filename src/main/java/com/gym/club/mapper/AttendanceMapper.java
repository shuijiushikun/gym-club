package com.gym.club.mapper;

import com.gym.club.entity.Attendance;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface AttendanceMapper {
        // 新增打卡记录
        int insert(Attendance attendance);

        // 根据ID查询打卡记录
        Attendance selectById(Integer id);

        // 根据会员ID查询所有打卡记录
        List<Attendance> selectByMemberId(Integer memberId);

        // 根据日期范围查询打卡记录
        List<Attendance> selectByDateRange(
                        @Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate);

        // 根据会员ID+日期范围查询打卡记录
        List<Attendance> selectByMemberAndDateRange(
                        @Param("memberId") Integer memberId,
                        @Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate);

        // 更新签退时间和时长
        int updateCheckOutTime(
                        @Param("id") Integer id,
                        @Param("checkOutTime") LocalDateTime checkOutTime,
                        @Param("durationMinutes") Integer durationMinutes);

        // 按日期统计打卡次数
        List<Map<String, Object>> countAttendanceByDate(
                        @Param("startDate") String startDate,
                        @Param("endDate") String endDate);

        // 按小时统计打卡高峰
        List<Map<String, Object>> countAttendanceByHour(@Param("date") String date);

        // 统计会员活跃度
        List<Map<String, Object>> countMemberActivity(@Param("days") Integer days);

        // 统计会员总打卡次数
        Integer countByMemberId(Integer memberId);

        // 统计会员总打卡时长（分钟）
        Integer sumDurationByMemberId(Integer memberId);

        // 统计日期范围内总打卡次数
        Integer countByDateRange(
                        @Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate);

                        
}
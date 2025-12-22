package com.gym.club.controller;

import com.gym.club.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/report")
public class ReportController {
    @Autowired
    private ReportService reportService;

    /**
     * 会员出勤率分析
     */
    @GetMapping("/attendance-analysis")
    public Map<String, Object> getAttendanceAnalysis(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        if (startDate == null) {
            startDate = LocalDate.now().minusMonths(1);
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }

        return reportService.getAttendanceAnalysis(startDate, endDate);
    }

    /**
     * 热门课程排行榜
     */
    @GetMapping("/popular-courses")
    public Object getPopularCourses(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        if (startDate == null) {
            startDate = LocalDate.now().minusMonths(1);
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }

        return reportService.getPopularCoursesTop10(startDate, endDate);
    }

    /**
     * 俱乐部收支趋势
     */
    @GetMapping("/revenue-trend")
    public Map<String, Object> getRevenueTrend(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        if (startDate == null) {
            startDate = LocalDate.now().minusMonths(6);
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }

        return reportService.getRevenueTrend(startDate, endDate);
    }

    /**
     * 会员统计数据
     */
    @GetMapping("/member-statistics")
    public Map<String, Object> getMemberStatistics() {
        return reportService.getMemberStatistics();
    }

    /**
     * 教练工作数据
     */
    @GetMapping("/coach-work-data/{coachId}")
    public Map<String, Object> getCoachWorkData(
            @PathVariable Integer coachId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        if (startDate == null) {
            startDate = LocalDate.now().minusMonths(1);
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }

        return reportService.getCoachWorkData(coachId, startDate, endDate);
    }
}
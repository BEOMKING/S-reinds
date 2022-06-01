package com.project.autonomous.team.controller;

import com.project.autonomous.team.dto.request.SchedulePostReq;
import com.project.autonomous.team.dto.response.CalendarRes;
import com.project.autonomous.team.entity.Calendar;
import com.project.autonomous.team.service.TeamCalendarService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("/teams/calendar")
public class TeamCalendarController {

    @Autowired
    TeamCalendarService teamCalendarService;

    @PostMapping("/{teamId}")
    @Operation(summary = "일정 등록", description = "일정 등록")
    public ResponseEntity<Calendar> createCalendar(@PathVariable("teamId") long teamId, @RequestBody SchedulePostReq schedulePostReq){
        return ResponseEntity.ok(teamCalendarService.create(schedulePostReq, teamId));
    }

    @PutMapping("/{calendarId}")
    @Operation(summary = "일정 수정", description = "일정 수정")
    public ResponseEntity<Calendar> modifyCalendar(@PathVariable("calendarId") long calendarId, @RequestBody SchedulePostReq schedulePostReq){
        return ResponseEntity.ok(teamCalendarService.modify(schedulePostReq, calendarId));
    }

    @DeleteMapping("/{calendarId}")
    @Operation(summary = "일정 삭제", description = "일정 삭제")
    public ResponseEntity<Boolean> deleteCalendar(@PathVariable("calendarId") long calendarId){
        return ResponseEntity.ok(teamCalendarService.delete(calendarId));
    }

    @GetMapping("/{calendarId}")
    @Operation(summary = "일정 조회", description = "일정 조회(1개)")
    public ResponseEntity<CalendarRes> getCalendar(@PathVariable("calendarId") long calendarId){
        return ResponseEntity.ok(teamCalendarService.getSchedule(calendarId));
    }

    @GetMapping("/{teamId}/{year}/{month}")
    @Operation(summary = "일정 월별 조회", description = "일정 월별 조회(목록)")
    public ResponseEntity<ArrayList<CalendarRes>> getCalendarList(@PathVariable("teamId") long teamId, @PathVariable("year") int year, @PathVariable("month") int month){
        return ResponseEntity.ok(teamCalendarService.getScheduleList(teamId, year, month));
    }
}

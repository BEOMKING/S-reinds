package com.project.autonomous.team.repository;

import com.project.autonomous.team.entity.Calendar;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.ArrayList;

public interface CalendarRepository extends JpaRepository<Calendar, Long> {
    ArrayList<Calendar> findAllByTeamIdAndYearAndMonth(long teamId, int year, int month);
}
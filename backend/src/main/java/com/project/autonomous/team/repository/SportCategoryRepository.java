package com.project.autonomous.team.repository;

import com.project.autonomous.team.entity.SportCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SportCategoryRepository extends JpaRepository<SportCategory, Long> {

    Optional<SportCategory> findByName(String name);
}
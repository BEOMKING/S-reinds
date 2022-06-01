package com.project.autonomous.user.repository;

import com.project.autonomous.user.entity.User;
import com.project.autonomous.user.entity.UserInterest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public interface UserInterestRepository extends JpaRepository<UserInterest, Long> {
    Optional<ArrayList<UserInterest>> findAllByUserInterestIdUserId(long userId);

    // 유저의 관심 종목
    List<UserInterest> findAllByUserInterestIdUser(User user);

    // 현재 유저의 관심 종목 초기화
    void deleteAllByUserInterestIdUser(User user);
}
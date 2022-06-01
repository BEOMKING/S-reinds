package com.project.autonomous.user.repository;

import com.project.autonomous.team.entity.Team;
import com.project.autonomous.user.entity.User;
import com.project.autonomous.user.entity.UserTeam;
import com.project.autonomous.user.entity.UserTeamId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserTeamRepository extends CrudRepository<UserTeam, UserTeamId> {
    Optional<UserTeam> findByUserId(long userId);
    Optional<UserTeam> findByTeamId(long teamId);
    Optional<UserTeam> findByUserIdAndTeamId(long userId, long teamId);
    ArrayList<UserTeam> findAllByTeamId(long teamId);

    // 유저가 가입된 팀 슬라이싱
    @Query("select ut.team from UserTeam ut where ut.user = :user")
    Slice<Team> findTeamByUser(@Param("user") User user, Pageable pageable);

    // 유저가 가입된 팀 정보
    List<UserTeam> findAllByUser(User user);

    // 유저가 가입된 팀 리스트
    @Query("select ut.team from UserTeam ut where ut.user = :user")
    List<Team> findTeamByUserList(@Param("user") User user);
}

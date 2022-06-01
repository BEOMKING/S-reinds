package com.project.autonomous.user.entity;

import com.project.autonomous.team.entity.Team;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@IdClass(UserTeamId.class)
public class UserTeam {

    @Id
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Id
    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;

    LocalDateTime registerDate;
    String authority;
}

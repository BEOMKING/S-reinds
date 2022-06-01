package com.project.autonomous.team.entity;

import com.project.autonomous.common.entity.BaseEntity;
import com.project.autonomous.picture.entity.Picture;
import com.project.autonomous.user.entity.UserTeam;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Team extends BaseEntity {
    Long leaderId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "picture_id")
    private Picture picture;

    String name;
    LocalDateTime createDate;
    int memberCount;
    int maxCount;
    String description;
    boolean recruitmentState;
    boolean membershipFee;
    String city;
    Long sportCategoryId;


    @OneToMany(mappedBy = "team")
    private List<UserTeam> userTeamList = new ArrayList<>();
}

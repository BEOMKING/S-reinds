package com.project.autonomous.matchboard.posts.entity;

import com.project.autonomous.common.entity.BaseEntity;
import com.project.autonomous.common.entity.City;
import com.project.autonomous.matchboard.comments.entity.MatchBoardComment;
import com.project.autonomous.matchboard.posts.dto.request.MatchBoardUpdateReq;
import com.project.autonomous.team.entity.SportCategory;
import com.project.autonomous.team.entity.Team;
import com.project.autonomous.user.entity.User;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class MatchBoardPost extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_id")
    private User user;

    private LocalDateTime createdAt;

    private boolean recruited;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sport_category_id", nullable = false)
    private SportCategory sportCategory;

    @Enumerated(EnumType.STRING)
    private MatchBoardCategory matchBoardCategory;

    private LocalDate playingTime;

    @Enumerated(EnumType.STRING)
    private City city;

    private String place;

    private String recruitmentCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    private String content;

    @OneToMany(mappedBy = "matchBoardPost" , cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MatchBoardComment> comments = new ArrayList<>();

    public void update(MatchBoardUpdateReq matchBoardUpdateReq, SportCategory sportCategory, Team team) {
        this.recruited = matchBoardUpdateReq.isRecruited();
        this.sportCategory = sportCategory;
        this.matchBoardCategory = MatchBoardCategory.from(matchBoardUpdateReq.getMatchBoardCategory());
        this.playingTime = matchBoardUpdateReq.getPlayingTime();
        this.city = City.from(matchBoardUpdateReq.getCity());
        this.place = matchBoardUpdateReq.getPlace();
        this.recruitmentCount = matchBoardUpdateReq.getRecruitmentCount();
        this.team = team;
        this.content = matchBoardUpdateReq.getContent();
    }
}

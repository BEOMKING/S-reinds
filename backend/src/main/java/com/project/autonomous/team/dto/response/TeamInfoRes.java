package com.project.autonomous.team.dto.response;

import com.project.autonomous.team.entity.Team;
import com.project.autonomous.user.dto.response.UserSimpleInfoRes;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class TeamInfoRes {
    long id;
    String name;
    LocalDateTime createDate;
    UserSimpleInfoRes leader;
//    long leaderId;
//    String leaderName;
    String pictureDownloadUrl;
    int memberCount;
    int maxCount;
    String description;
    Boolean recruitmentState;
    Boolean membershipFee;
    String city;
    String sportCategory;

}

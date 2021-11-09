package com.project.autonomous.team.dto.response;

import com.project.autonomous.user.dto.response.UserSimpleInfoRes;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class MemberListRes {
//    long userId;
//    String name;
    UserSimpleInfoRes user;
    String email;
    String authority;
    LocalDateTime registerDate;
}

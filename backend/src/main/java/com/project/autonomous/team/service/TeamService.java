package com.project.autonomous.team.service;

import com.project.autonomous.team.dto.request.ApplyPostReq;
import com.project.autonomous.team.dto.request.AuthorityPostReq;
import com.project.autonomous.team.dto.request.TeamCreatePostReq;
import com.project.autonomous.team.dto.request.TeamModifyPostReq;
import com.project.autonomous.team.dto.response.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.util.ArrayList;

public interface TeamService {
    TeamInfoRes create(TeamCreatePostReq teamInfo) throws IOException;

    Page<TeamListRes> getList(Pageable pageable);

    Page<TeamListRes> getChooseList(String cityName, String sportCategory, Pageable pageable);

    TeamInfoRes modify(TeamModifyPostReq teamInfo, long teamId) throws IOException;

    boolean delete(long teamId);

    TeamInfoRes getTeamInfo(long teamId);

    void apply(long teamId, long userId, ApplyPostReq applyPostReq);

    ArrayList<ApplyListRes> applyList(long teamId);

    ArrayList<MemberListRes> memberList(long teamId);

    boolean permit(long teamId, long userId);

    void quit(long teamId);

    void kickout(long teamId, long userId);

    AuthorityRes checkAuthority(long teamId);

    boolean giveAuthority(long teamId, long userId, AuthorityPostReq authorityPostReq);

}

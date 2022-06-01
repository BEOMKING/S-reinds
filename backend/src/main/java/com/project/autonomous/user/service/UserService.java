package com.project.autonomous.user.service;


import com.project.autonomous.user.dto.request.CheckPasswordReq;
import com.project.autonomous.user.dto.request.InterestReq;
import com.project.autonomous.user.dto.request.UserModifyReq;
import com.project.autonomous.user.dto.response.MyInfoRes;
import com.project.autonomous.user.dto.response.UserInfoRes;
import com.project.autonomous.user.dto.response.UserInterestRes;
import com.project.autonomous.user.dto.response.UserTeamListRes;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.io.IOException;
import java.util.List;

public interface UserService {

    boolean checkPassword(CheckPasswordReq checkPasswordReq);

    void changePassword(CheckPasswordReq checkPasswordReq);

    MyInfoRes modifyUser(UserModifyReq modifyInfo) throws IOException;

    Slice<UserTeamListRes> getMyTeams(Pageable pageable);

    MyInfoRes getMyInfo();

    List<UserInterestRes> getMyInterest();

    void deleteUser();

    UserInfoRes getUserInfo(Long userId);

    List<UserInterestRes> updateInterest(InterestReq interestReq);
}

package com.project.autonomous.team.service;

import com.project.autonomous.common.exception.CustomException;
import com.project.autonomous.common.exception.ErrorCode;
import com.project.autonomous.jwt.util.SecurityUtil;
import com.project.autonomous.notification.entity.NoticeType;
import com.project.autonomous.notification.service.NotificationService;
import com.project.autonomous.picture.entity.Picture;
import com.project.autonomous.picture.repository.PictureRepository;
import com.project.autonomous.picture.service.DBFileStorageService;
import com.project.autonomous.team.dto.request.ApplyPostReq;
import com.project.autonomous.team.dto.request.AuthorityPostReq;
import com.project.autonomous.team.dto.request.TeamCreatePostReq;
import com.project.autonomous.team.dto.request.TeamModifyPostReq;
import com.project.autonomous.team.dto.response.*;
import com.project.autonomous.team.entity.RequestJoin;
import com.project.autonomous.team.entity.Team;
import com.project.autonomous.team.repository.RequestJoinRepository;
import com.project.autonomous.team.repository.SportCategoryRepository;
import com.project.autonomous.team.repository.TeamRepository;
import com.project.autonomous.team.repository.TeamRepositorySupport;
import com.project.autonomous.user.dto.response.UserSimpleInfoRes;
import com.project.autonomous.user.entity.User;
import com.project.autonomous.user.entity.UserInterest;
import com.project.autonomous.user.entity.UserTeam;
import com.project.autonomous.user.repository.UserInterestRepository;
import com.project.autonomous.user.repository.UserRepository;
import com.project.autonomous.user.repository.UserTeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class TeamServiceImpl implements TeamService{

    @Autowired
    TeamRepository teamRepository;
    @Autowired
    UserTeamRepository userTeamRepository;

    @Autowired
    SportCategoryRepository sportCategoryRepository;

    @Autowired
    UserInterestRepository userInterestRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PictureRepository pictureRepository;

    @Autowired
    RequestJoinRepository requestJoinRepository;

    @Autowired
    TeamRepositorySupport teamRepositorySupport;

    private final DBFileStorageService dbFileStorageService;

    private final NotificationService notificationService;


    @Override
    @Transactional
    public TeamInfoRes create(TeamCreatePostReq teamInfo) throws IOException {
        long userId = SecurityUtil.getCurrentMemberId();

        Team team = new Team();
        team.setName(teamInfo.getName());
        team.setCreateDate(LocalDateTime.now());//현재시각 받는거면 수정
        team.setLeaderId(userId);//만약에 입력에 userId받으면 그냥 Req에 leaderId주석풀면 됨
        team.setMemberCount(1);
        team.setMaxCount(teamInfo.getMaxCount());
        team.setDescription(teamInfo.getDescription());
        team.setSportCategoryId(sportCategoryRepository.findByName(teamInfo.getSportCategory()).get().getId());
        team.setCity(teamInfo.getCity());
        team.setRecruitmentState(teamInfo.getRecruitmentState());
        team.setMembershipFee(teamInfo.getMembershipFee());

        Picture picture;
        if (teamInfo.getFile() == null) {
            picture = null;
        } else {
            picture = dbFileStorageService.storeFile(teamInfo.getFile());
        }
        team.setPicture(picture);
        team = teamRepository.save(team);

        UserTeam userTeam = new UserTeam();
        userTeam.setUser(userRepository.getById(userId));
        userTeam.setTeam(team);
        userTeam.setRegisterDate(LocalDateTime.now());
        userTeam.setAuthority("대표");
        userTeamRepository.save(userTeam);

        return TeamInfoRes.from(team, userRepository.findById(userId).get(), teamInfo.getSportCategory());
    }


    @Override
    public Page<TeamListRes> getList(Pageable pageable) {
        long userId = SecurityUtil.getCurrentMemberId();

        ArrayList<UserInterest> interests = userInterestRepository.findAllByUserInterestIdUserId(userId).get();
        if(interests.isEmpty()){
            throw new CustomException(ErrorCode.NO_INTERESTING_ITEMS);
        }
        ArrayList<Long> sportsCategory = new ArrayList<>();
        for(UserInterest userInterest : interests){
            sportsCategory.add(userInterest.getUserInterestId().getSportCategory().getId());
        }

        String city = userRepository.findById(userId).get().getCity().value();
        Page<TeamListRes> teams = teamRepositorySupport.getList(sportsCategory,city,pageable);

        if(teams.isEmpty()){
            throw new CustomException(ErrorCode.LIST_NOT_FOUND);
        }

        return teams;
    }

    @Override
    public Page<TeamListRes> getChooseList(String cityName, String sportCategoryName, Pageable pageable) {

        String city = cityName;
        ArrayList<Long> sportsCategory = new ArrayList<>();
        sportsCategory.add(sportCategoryRepository.findByName(sportCategoryName).get().getId());

        Page<TeamListRes> teams = teamRepositorySupport.getList(sportsCategory,city,pageable);

        if(teams.isEmpty()){
            throw new CustomException(ErrorCode.LIST_NOT_FOUND);
        }

        return teams;
    }

    @Override
    @Transactional
    public TeamInfoRes modify(TeamModifyPostReq teamInfo, long teamId) throws IOException {

        long userId = SecurityUtil.getCurrentMemberId();

        if(userTeamRepository.findByUserIdAndTeamId(userId, teamId).get().getAuthority().equals("매니저")||userTeamRepository.findByUserIdAndTeamId(userId, teamId).get().getAuthority().equals("대표")){
            //수정할 항목 보고 결정하기 (지금은 생성이랑 같음)
            Team team = teamRepository.findById(teamId).get();

            team.setName(teamInfo.getName());
            team.setMaxCount(teamInfo.getMaxCount());
            team.setDescription(teamInfo.getDescription());
            team.setSportCategoryId(sportCategoryRepository.findByName(teamInfo.getSportCategory()).get().getId());
            team.setCity(teamInfo.getCity());
            team.setRecruitmentState(teamInfo.getRecruitmentState());
            team.setMembershipFee(teamInfo.getMembershipFee());

            Picture picture;
            if(team.getPicture() != null) {
                dbFileStorageService.deleteFile(team.getPicture().getId());
                pictureRepository.delete(team.getPicture());
            }
            if (teamInfo.getFile() == null) {
                picture = null;
            } else {
                picture = dbFileStorageService.storeFile(teamInfo.getFile());
            }
            team.setPicture(picture);


            team = teamRepository.save(team);

            return TeamInfoRes.from(team, userRepository.findById(team.getLeaderId()).get(), teamInfo.getSportCategory());
        }

        throw new CustomException(ErrorCode.AUTHORITY_NOT_FOUND);
    }

    @Override
    @Transactional
    public boolean delete(long teamId) {

        long userId = SecurityUtil.getCurrentMemberId();
        Team team = teamRepository.findById(teamId).get();

        if(team.getLeaderId().equals(userId)){

            ArrayList<UserTeam> list = userTeamRepository.findAllByTeamId(teamId);

            for(UserTeam userTeam : list){
                userTeamRepository.delete(userTeam);
            }

            teamRepository.delete(team);

            //게시물을 다 지우는지??
            return true;
        }

        throw new CustomException(ErrorCode.COMMON_MEMBER_CANNOT_REMOVE);

    }

    @Override
    public TeamInfoRes getTeamInfo(long teamId) {

        Team team = teamRepository.findById(teamId).get();
        team.setMemberCount(userTeamRepository.findAllByTeamId(teamId).size());
        User leader = userRepository.findById(team.getLeaderId()).get();
        String sportCategory = sportCategoryRepository.getById(team.getSportCategoryId()).getName();

        return TeamInfoRes.from(team, leader, sportCategory);
    }

    @Override
    public void apply(long teamId, long userId, ApplyPostReq applyPostReq) {

        long userid = SecurityUtil.getCurrentMemberId();

        if(userTeamRepository.findByUserIdAndTeamId(userId,teamId).isPresent()){
            throw new CustomException(ErrorCode.ALREADY_JOIN);
        }
        if(requestJoinRepository.findByUserIdAndTeamId(userid, teamId).isPresent()){
            throw new CustomException(ErrorCode.ALREADY_APPLY);
        }
        RequestJoin requestJoin = new RequestJoin();
        requestJoin.setUserId(userid);
        requestJoin.setTeamId(teamId);
        requestJoin.setDescription(applyPostReq.getDescription());
        requestJoin.setCreate_date(LocalDateTime.now());

        requestJoinRepository.save(requestJoin);
    }

    @Override
    public ArrayList<ApplyListRes> applyList(long teamId) {
        long userId = SecurityUtil.getCurrentMemberId();

        ArrayList<ApplyListRes> ret = new ArrayList<>();

        ArrayList<RequestJoin> list = requestJoinRepository.findAllByTeamId(teamId);

        for(RequestJoin rj : list){
            ApplyListRes applyListRes = new ApplyListRes();
            applyListRes.setUser(UserSimpleInfoRes.from(userRepository.findById(rj.getUserId()).get()));
            applyListRes.setDescription(rj.getDescription());
            applyListRes.setCreateDate(rj.getCreate_date());

            ret.add(applyListRes);
        }

        return ret;
    }

    @Override
    public ArrayList<MemberListRes> memberList(long teamId) {
        ArrayList<MemberListRes> ret = new ArrayList<>();

        ArrayList<UserTeam> list = userTeamRepository.findAllByTeamId(teamId);

        for(UserTeam userTeam : list){
            MemberListRes memberListRes = new MemberListRes();
            memberListRes.setUser(UserSimpleInfoRes.from(userTeam.getUser()));
            memberListRes.setEmail(userTeam.getUser().getEmail());
            memberListRes.setAuthority(userTeam.getAuthority());
            memberListRes.setRegisterDate(userTeam.getRegisterDate());

            ret.add(memberListRes);
        }

        return ret;
    }

    @Override
    public boolean permit(long teamId, long userId) {
        long managerId = SecurityUtil.getCurrentMemberId();
        Team team = teamRepository.findById(teamId).get();

        UserTeam userTeam = userTeamRepository.findByUserIdAndTeamId(managerId, teamId).get();
        if(userTeam.getAuthority().equals("매니저")||userTeam.getAuthority().equals("대표")){//조회하는 사람이 관리자 이상이면 가능

            if(!requestJoinRepository.findByUserIdAndTeamId(userId,teamId).isPresent()){
                throw new CustomException(ErrorCode.APPLIY_FORM_NOT_FOUND);
            }

            if(userTeamRepository.findByUserIdAndTeamId(userId,teamId).isPresent()){//예전에 탈퇴했던 사람이 다시 신청할 경우
                RequestJoin requestJoin = requestJoinRepository.findByUserIdAndTeamId(userId,teamId).get();

                requestJoinRepository.delete(requestJoin);
                throw new CustomException(ErrorCode.ALREADY_APPLY);

            }else{
                RequestJoin requestJoin = requestJoinRepository.findByUserIdAndTeamId(userId,teamId).get();
                UserTeam applyUser = new UserTeam();
                applyUser.setUser(userRepository.findById(userId).get());
                applyUser.setTeam(teamRepository.findById(teamId).get());
                applyUser.setAuthority("회원");
                applyUser.setRegisterDate(LocalDateTime.now());
                userTeamRepository.save(applyUser);

                sendNotification(findMember(userId), team);

                team.setMemberCount(userTeamRepository.findAllByTeamId(teamId).size());
                teamRepository.save(team);
                requestJoinRepository.delete(requestJoin);
            }
            return true;
        }else {
            throw new CustomException(ErrorCode.AUTHORITY_NOT_FOUND);
        }

    }

    public void sendNotification(User applier, Team team) {
        notificationService.createJoinNotification(applier, NoticeType.TEAMJOIN, team);
    }

    @Override
    public AuthorityRes checkAuthority(long teamId) {
        long userId = SecurityUtil.getCurrentMemberId();
        if(!userTeamRepository.findByUserIdAndTeamId(userId,teamId).isPresent())
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        UserTeam userTeam = userTeamRepository.findByUserIdAndTeamId(userId,teamId).get();

        AuthorityRes authorityRes = new AuthorityRes();
        authorityRes.setAuthority(userTeam.getAuthority());

        return authorityRes;
    }

    @Override
    public boolean giveAuthority(long teamId, long userId, AuthorityPostReq authorityPostReq) {
        long managerId = SecurityUtil.getCurrentMemberId();
        if(userTeamRepository.findByUserIdAndTeamId(managerId,teamId).get().getAuthority().equals("대표")){

            UserTeam userTeam = userTeamRepository.findByUserIdAndTeamId(userId,teamId).get();

            if(authorityPostReq.getAuthority().equals("대표")) {
                userTeam.setAuthority("대표");
                Team team = teamRepository.findById(teamId).get();
                team.setLeaderId(userId);
                teamRepository.save(team);

                UserTeam userTeam1 = userTeamRepository.findByUserIdAndTeamId(managerId,teamId).get();
                userTeam1.setAuthority("회원");
                userTeamRepository.save(userTeam1);

            }else if(authorityPostReq.getAuthority().equals("매니저")){
                userTeam.setAuthority("매니저");
            }else{
                userTeam.setAuthority("회원");
            }

            userTeamRepository.save(userTeam);

            return true;
        }
        throw new CustomException(ErrorCode.AUTHORITY_NOT_FOUND);
    }


    @Override
    @Transactional
    public void quit(long teamId) {
        long userId = SecurityUtil.getCurrentMemberId();
        if(!userTeamRepository.findByUserIdAndTeamId(userId,teamId).isPresent())
            throw new CustomException(ErrorCode.USER_NOT_FOUND);

        UserTeam userTeam = userTeamRepository.findByUserIdAndTeamId(userId,teamId).get();
        if(userTeam.getAuthority().equals("대표"))
            throw new CustomException(ErrorCode.CANNOT_LEAVE_LEADER);

        userTeamRepository.delete(userTeam);
        Team team = teamRepository.findById(teamId).get();
        team.setMemberCount(userTeamRepository.findAllByTeamId(teamId).size());
        teamRepository.save(team);
        return;
    }

    @Override
    @Transactional
    public void kickout(long teamId, long userId) {
        long leaderId = SecurityUtil.getCurrentMemberId();
        if(!userTeamRepository.findByUserIdAndTeamId(userId,teamId).isPresent())
            throw new CustomException(ErrorCode.USER_NOT_FOUND);

        Team team = teamRepository.findById(teamId).get();
        UserTeam userTeam = userTeamRepository.findByUserIdAndTeamId(leaderId,teamId).get();

        if(userTeam.getAuthority().equals("매니저")){//조회하는 사람이 관리자 이상이면 가능
            UserTeam kickOutUser = userTeamRepository.findByUserIdAndTeamId(userId,teamId).get();
            if(kickOutUser.getAuthority().equals("회원")) {
                userTeamRepository.delete(kickOutUser);
                team.setMemberCount(userTeamRepository.findAllByTeamId(teamId).size());
                teamRepository.save(team);
            }
            else
                throw new CustomException(ErrorCode.CANNOT_KICKOUT_MANAGER);
            return;
        }else if(userTeam.getAuthority().equals("대표")){
            UserTeam kickOutUser = userTeamRepository.findByUserIdAndTeamId(userId,teamId).get();
            if(kickOutUser.getAuthority().equals("대표"))
                throw new CustomException(ErrorCode.CANNOT_LEAVE_LEADER);
            userTeamRepository.delete(kickOutUser);
            team.setMemberCount(userTeamRepository.findAllByTeamId(teamId).size());
            teamRepository.save(team);
            return;
        }
        throw new CustomException(ErrorCode.AUTHORITY_NOT_FOUND);

    }

    public User findMember(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

}

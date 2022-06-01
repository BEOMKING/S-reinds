package com.project.autonomous.user.service;

import com.project.autonomous.common.exception.CustomException;
import com.project.autonomous.common.exception.ErrorCode;
import com.project.autonomous.jwt.util.SecurityUtil;
import com.project.autonomous.picture.entity.Picture;
import com.project.autonomous.picture.repository.PictureRepository;
import com.project.autonomous.picture.service.DBFileStorageService;
import com.project.autonomous.team.entity.SportCategory;
import com.project.autonomous.team.entity.Team;
import com.project.autonomous.team.repository.SportCategoryRepository;
import com.project.autonomous.user.dto.request.CheckPasswordReq;
import com.project.autonomous.user.dto.request.InterestReq;
import com.project.autonomous.user.dto.request.UserModifyReq;
import com.project.autonomous.user.dto.response.MyInfoRes;
import com.project.autonomous.user.dto.response.UserInfoRes;
import com.project.autonomous.user.dto.response.UserInterestRes;
import com.project.autonomous.user.dto.response.UserTeamListRes;
import com.project.autonomous.user.entity.User;
import com.project.autonomous.user.entity.UserInterest;
import com.project.autonomous.user.entity.UserInterestId;
import com.project.autonomous.user.entity.UserTeam;
import com.project.autonomous.user.repository.UserInterestRepository;
import com.project.autonomous.user.repository.UserRepository;
import com.project.autonomous.user.repository.UserTeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserTeamRepository userTeamRepository;
    private final PictureRepository pictureRepository;
    private final SportCategoryRepository sportCategoryRepository;
    private final UserInterestRepository userInterestRepository;
    private final PasswordEncoder passwordEncoder;
    private final DBFileStorageService dbFileStorageService;

    // 비밀번호 확인
    public boolean checkPassword(CheckPasswordReq checkPasswordReq) {
        String currentPass = userRepository.findById(SecurityUtil.getCurrentMemberId())
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND)).getPassword();

        return passwordEncoder.matches(checkPasswordReq.getPassword(), currentPass);
    }

    // 비밀번호 변경
    @Transactional
    public void changePassword(CheckPasswordReq checkPasswordReq) {
        User user = userRepository.findById(SecurityUtil.getCurrentMemberId())
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        user.changePassword(checkPasswordReq.encodePassword(passwordEncoder));
    }

    // 회원 수정
    @Transactional
    public MyInfoRes modifyUser(UserModifyReq userModifyReq) throws IOException {
        User user = findMember(SecurityUtil.getCurrentMemberId());

        Picture picture;
        if(user.getPicture() != null) {
            dbFileStorageService.deleteFile(user.getPicture().getId());
            pictureRepository.delete(user.getPicture());
        }
        if (userModifyReq.getFile() == null) {
            picture = null;
        } else {
            picture = dbFileStorageService.storeFile(userModifyReq.getFile());
        }

        user.update(userModifyReq, picture);
        return MyInfoRes.from(user);
    }

    // 나의 팀 조회 (무한 스크롤)
    public Slice<UserTeamListRes> getMyTeams(Pageable pageable) {
        Slice<Team> teams = userTeamRepository.findTeamByUser(
            findMember(SecurityUtil.getCurrentMemberId()), pageable);
        return teams.map(p -> UserTeamListRes.from(p));
    }

    // 나의 개인 정보 조회
    public MyInfoRes getMyInfo() {
        return MyInfoRes.from(findMember(SecurityUtil.getCurrentMemberId()));
    }

    // 유저 관심 목록 조회
    public List<UserInterestRes> getMyInterest() {
        List<UserInterest> interests = userInterestRepository.findAllByUserInterestIdUser(
            findMember(SecurityUtil.getCurrentMemberId()));
        return interests.stream()
            .map(UserInterestRes::from)
            .collect(Collectors.toList());
    }

    // 유저 관심 목록 업데이트
    @Transactional
    public List<UserInterestRes> updateInterest(InterestReq interestReq) {
        User user = findMember(SecurityUtil.getCurrentMemberId());
        userInterestRepository.deleteAllByUserInterestIdUser(user);

        if (interestReq.getInterests() == null) {
            return getMyInterest();
        }

        for (String interest : interestReq.getInterests()) {
            SportCategory sportCategory = findSportCategory(interest);
            UserInterestId id = new UserInterestId(user, sportCategory);

            userInterestRepository.save(new UserInterest(id));
        }
        return getMyInterest();
    }

    @Transactional
    public void deleteUser() {
        User user = findMember(SecurityUtil.getCurrentMemberId());

        // 소유자인 동호회 확인, 동호회 탈퇴 로직
        List<UserTeam> userTeams = userTeamRepository.findAllByUser(user);
        for (UserTeam userTeam : userTeams) {
            if (userTeam.getAuthority().equals("대표")) {
                throw new CustomException(ErrorCode.STILL_YOU_HAVE_SREINEDS);
            }
            userTeamRepository.delete(userTeam);
        }
        // 본인이 작성한 게시글, 댓글 삭제 로직

        userRepository.delete(user);
    }

    // 다른 유저 조회
    public UserInfoRes getUserInfo(Long userId) {
        return UserInfoRes.from(findMember(userId));
    }

    public User findMember(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

    public SportCategory findSportCategory(String name) {
        return sportCategoryRepository.findByName(name)
            .orElseThrow(() -> new CustomException(ErrorCode.SPORT_CATEGORY_NOT_FOUND));
    }
}
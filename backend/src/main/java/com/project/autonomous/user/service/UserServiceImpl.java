package com.project.autonomous.user.service;

import com.project.autonomous.common.entity.City;
import com.project.autonomous.common.exception.CustomException;
import com.project.autonomous.common.exception.ErrorCode;
import com.project.autonomous.jwt.util.SecurityUtil;
import com.project.autonomous.picture.repository.PictureRepository;
import com.project.autonomous.team.entity.Team;
import com.project.autonomous.team.repository.SportCategoryRepository;
import com.project.autonomous.user.dto.request.CheckPasswordReq;
import com.project.autonomous.user.dto.request.InterestReq;
import com.project.autonomous.user.dto.request.UserModifyPutReq;
import com.project.autonomous.user.dto.response.MyProfileRes;
import com.project.autonomous.user.dto.response.UserProfileRes;
import com.project.autonomous.user.dto.response.UserTeamListRes;
import com.project.autonomous.user.entity.Interest;
import com.project.autonomous.user.entity.User;
import com.project.autonomous.user.entity.UserTeam;
import com.project.autonomous.user.repository.InterestRepository;
import com.project.autonomous.user.repository.UserRepository;
import com.project.autonomous.user.repository.UserRepositorySupport;
import com.project.autonomous.user.repository.UserTeamRepository;
import java.util.ArrayList;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserRepositorySupport userRepositorySupport;

    @Autowired
    UserTeamRepository userTeamRepository;

    @Autowired
    PictureRepository pictureRepository;

    @Autowired
    SportCategoryRepository sportCategoryRepository;

    @Autowired
    InterestRepository interestRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Boolean emailCheck(String email) {
        User user = new User();
        boolean check = userRepository.findByEmail(email).isPresent();

        if (check) {//이미 있는 이메일
            return false;
        }
        return true;
    }

    public boolean checkPassword(CheckPasswordReq checkPasswordReq) {
        String currentPass = userRepository.findById(SecurityUtil.getCurrentMemberId())
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND)).getPassword();

        return passwordEncoder.matches(checkPasswordReq.getPassword(), currentPass);
    }

    @Override
    public User modifyUser(Long userId, UserModifyPutReq modifyInfo) {
        SecurityUtil.getCurrentMemberId();
        User user = userRepository.findById(userId).get();
        user.setName(modifyInfo.getName());
        user.setBirth(modifyInfo.getBirth());
        user.setPhone(modifyInfo.getPhone());
        user.setGender(modifyInfo.getGender());
        user.setCity(City.from(modifyInfo.getCity()));
        user.setPicture_id(modifyInfo.getUuid());
        userRepository.save(user);
        return null;
    }

    @Override
    public User deleteUser(Long userId) {
        return null;
    }

    @Override
    public MyProfileRes getMyProfile() {
        long userId = SecurityUtil.getCurrentMemberId();

        User user = userRepository.findById(userId).get();

        MyProfileRes res = new MyProfileRes();
        res.setId(userId);
        res.setEmail(user.getEmail());
        res.setName(user.getName());
        res.setBirth(user.getBirth());
        res.setPhone(user.getPhone());
        res.setGender(user.getGender());
        res.setCity(user.getCity().toString());

        ArrayList<UserTeamListRes> teamList = new ArrayList<>();
        for (UserTeam userTeam : userTeamRepository.findAll()) {
            if (userTeam.getUser().equals(user)) {
                Team team = userTeam.getTeam();
                UserTeamListRes utl = new UserTeamListRes();
                utl.setId(team.getId());
                utl.setName(team.getName());
//                utl.setPictureDownloadUri(pictureRepository.findById(team.getPicture_id()).get().getDownload_uri());

                teamList.add(utl);
            }
        }

        //사진 주소 넣기
//        res.setPictureDownloadUri(pictureRepository.findById(user.getPicture_id()).get().getDownload_uri());

        return res;
    }

    @Override
    public UserProfileRes getUserProfile(Long userId) {
        User user = userRepository.findById(userId).get();

        UserProfileRes res = new UserProfileRes();
        res.setEmail(user.getEmail());
        res.setName(user.getName());
        res.setBirth(user.getBirth());
        res.setPhone(user.getPhone());
        res.setCity(user.getCity().toString());

        return res;
    }

    @Override
    public User getUser(String userEmail) {
        User user = userRepository.findByEmail(userEmail).get();

        if (user == null)
            return null;
        return user;
    }

    @Override
    public void interest(InterestReq interestReq) {
        long userId = SecurityUtil.getCurrentMemberId();

        for(String name : interestReq.getSportCategory()){
            long sportCategoryId = sportCategoryRepository.findByName(name).get().getId();
            Interest interest = new Interest();
            interest.setSportCategoryId(sportCategoryId);
            interest.setUserId(userId);

            interestRepository.save(interest);
        }

        return;
    }


}
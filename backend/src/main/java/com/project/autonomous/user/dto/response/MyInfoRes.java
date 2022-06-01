package com.project.autonomous.user.dto.response;

import com.project.autonomous.picture.entity.Picture;
import com.project.autonomous.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Schema(title = "내 정보 조회", description = "유저가 본인의 정보를 조회하기 위한 Response Dto Class")
@Getter
@Builder
@AllArgsConstructor
public class MyInfoRes {

    @Schema(description = "유저 id", example = "3")
    private Long id;

    @Schema(description = "유저 email", example = "qjawlsqjacks@naver.com")
    private String email;

    @Schema(description = "유저명", example = "3")
    private String name;

    @Schema(description = "생년월일", example = "1995-09-06")
    private LocalDate birth;

    @Schema(description = "성별", example = "남")
    private String gender;

    @Schema(description = "전화번호", example = "01028732329")
    private String phone;

    @Schema(description = "지역", example = "서울")
    private String city;

    @Schema(description = "유저 사진", example = "https://matchhere.s3.ap-northeast-2.amazonaws.com/%EA%%B8_EC%A7%84.JPG")
    private String pictureUrl;

    public void setPictureUrl(Picture picture){
        if(picture == null) {
            this.pictureUrl = null;
            return;
        }
        this.pictureUrl = picture.getImageUrl();
    }

    public static MyInfoRes from(User user){
        return MyInfoRes.builder()
            .id(user.getId())
            .email(user.getEmail())
            .name(user.getName())
            .birth(user.getBirth())
            .gender(user.getGender())
            .phone(user.getPhone())
            .city(user.getCity().toString())
            .pictureUrl(user.getPicture() == null ? null : user.getPicture().getImageUrl())
            .build();
    }

}

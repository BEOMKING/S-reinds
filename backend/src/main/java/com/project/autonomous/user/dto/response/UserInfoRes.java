package com.project.autonomous.user.dto.response;

import com.project.autonomous.picture.entity.Picture;
import com.project.autonomous.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;


@Schema(title = "다른 유저 정보 조회", description = "다른 유저 정보를 조회하기 위한 Response Dto Class")
@Getter
@Builder
@AllArgsConstructor
public class UserInfoRes {

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

    @Schema(description = "지역", example = "서울")
    private String city;

    @Schema(description = "유저 사진", example = "경로 아직")
    private String pictureUrl;

    public void setPictureUrl(Picture picture){
        if(picture == null) {
            this.pictureUrl = null;
            return;
        }
        this.pictureUrl = picture.getImageUrl();
    }

    public static UserInfoRes from(User user){
        return UserInfoRes.builder()
            .id(user.getId())
            .email(user.getEmail())
            .name(user.getName())
            .birth(user.getBirth())
            .gender(user.getGender())
            .city(user.getCity().toString())
            .pictureUrl(user.getPicture() == null ? null : user.getPicture().getImageUrl())
            .build();
    }

}

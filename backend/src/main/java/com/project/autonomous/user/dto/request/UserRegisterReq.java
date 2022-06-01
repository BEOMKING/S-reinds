package com.project.autonomous.user.dto.request;

import com.project.autonomous.common.entity.City;
import com.project.autonomous.picture.entity.Picture;
import com.project.autonomous.user.entity.User;
import com.project.autonomous.user.entity.UserAuthority;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;

import static com.project.autonomous.common.exception.ValidatorMessage.*;

@Getter
@AllArgsConstructor
public class UserRegisterReq {

    @Schema(description = "Email", example = "k5d106@naver.com")
    @NotBlank(message = EMPTY_MESSAGE)
    @Email(message = EMAIL_MESSAGE)
    private String email;

    @Schema(description = "이름", example = "박범진")
    @NotBlank(message = EMPTY_MESSAGE)
    @Length(min = 2, max = 45, message = USER_NAME_MESSAGE)
    private String name;

    @Schema(description = "생년월일", example = "1995-09-06")
    @NotNull(message = EMPTY_MESSAGE)
    @DateTimeFormat(pattern = DATE_FORMAT)
    private LocalDate birth;

    @Schema(description = "성별 남, 여", example = "남")
    @NotBlank(message = EMPTY_MESSAGE)
    private String gender;

    @Schema(description = "휴대폰 번호", example = "01028732329")
    @Pattern(regexp = USER_PHONE_FORMAT, message = USER_PHONE_MESSAGE)
    private String phone;

    @Schema(description = "서울특별시, 부산광역시, 부천시 등", example = "서울특별시")
    @NotBlank(message = EMPTY_MESSAGE)
    private String city;

    @Schema(description = "패스워드 숫자, 문자, 특수 문자 포함 8 ~ 20자", example = "qwer1234^")
    @NotNull(message = EMPTY_MESSAGE)
    @Pattern(regexp = USER_PW_FORMAT, message = USER_PW_MESSAGE)
    private String password;

    @Schema(description = "사진 파일", example = "MultiPart/from-data")
    private MultipartFile file;

    public User toUser(PasswordEncoder passwordEncoder, Picture picture) {
        return User.builder()
            .email(email)
            .name(name)
            .birth(birth)
            .gender(gender)
            .phone(phone)
            .city(City.from(city))
            .password(passwordEncoder.encode(password))
            .userAuthority(UserAuthority.ROLE_USER)
            .picture(picture)
            .build();
    }
}

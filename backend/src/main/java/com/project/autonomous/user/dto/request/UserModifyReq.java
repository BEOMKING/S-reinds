package com.project.autonomous.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import static com.project.autonomous.common.exception.ValidatorMessage.*;

@Schema(title = "유저 수정 요청", description = "유저가 본인의 정보를 수정하기 위한 Request Dto Class")
@Getter
@AllArgsConstructor
public class UserModifyReq {

    @Schema(description = "이름", example = "박범진")
    @NotBlank(message = EMPTY_MESSAGE)
    @Length(min = 2, max = 45, message = USER_NAME_MESSAGE)
    private String name;

    @Schema(description = "휴대폰 번호", example = "01028732329")
    @Pattern(regexp = USER_PHONE_FORMAT, message = USER_PHONE_MESSAGE)
    private String phone;

    @Schema(description = "서울특별시, 부산광역시, 부천시 등", example = "서울특별시")
    @NotBlank(message = EMPTY_MESSAGE)
    private String city;

    @Schema(description = "사진 파일", example = "MultiPart/from-data")
    private MultipartFile file;

}

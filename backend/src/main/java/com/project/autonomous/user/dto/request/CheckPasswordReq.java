package com.project.autonomous.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import static com.project.autonomous.common.exception.ValidatorMessage.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CheckPasswordReq {

    @Schema(description = "확인할 비밀번호", example = "qwer1234^")
    @NotNull(message = EMPTY_MESSAGE)
    @Pattern(regexp = USER_PW_FORMAT, message = USER_PW_MESSAGE)
    private String password;

    public String encodePassword(PasswordEncoder passwordEncoder){
        return passwordEncoder.encode(this.password);
    }

}

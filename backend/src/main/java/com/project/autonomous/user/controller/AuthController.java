package com.project.autonomous.user.controller;

import com.project.autonomous.common.exception.ErrorResponse;
import com.project.autonomous.jwt.dto.TokenDto;
import com.project.autonomous.user.dto.request.AuthCode;
import com.project.autonomous.user.dto.request.LoginReq;
import com.project.autonomous.user.dto.request.UserRegisterReq;
import com.project.autonomous.user.service.AuthService;
import com.project.autonomous.user.service.EmailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;

@Tag(name = "Auth", description = "토큰 없는 유저 API")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final EmailService emailService;

    @InitBinder
    public void InitBinder(WebDataBinder dataBinder) {
        StringTrimmerEditor stringTrimmerEditor = new StringTrimmerEditor(true);
        dataBinder.registerCustomEditor(String.class, stringTrimmerEditor);
    }

    @PostMapping(path = "/sign-up", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    @Operation(summary = "회원 가입", description = "<strong>입력 받은 정보</strong>를 사용해 회원 가입한다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "정상 가입",
            content = @Content(schema = @Schema(implementation = TokenDto.class))),
        @ApiResponse(responseCode = "400", description = "ALREADY_JOIN\n\nBAD_REQUEST",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    public ResponseEntity<TokenDto> signUp(@Valid @ModelAttribute UserRegisterReq userRegisterReq, BindingResult theBindingResult)
        throws IOException {
        return ResponseEntity.ok(authService.signup(userRegisterReq));
    }

    @GetMapping("/sign-up/{email}")
    @Operation(summary = "중복 이메일 확인", description = "<strong>받은 이메일 주소</strong>를 사용해 중복을 확인한다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "true = 이미 존재하는 이메일\n\nfalse = 사용 가능"),
    })
    public ResponseEntity<Boolean> emailCheck(@PathVariable("email") String email) {
        return ResponseEntity.ok(authService.checkEmail(email));
    }

    @PostMapping("/login")
    @Operation(summary = "로그인", description = "<strong>받은 이메일, 패스워드</strong>를 사용해 로그인한다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "토큰 정보",
            content = @Content(schema = @Schema(implementation = TokenDto.class))),
        @ApiResponse(responseCode = "400", description = "BAD_REQUEST (이메일, 비밀번호 형식 틀림)", content = @Content),
        @ApiResponse(responseCode = "401", description = "Unauthoriezed (아이디 또는 비밀번호 틀림)", content = @Content),
    })
    public ResponseEntity<TokenDto> login(@Valid @RequestBody LoginReq loginReq) {
        return ResponseEntity.ok(authService.login(loginReq));
    }

    @GetMapping("/{email}")
    public ResponseEntity<Boolean> sendMail(@PathVariable("email") String email) {
        System.out.println("이메일 발송");

        emailService.sendMail(email);

        return ResponseEntity.status(200).body(true);
    }

    @PostMapping("/{email}")
    public ResponseEntity<Boolean> checkMail(@PathVariable("email") String email,
        @RequestBody AuthCode authCode) {
        System.out.println("이메일 확인");

        if (emailService.checkCode(authCode, email)) {

            return ResponseEntity.status(200).body(true);
        }
        return ResponseEntity.status(500).body(false);
    }


}

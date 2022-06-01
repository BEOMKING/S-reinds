package com.project.autonomous.user.controller;


import com.project.autonomous.common.exception.ErrorResponse;
import com.project.autonomous.user.dto.request.CheckPasswordReq;
import com.project.autonomous.user.dto.request.InterestReq;
import com.project.autonomous.user.dto.request.UserModifyReq;
import com.project.autonomous.user.dto.response.MyInfoRes;
import com.project.autonomous.user.dto.response.UserInfoRes;
import com.project.autonomous.user.dto.response.UserInterestRes;
import com.project.autonomous.user.dto.response.UserTeamListRes;
import com.project.autonomous.user.service.EmailService;
import com.project.autonomous.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@Tag(name = "User", description = "토큰이 필요한 유저 API")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Autowired
    EmailService emailService;

    @InitBinder
    public void InitBinder(WebDataBinder dataBinder) {
        StringTrimmerEditor stringTrimmerEditor = new StringTrimmerEditor(true);
        dataBinder.registerCustomEditor(String.class, stringTrimmerEditor);
    }

    @PostMapping("/password")
    @Operation(summary = "비밀번호 확인 (비밀번호 수정 페이지에 사용)", description = "<strong>입력 받은 비밀번호</strong>를 사용해 현 사용자의 비밀번호와 비교한다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "true, false", content = @Content),
        @ApiResponse(responseCode = "404", description = "USER_NOT_FOUND",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    public ResponseEntity<Boolean> checkPassword(
        @Valid @RequestBody CheckPasswordReq checkPasswordReq) {
        return ResponseEntity.ok(userService.checkPassword(checkPasswordReq));
    }

    @PutMapping("/password")
    @Operation(summary = "비밀번호 수정 (비밀번호 수정 페이지에 사용)", description = "<strong>입력 받은 비밀번호</strong>를 사용해 사용자의 비밀번호를 변경한다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "변경되었습니다.", content = @Content),
        @ApiResponse(responseCode = "404", description = "USER_NOT_FOUND",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    public ResponseEntity<String> changePassword(
        @Valid @RequestBody CheckPasswordReq checkPasswordReq) {
        userService.changePassword(checkPasswordReq);
        return ResponseEntity.ok("변경되었습니다.");
    }

    @PutMapping(consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    @Operation(summary = "회원 정보 수정 (회원 정보 수정 페이지에 사용)", description = "<strong>입력 받은 정보</strong>를 사용해 유저 정보를 변경한다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "변경된 회원 정보 조회",
            content = @Content(schema = @Schema(implementation = MyInfoRes.class))),
        @ApiResponse(responseCode = "400", description = "BAD_REQUEST"),
        @ApiResponse(responseCode = "404", description = "USER_NOT_FOUND\n\nDELETED_USER",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    public ResponseEntity<MyInfoRes> modifyUserInfo(
        @Valid @ModelAttribute UserModifyReq modifyInfo, BindingResult theBindingResult)
        throws IOException {
        return ResponseEntity.ok(userService.modifyUser(modifyInfo));
    }

    @GetMapping("/team")
    @Operation(summary = "나의 팀 정보 조회", description = "<strong>페이징</strong>을 사용해 유저의 팀 정보를 조회한다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "팀 정보 조회",
            content = @Content(schema = @Schema(implementation = UserTeamListRes.class))),
        @ApiResponse(responseCode = "400", description = "BAD_REQUEST"),
        @ApiResponse(responseCode = "404", description = "USER_NOT_FOUND\n\nDELETED_USER",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    public ResponseEntity<Slice<UserTeamListRes>> getMyTeams(@ParameterObject
        @PageableDefault(size = 5) Pageable pageable) {
        return ResponseEntity.ok(userService.getMyTeams(pageable));
    }

    @GetMapping("/me")
    @Operation(summary = "나의 개인 정보 조회", description = "유저의 개인 정보를 조회한다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "개인 정보 조회",
            content = @Content(schema = @Schema(implementation = MyInfoRes.class))),
        @ApiResponse(responseCode = "400", description = "BAD_REQUEST"),
        @ApiResponse(responseCode = "404", description = "USER_NOT_FOUND\n\nDELETED_USER",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    public ResponseEntity<MyInfoRes> getMyInfo() {
        return ResponseEntity.ok(userService.getMyInfo());
    }

    @GetMapping("/{userId}")
    @Operation(summary = "다른 유저의 개인 정보 조회", description = "다른 유저의 개인 정보를 조회한다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "다른 유저 개인 정보 조회",
            content = @Content(schema = @Schema(implementation = UserInfoRes.class))),
        @ApiResponse(responseCode = "400", description = "BAD_REQUEST"),
        @ApiResponse(responseCode = "404", description = "USER_NOT_FOUND",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    public ResponseEntity<UserInfoRes> getUserInfo(@PathVariable("userId") long userId) {
        return ResponseEntity.ok(userService.getUserInfo(userId));
    }

    @GetMapping("/interest")
    @Operation(summary = "나의 관심 정보 목록 조회", description = "관심 정보 목록을 조회한다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "관심 정보 목록 조회",
            content = @Content(schema = @Schema(implementation = UserInterestRes.class))),
        @ApiResponse(responseCode = "400", description = "BAD_REQUEST"),
        @ApiResponse(responseCode = "404", description = "USER_NOT_FOUND\n\nDELETED_USER",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    public ResponseEntity<List<UserInterestRes>> getMyInterest() {
        return ResponseEntity.ok(userService.getMyInterest());
    }

    @PutMapping("/interest")
    @Operation(summary = "나의 관심 정보 목록 업데이트", description = "관심 정보 목록을 생성, 및 수정한다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "변경된 관심 정보 목록 조회",
            content = @Content(schema = @Schema(implementation = UserInterestRes.class))),
        @ApiResponse(responseCode = "404", description = "USER_NOT_FOUND\n\nSPORT_CATEGORY_NOT_FOUND",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    public ResponseEntity<List<UserInterestRes>> updateInterest(@RequestBody InterestReq interestReq) {
        return ResponseEntity.ok(userService.updateInterest(interestReq));
    }

    @DeleteMapping
    @Operation(summary = "회원 탈퇴", description = "비밀번호 확인이 끝난 회원이 탈퇴한다. (본인이 소유자인 스렌즈가 있다면 탈퇴 불가, 속한 동호회는 탈퇴)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "탈퇴되었습니다.", content = @Content),
        @ApiResponse(responseCode = "400", description = "STILL_YOU_HAVE_SREINEDS",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "USER_NOT_FOUND",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    public ResponseEntity<String> deleteUser() {
        userService.deleteUser();
        return ResponseEntity.ok("탈퇴되었습니다.");
    }

}

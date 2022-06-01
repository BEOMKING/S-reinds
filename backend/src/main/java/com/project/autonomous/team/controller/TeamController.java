package com.project.autonomous.team.controller;

import com.project.autonomous.team.dto.request.ApplyPostReq;
import com.project.autonomous.team.dto.request.AuthorityPostReq;
import com.project.autonomous.team.dto.request.TeamCreatePostReq;
import com.project.autonomous.team.dto.request.TeamModifyPostReq;
import com.project.autonomous.team.dto.response.*;
import com.project.autonomous.team.service.TeamService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.util.ArrayList;

@RestController
@RequestMapping("/teams")
public class TeamController {

    @Autowired
    TeamService teamService;


    @InitBinder
    public void InitBinder(WebDataBinder dataBinder) {
        StringTrimmerEditor stringTrimmerEditor = new StringTrimmerEditor(true);
        dataBinder.registerCustomEditor(String.class, stringTrimmerEditor);
    }

    @PostMapping(path ="/", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    @Operation(summary = "팀 생성", description = "<strong>입력 받은 정보</strong>를 사용해 팀을 생성한다.")
    public ResponseEntity<TeamInfoRes> createTeam(@Valid @ModelAttribute TeamCreatePostReq teamInfo, BindingResult theBindingResult) throws IOException {
        return ResponseEntity.ok(teamService.create(teamInfo));
    }

    @GetMapping("/list")
    @Operation(summary = "팀 리스트 조회 (회원 정보를 바탕으로 가입할 팀을 찾을때)", description = "<strong>회원가입할때 입력한 city와 관심 종목</strong>을 사용해 팀 목록을 조회한다.")
    public ResponseEntity<Page<TeamListRes>> getTeamList(@PageableDefault(size = 10) Pageable pageable){

        return ResponseEntity.ok(teamService.getList(pageable));
    }

    @GetMapping("/list/{city}/{sportCategory}")
    @Operation(summary = "팀 리스트 조회 (입력을 바탕으로 가입할 팀을 찾을때)", description = "<strong>city와 관심 종목을 선택해서</strong> 사용해 팀 목록을 조회한다.")
    public ResponseEntity<Page<TeamListRes>> getTeamListChoose(@PathVariable("city") String city, @PathVariable("sportCategory") String sportCategory, @PageableDefault(size = 10) Pageable pageable){

        Page<TeamListRes> list = teamService.getChooseList(city, sportCategory, pageable);
        if(list.isEmpty()){
            return ResponseEntity.status(400).body(list);
        }

        return ResponseEntity.ok(list);
    }

    @PutMapping(path ="/{teamId}", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    @Operation(summary = "팀 정보 수정", description = "<strong>입력 받은 정보</strong>를 사용해 팀 정보를 변경한다.")
    public ResponseEntity<TeamInfoRes> modifyTeam(@PathVariable("teamId") long teamId, @Valid @ModelAttribute TeamModifyPostReq teamInfo, BindingResult theBindingResult) throws IOException {

        return ResponseEntity.ok(teamService.modify(teamInfo, teamId));
    }

    @DeleteMapping("/{teamId}")
    @Operation(summary = "팀 삭제", description = "팀을 삭제한다. (리더만 가능), userteam 목록에 있는 모든 회원을 삭제")
    public ResponseEntity<Boolean> deleteTeam(@PathVariable("teamId") long teamId){
        System.out.println("팀 삭제");

        if(teamService.delete(teamId)){
            return ResponseEntity.ok(true);
        }
        return ResponseEntity.status(400).body(false);

    }

    @GetMapping("/{teamId}")
    @Operation(summary = "팀 정보 조회", description = "팀 정보 조회")
    public ResponseEntity<TeamInfoRes> getTeamInfo(@PathVariable("teamId") long teamId){

        TeamInfoRes teamInfoRes = teamService.getTeamInfo(teamId);

        return ResponseEntity.status(200).body(teamInfoRes);
    }


    @PostMapping("/{teamId}/{userId}")
    @Operation(summary = "팀 가입 신청", description = "팀 가입 신청(중복 신청 안됨)")
    public void apply(@PathVariable("teamId") long teamId, @PathVariable("userId") long userId, @RequestBody ApplyPostReq applyPostReq){

        teamService.apply(teamId, userId, applyPostReq);
        return;
    }

    @GetMapping("/apply-list/{teamId}")
    @Operation(summary = "팀 가입 신청 리스트 조회", description = "팀 가입 신청 승인을 받아주기 위한 리스트 조회")
    public ResponseEntity<ArrayList<ApplyListRes>> applyList(@PathVariable("teamId") long teamId){

        ArrayList<ApplyListRes> users = teamService.applyList(teamId);

        return ResponseEntity.ok(users);
    }

    @PostMapping("/permit/{teamId}/{userId}")
    @Operation(summary = "팀 가입 승인", description = "팀 가입 신청 승인 매니저 이상만 가능")
    public ResponseEntity<Boolean> permit(@PathVariable("teamId") long teamId, @PathVariable("userId") long userId){
        System.out.println("팀 가입 승인");

        if(teamService.permit(teamId, userId)){
            return ResponseEntity.ok(true);
        }

        return ResponseEntity.status(400).body(false);
    }


    @GetMapping("/{teamId}/me")
    @Operation(summary = "본인 권한 조회", description = "팀 내에서의 본인의 권한 조회")
    public ResponseEntity<AuthorityRes> checkAuthortiy(@PathVariable("teamId") long teamId){
        return ResponseEntity.ok(teamService.checkAuthority(teamId));
    }

    @PutMapping("/manager/{teamId}/{userId}")
    @Operation(summary = "팀 관리자 권한 부여", description = "매니저 권한 주기 (리더만 가능)")
    public ResponseEntity<Boolean> giveAuthority(@PathVariable("teamId") long teamId, @PathVariable("userId") long userId, @RequestBody AuthorityPostReq authorityPostReq){

        if(teamService.giveAuthority(teamId,userId,authorityPostReq)){
           return ResponseEntity.ok(true);
        }
        return ResponseEntity.status(400).body(false);
    }

    @GetMapping("/member-list/{teamId}")
    @Operation(summary = "팀 회원 리스트 조회", description = "팀 회원 리스트 조회")
    public ResponseEntity<ArrayList<MemberListRes>> memberList(@PathVariable("teamId") long teamId){

        return ResponseEntity.ok(teamService.memberList(teamId));
    }

    @DeleteMapping("/{teamId}/me")
    @Operation(summary = "팀 탈퇴", description = "팀 탈퇴")
    public void quit(@PathVariable("teamId") long teamId){
        teamService.quit(teamId);
        return;
    }

    @DeleteMapping("/kick-out/{teamId}/{userId}")
    @Operation(summary = "팀 강퇴", description = "회원을 팀에서 강퇴(매니저 이상만 가능)")
    public void kickOut(@PathVariable("teamId") long teamId, @PathVariable("userId") long userId){
        teamService.kickout(teamId,userId);
        return;
    }



}

package com.project.autonomous.matchboard.posts.controller;

import com.project.autonomous.common.exception.ErrorResponse;
import com.project.autonomous.matchboard.posts.dto.request.MatchBoardCreateReq;
import com.project.autonomous.matchboard.posts.dto.request.MatchBoardUpdateReq;
import com.project.autonomous.matchboard.posts.dto.response.MatchBoardPostInfoRes;
import com.project.autonomous.matchboard.posts.dto.response.MatchBoardPostSimpleInfoRes;
import com.project.autonomous.matchboard.posts.service.MatchBoardPostService;
import com.project.autonomous.user.dto.response.UserTeamListRes;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Tag(name = "MatchBoardPost", description = "매치 게시글 API")
@RestController
@RequestMapping("/match/board/posts")
@RequiredArgsConstructor
public class MatchBoardPostController {

    private final MatchBoardPostService matchBoardPostService;

    @GetMapping("/team")
    @Operation(summary = "나의 팀 정보 조회", description = "<strong>매치 게시글 생성</strong>을 위해 유저의 팀 정보를 조회한다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "팀 정보 조회",
            content = @Content(schema = @Schema(implementation = UserTeamListRes.class))),
        @ApiResponse(responseCode = "400", description = "BAD_REQUEST",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "USER_NOT_FOUND\n\nDELETED_USER",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    public ResponseEntity<List<UserTeamListRes>> getMyTeams() {
        return ResponseEntity.ok(matchBoardPostService.getMyTeams());
    }

    @PostMapping
    @Operation(summary = "게시글 생성", description = "<strong>입력 받은 정보</strong>를 사용해 게시글을 생성한다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "게시글 상세 정보",
            content = @Content(schema = @Schema(implementation = MatchBoardPostInfoRes.class))),
        @ApiResponse(responseCode = "404", description = "USER_NOT_FOUND\n\nTEAM_NOT_FOUND\n\n"
            + "SPORT_CATEGORY_NOT_FOUND\n\nBOARD_NOT_FOUND\n\nBOARD_CATEGORY_NOT_FOUND",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    public ResponseEntity<MatchBoardPostInfoRes> createPost(
        @Valid @RequestBody MatchBoardCreateReq matchBoardCreateReq) {
        return ResponseEntity.ok(matchBoardPostService.createPost(matchBoardCreateReq));
    }

    @GetMapping("/info/{postId}")
    @Operation(summary = "게시글 상세 정보 조회", description = "<strong>게시글 id</strong>를 사용해 게시글 정보를 조회한다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "게시글 정보 조회",
            content = @Content(schema = @Schema(implementation = MatchBoardPostInfoRes.class))),
        @ApiResponse(responseCode = "404", description = "BOARD_NOT_FOUND",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    public ResponseEntity<MatchBoardPostInfoRes> getPostInfo(@PathVariable("postId") Long postId) {
        return ResponseEntity.ok(matchBoardPostService.getPostInfo(postId));
    }

    @PutMapping("/{postId}")
    @Operation(summary = "게시글 수정", description = "<strong>입력 받은 정보</strong>를 사용해 게시글을 수정한다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "변경된 게시글 상세 정보",
            content = @Content(schema = @Schema(implementation = MatchBoardPostInfoRes.class))),
        @ApiResponse(responseCode = "404", description = "USER_NOT_FOUND\n\nTEAM_NOT_FOUND\n\n"
            + "SPORT_CATEGORY_NOT_FOUND\n\nBOARD_NOT_FOUND\n\nBOARD_CATEGORY_NOT_FOUND",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    public ResponseEntity<MatchBoardPostInfoRes> updatePost(@PathVariable("postId") Long postId,
        @Valid @RequestBody MatchBoardUpdateReq matchBoardUpdateReq) {
        return ResponseEntity.ok(matchBoardPostService.updatePost(postId, matchBoardUpdateReq));
    }

    @DeleteMapping("/{postId}")
    @Operation(summary = "게시글 삭제", description = "<strong>입력 받은 정보</strong>를 사용해 게시글을 삭제한다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "삭제되었습니다.", content = @Content),
    })
    public ResponseEntity<String> deletePost(@PathVariable("postId") Long postId) {
        matchBoardPostService.deletePost(postId);
        return ResponseEntity.ok("삭제되었습니다.");
    }

    @GetMapping("/all/{cities}/{sportCategories}/{matchBoardCategories}")
    @Operation(summary = "게시글 전체 조회", description = "<strong>주어진 조건(지역, 스포츠, 매치)</strong>를 사용해 조건에 맞는 게시글을 리스트 조회한다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "게시글 리스트 조회",
            content = @Content(schema = @Schema(implementation = MatchBoardPostSimpleInfoRes.class))),
    })
    public ResponseEntity<Page<MatchBoardPostSimpleInfoRes>> getAllCondition(
        @PathVariable(value = "cities") List<String> cities,
        @PathVariable(value = "sportCategories") List<String> sportCategories,
        @PathVariable(value = "matchBoardCategories") List<String> matchBoardCategories,
        @ParameterObject @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(matchBoardPostService.getAllCondition(cities, sportCategories, matchBoardCategories, pageable));
    }

}

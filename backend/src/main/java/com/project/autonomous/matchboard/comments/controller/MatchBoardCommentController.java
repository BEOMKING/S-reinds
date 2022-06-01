package com.project.autonomous.matchboard.comments.controller;

import com.project.autonomous.common.exception.ErrorResponse;
import com.project.autonomous.matchboard.comments.dto.request.MatchBoardCreateCommentReq;
import com.project.autonomous.matchboard.comments.dto.request.MatchBoardUpdateCommentReq;
import com.project.autonomous.matchboard.comments.dto.response.MatchBoardCommentRes;
import com.project.autonomous.matchboard.comments.service.MatchBoardCommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Tag(name = "MatchBoardComment", description = "매치 게시글 댓글 API")
@RestController
@RequestMapping("/match/board/comments")
@RequiredArgsConstructor
public class MatchBoardCommentController {

    private final MatchBoardCommentService matchBoardCommentService;

    @PostMapping("/{postId}")
    @Operation(summary = "댓글 생성", description = "<strong>입력 받은 댓글 id와 댓글 정보</strong>를 사용해 댓글을 생성한다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "댓글 정보",
            content = @Content(schema = @Schema(implementation = MatchBoardCommentRes.class))),
        @ApiResponse(responseCode = "404", description = "USER_NOT_FOUND\n\nBOARD_NOT_FOUND\n\nCOMMENT_NOT_FOUND",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    public ResponseEntity<MatchBoardCommentRes> createComment(@PathVariable("postId") Long postId,
        @Valid @RequestBody MatchBoardCreateCommentReq req) {
        return ResponseEntity.ok(matchBoardCommentService.createComment(postId, req));
    }

    @GetMapping("/{postId}/{parentId}")
    @Operation(summary = "댓글 정보 리스트 조회", description = "<strong>댓글 id와 부모 id</strong>를 사용해 댓글 정보를 조회한다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "댓글 정보 리스트 조회",
            content = @Content(schema = @Schema(implementation = MatchBoardCommentRes.class))),
        @ApiResponse(responseCode = "404", description = "BOARD_NOT_FOUND",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    public ResponseEntity<List<MatchBoardCommentRes>> getAllComment(
        @PathVariable("postId") Long postId, @PathVariable("parentId") Long parentId) {
        return ResponseEntity.ok(matchBoardCommentService.getAllComment(postId, parentId));
    }

    @PutMapping("/{commentId}")
    @Operation(summary = "댓글 수정", description = "<strong>입력 받은 정보</strong>를 사용해 댓글을 수정한다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "변경된 댓글 정보",
            content = @Content(schema = @Schema(implementation = MatchBoardCommentRes.class))),
        @ApiResponse(responseCode = "401", description = "UNAUTHORIZED_CHANGE",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "USER_NOT_FOUND\n\nCOMMENT_NOT_FOUND",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    public ResponseEntity<MatchBoardCommentRes> updatePost(
        @PathVariable("commentId") Long commentId, @Valid @RequestBody MatchBoardUpdateCommentReq req) {
        return ResponseEntity.ok(matchBoardCommentService.updateComment(commentId, req));
    }

    @DeleteMapping("/{commentId}")
    @Operation(summary = "댓글 삭제", description = "<strong>댓글 id</strong>를 사용해 댓글을 삭제한다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "삭제되었습니다.", content = @Content),
        @ApiResponse(responseCode = "401", description = "UNAUTHORIZED_CHANGE",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "USER_NOT_FOUND\n\nCOMMENT_NOT_FOUND",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    public ResponseEntity<String> deleteComment(@PathVariable("commentId") Long commentId) {
        matchBoardCommentService.deleteComment(commentId);
        return ResponseEntity.ok("삭제되었습니다.");
    }

}

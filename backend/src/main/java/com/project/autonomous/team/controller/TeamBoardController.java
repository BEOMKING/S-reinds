package com.project.autonomous.team.controller;

import com.project.autonomous.team.dto.request.CommentModifyPutReq;
import com.project.autonomous.team.dto.request.CommentPostReq;
import com.project.autonomous.team.dto.request.PostingReq;
import com.project.autonomous.team.dto.response.CommentListRes;
import com.project.autonomous.team.dto.response.CommentRes;
import com.project.autonomous.team.dto.response.PostViewRes;
import com.project.autonomous.team.entity.TeamBoardComment;
import com.project.autonomous.team.service.TeamBoardService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/teams/board")
public class TeamBoardController {

    @Autowired
    TeamBoardService teamBoardService;

    @PostMapping("/{teamId}")
    @Operation(summary = "공지사항 등록", description = "팀 내 게시판 글 쓰기")
    public ResponseEntity<PostViewRes> createPost(@PathVariable("teamId") long teamId, @RequestBody PostingReq postingReq){
        return ResponseEntity.ok(teamBoardService.posting(postingReq, teamId));

    }

    @PutMapping("/{teamId}/{boardId}")
    @Operation(summary = "공지사항 수정", description = "팀 내 게시판 글 수정(본인만 가능)")
    public ResponseEntity<PostViewRes> modifyPost(@PathVariable("teamId") long teamId, @PathVariable("boardId") long boardId, @RequestBody PostingReq postingReq){
        return ResponseEntity.ok(teamBoardService.postingModify(postingReq, teamId, boardId));

    }

    @DeleteMapping("/{teamId}/{boardId}")
    @Operation(summary = "공지사항 삭제", description = "팀 내 게시판 글 삭제(본인만 가능)")
    public ResponseEntity<Boolean> deletePost(@PathVariable("teamId") long teamId, @PathVariable("boardId") long boardId){
        return ResponseEntity.ok(teamBoardService.postingDelete(teamId, boardId));

    }

    @GetMapping("/{teamId}/{boardId}")
    @Operation(summary = "공지사항 조회", description = "팀 내 게시판 글 조회(한 개)")
    public ResponseEntity<PostViewRes> getPost(@PathVariable("teamId") long teamId, @PathVariable("boardId") long boardId){
        return ResponseEntity.ok(teamBoardService.postingView(teamId, boardId));

    }

    @GetMapping("/{teamId}")
    @Operation(summary = "공지사항 목록 조회", description = "팀 내 게시판 글 목록 조회(여러개)")
    public ResponseEntity<Page<PostViewRes>> getPostList(@PathVariable("teamId") long teamId, @PageableDefault(size = 10) Pageable pageable){
        return ResponseEntity.ok(teamBoardService.postingViewList(teamId, pageable));
    }

    @PostMapping("/{boardId}/comments")
    @Operation(summary = "댓글 작성", description = "댓글 작성(대댓글도 같음)")
    public ResponseEntity<TeamBoardComment> comment(@PathVariable("boardId") long boardId, @RequestBody CommentPostReq commentPostReq){
        return ResponseEntity.ok(teamBoardService.comment(commentPostReq, boardId));
    }

    @PutMapping("/comments/{commentId}")
    @Operation(summary = "댓글 수정", description = "댓글 수정 (작성한 본인만 가능)")
    public ResponseEntity<TeamBoardComment> modifyComment(@PathVariable("commentId") long commentId, @RequestBody CommentModifyPutReq commentModifyPutReq){
        return ResponseEntity.ok(teamBoardService.modifyComment(commentModifyPutReq, commentId));
    }

    @DeleteMapping("/comments/{commentId}")
    @Operation(summary = "댓글 삭제", description = "댓글 삭제(작성한 본인만 가능)")
    public ResponseEntity<Boolean> deleteComment(@PathVariable("commentId") long commentId){
        return ResponseEntity.ok(teamBoardService.deleteComment(commentId));
    }

    @GetMapping("/comments/{commentId}")
    @Operation(summary = "댓글 조회", description = "댓글 조회(1개)")
    public ResponseEntity<CommentRes> getComment(@PathVariable("commentId") long commentId){
        return ResponseEntity.ok(teamBoardService.getComment(commentId));
    }

    @GetMapping("/{boardId}/comments/{parentId}")
    @Operation(summary = "댓글 목록 조회", description = "댓글 목록 조회(부모 없으면 0)")
    public ResponseEntity<CommentListRes> getCommentsList(@PathVariable("boardId") long boardId, @PathVariable("parentId") long parentId){
        return ResponseEntity.ok(teamBoardService.getCommentList(boardId, parentId));
    }

}

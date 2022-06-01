package com.project.autonomous.matchboard.comments.dto.request;

import com.project.autonomous.matchboard.comments.entity.MatchBoardComment;
import com.project.autonomous.matchboard.posts.entity.MatchBoardPost;
import com.project.autonomous.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

import static com.project.autonomous.common.exception.ValidatorMessage.EMPTY_MESSAGE;

@Getter
public class MatchBoardCreateCommentReq {

    @Schema(description = "부모 아이디", example = "0 부모 댓글, 이외 대댓글")
    @NotNull(message = EMPTY_MESSAGE)
    private Long parentId;

    @Schema(description = "댓글 내용", example = "용병 신청합니다.")
    @NotBlank(message = EMPTY_MESSAGE)
    @Size(max = 500)
    private String content;

    public MatchBoardComment toMatchBoardComment(MatchBoardPost matchBoardPost, User user) {
        return MatchBoardComment.builder()
            .matchBoardPost(matchBoardPost)
            .user(user)
            .content(content)
            .createdAt(LocalDateTime.now())
            .parentId(parentId)
            .replyCount(0)
            .modified(false)
            .build();
    }
}

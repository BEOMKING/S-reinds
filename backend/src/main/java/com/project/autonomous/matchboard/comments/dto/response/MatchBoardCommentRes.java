package com.project.autonomous.matchboard.comments.dto.response;

import com.project.autonomous.matchboard.comments.entity.MatchBoardComment;
import com.project.autonomous.user.dto.response.UserSimpleInfoRes;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class MatchBoardCommentRes {

    @Schema(description = "댓글 id", example = "5")
    private Long id;

    @Schema(description = "작성자 정보", example = "[\"id\" : 4, \"name\" : \"박범진\", \"city\" : \"대구광역시\", \"pictureUrl\" : \"https://matchhere.s3.ap-northeast-2.amazonaws.com/%EA%%B8_EC%A7%84.JPG\"]")
    private UserSimpleInfoRes writer;

    @Schema(description = "댓글 내용", example = "농구 같이 해요")
    private String content;

    @Schema(description = "작성일", example = "2021-11-08T18:36:54")
    private LocalDateTime createdAt;

    @Schema(description = "댓글의 깊이", example = "0 부모 or 1 대댓글")
    private int depth;

    @Schema(description = "부모 id", example = "3")
    private long parentId;

    @Schema(description = "답글 수", example = "3")
    private int replyCount;

    @Schema(description = "수정 여부(수정됨)을 위한", example = "true")
    private boolean modified;

    public static MatchBoardCommentRes from(MatchBoardComment matchBoardComment) {
        return MatchBoardCommentRes.builder()
            .id(matchBoardComment.getId())
            .writer(UserSimpleInfoRes.from(matchBoardComment.getUser()))
            .content(matchBoardComment.getContent())
            .createdAt(matchBoardComment.getCreatedAt())
            .depth(matchBoardComment.getDepth())
            .parentId(matchBoardComment.getParentId())
            .replyCount(matchBoardComment.getReplyCount())
            .modified(matchBoardComment.isModified())
            .build();
    }

}

package com.project.autonomous.matchboard.comments.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import static com.project.autonomous.common.exception.ValidatorMessage.EMPTY_MESSAGE;

@Getter
public class MatchBoardUpdateCommentReq {

    @Schema(description = "댓글 내용", example = "용병 신청합니다.")
    @NotBlank(message = EMPTY_MESSAGE)
    @Size(max = 500)
    private String content;

}

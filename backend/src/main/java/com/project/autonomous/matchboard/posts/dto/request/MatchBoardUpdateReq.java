package com.project.autonomous.matchboard.posts.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import java.time.LocalDate;

import static com.project.autonomous.common.exception.ValidatorMessage.DATE_FORMAT;
import static com.project.autonomous.common.exception.ValidatorMessage.EMPTY_MESSAGE;

@Getter
public class MatchBoardUpdateReq {

    @Schema(description = "모집 상태", example = "false(모집 중)")
    private boolean recruited;

    @Schema(description = "종목명", example = "농구")
    @NotBlank(message = EMPTY_MESSAGE)
    private String sportCategory;

    @Schema(description = "게시판 유형", example = "매칭 or 용병")
    @NotBlank(message = EMPTY_MESSAGE)
    private String matchBoardCategory;

    @Schema(description = "경기 날짜", example = "1995-09-06")
    @DateTimeFormat(pattern = DATE_FORMAT)
    private LocalDate playingTime;

    @Schema(description = "지역", example = "서울특별시")
    @NotBlank(message = EMPTY_MESSAGE)
    private String city;

    @Schema(description = "경기 장소", example = "인동초등학교")
    private String place;

    @Schema(description = "모집 인원", example = "4 ~ 10명")
    private String recruitmentCount;

    @Schema(description = "팀 id", example = "3")
    private Long teamId;

    @Schema(description = "내용", example = "5 : 5 농구 인원구합니다.")
    private String content;

}

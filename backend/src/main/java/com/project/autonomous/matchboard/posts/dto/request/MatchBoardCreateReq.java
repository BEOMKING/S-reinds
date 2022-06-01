package com.project.autonomous.matchboard.posts.dto.request;

import com.project.autonomous.common.entity.City;
import com.project.autonomous.matchboard.posts.entity.MatchBoardCategory;
import com.project.autonomous.matchboard.posts.entity.MatchBoardPost;
import com.project.autonomous.team.entity.SportCategory;
import com.project.autonomous.team.entity.Team;
import com.project.autonomous.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.project.autonomous.common.exception.ValidatorMessage.DATE_FORMAT;
import static com.project.autonomous.common.exception.ValidatorMessage.EMPTY_MESSAGE;

@Getter
public class MatchBoardCreateReq {

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

    public MatchBoardPost toMatchBoardPost(User user, SportCategory sportCategory, Team team) {
        return MatchBoardPost.builder()
            .user(user)
            .createdAt(LocalDateTime.now())
            .recruited(false)
            .sportCategory(sportCategory)
            .matchBoardCategory(MatchBoardCategory.from(matchBoardCategory))
            .playingTime(playingTime)
            .city(City.from(city))
            .place(place)
            .recruitmentCount(recruitmentCount)
            .team(team)
            .content(content)
            .build();
    }

}

package com.project.autonomous.matchboard.posts.dto.response;

import com.project.autonomous.matchboard.posts.entity.MatchBoardPost;
import com.project.autonomous.user.dto.response.UserSimpleInfoRes;
import com.project.autonomous.user.dto.response.UserTeamListRes;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.project.autonomous.common.exception.ValidatorMessage.DATE_FORMAT;
import static com.project.autonomous.common.exception.ValidatorMessage.EMPTY_MESSAGE;

@Schema(title = "매치 게시글 상세 정보")
@Getter
@Builder
@AllArgsConstructor
public class MatchBoardPostInfoRes {

    @Schema(description = "게시글 id", example = "3")
    private long postId;

    @Schema(description = "작성자 정보", example = "[\"id\" : 4, \"name\" : \"박범진\", \"city\" : \"대구광역시\", \"pictureUrl\" : \"https://matchhere.s3.ap-northeast-2.amazonaws.com/%EA%%B8_EC%A7%84.JPG\"]")
    private UserSimpleInfoRes writer;

    @Schema(description = "작성일자", example = "2021-09-06 06:57:37.667537")
    private LocalDateTime createAt;

    @Schema(description = "모집 상태", example = "false(모집 중)")
    private boolean recruited;

    @Schema(description = "종목명", example = "농구")
    private String sportCategory;

    @Schema(description = "게시판 유형", example = "매칭 or 용병")
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

    @Schema(description = "팀 정보", example = "{\"id\": 4, \"name\":\"대명농구회\", \"pictureUrl\":\"https://matchhere.s3.ap-northeast-2.amazonaws.com/%EA%%B8_EC%A7%84.JPG\"}")
    private UserTeamListRes team;

    @Schema(description = "내용", example = "5 : 5 농구 인원구합니다.")
    private String content;

    public static MatchBoardPostInfoRes from(MatchBoardPost matchBoardPost) {
        return MatchBoardPostInfoRes.builder()
            .postId(matchBoardPost.getId())
            .writer(UserSimpleInfoRes.from(matchBoardPost.getUser()))
            .createAt(matchBoardPost.getCreatedAt())
            .recruited(matchBoardPost.isRecruited())
            .sportCategory(matchBoardPost.getSportCategory().getName())
            .matchBoardCategory(matchBoardPost.getMatchBoardCategory().toString())
            .playingTime(matchBoardPost.getPlayingTime())
            .city(matchBoardPost.getCity().toString())
            .place(matchBoardPost.getPlace())
            .recruitmentCount(matchBoardPost.getRecruitmentCount())
            .team(matchBoardPost.getTeam() == null ? null : UserTeamListRes.from(
                matchBoardPost.getTeam()))
            .content(matchBoardPost.getContent())
            .build();
    }
}

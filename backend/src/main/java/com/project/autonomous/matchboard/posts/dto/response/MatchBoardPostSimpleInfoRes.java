package com.project.autonomous.matchboard.posts.dto.response;

import com.project.autonomous.matchboard.posts.entity.MatchBoardPost;
import com.project.autonomous.user.dto.response.UserSimpleInfoRes;
import com.project.autonomous.user.entity.User;
import com.querydsl.core.annotations.QueryProjection;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

import static com.project.autonomous.common.exception.ValidatorMessage.EMPTY_MESSAGE;

@Schema(title = "매치 게시글 상세 정보")
@Getter
@Builder
@AllArgsConstructor
public class MatchBoardPostSimpleInfoRes {

    @Schema(description = "게시글 id", example = "3")
    private Long postId;

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

    @Schema(description = "지역", example = "서울특별시")
    @NotBlank(message = EMPTY_MESSAGE)
    private String city;

    @Schema(description = "모집 인원", example = "4 ~ 10명")
    private String recruitmentCount;

    @QueryProjection
    public MatchBoardPostSimpleInfoRes(Long postId,
        User user, LocalDateTime createAt, boolean recruited,
        String sportCategory, String matchBoardCategory, String city, String recruitmentCount) {
        this.postId = postId;
        this.writer = UserSimpleInfoRes.from(user);
        this.createAt = createAt;
        this.recruited = recruited;
        this.sportCategory = sportCategory;
        this.matchBoardCategory = matchBoardCategory;
        this.city = city;
        this.recruitmentCount = recruitmentCount;
    }

    public static MatchBoardPostSimpleInfoRes from(MatchBoardPost matchBoardPost) {
        return MatchBoardPostSimpleInfoRes.builder()
            .postId(matchBoardPost.getId())
            .writer(UserSimpleInfoRes.from(matchBoardPost.getUser()))
            .createAt(matchBoardPost.getCreatedAt())
            .recruited(matchBoardPost.isRecruited())
            .sportCategory(matchBoardPost.getSportCategory().getName())
            .matchBoardCategory(matchBoardPost.getMatchBoardCategory().toString())
            .city(matchBoardPost.getCity().toString())
            .recruitmentCount(matchBoardPost.getRecruitmentCount())
            .build();
    }
}

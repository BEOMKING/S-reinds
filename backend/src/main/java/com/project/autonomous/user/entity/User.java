package com.project.autonomous.user.entity;

import com.project.autonomous.common.entity.BaseEntity;
import com.project.autonomous.common.entity.City;
import com.project.autonomous.matchboard.comments.entity.MatchBoardComment;
import com.project.autonomous.matchboard.posts.entity.MatchBoardPost;
import com.project.autonomous.picture.entity.Picture;
import com.project.autonomous.user.dto.request.UserModifyReq;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseEntity {

    private String email;

    private String password;

    private String name;

    private LocalDate birth;

    private String gender;

    private String phone;

    @Enumerated(EnumType.STRING)
    private City city;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "picture_id")
    private Picture picture;

    @Enumerated(EnumType.STRING)
    @NotNull
    private UserAuthority userAuthority;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MatchBoardPost> matchBoardPosts = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MatchBoardComment> comments = new ArrayList<>();

    // 비밀번호 변경
    public void changePassword(String password) {
        this.password = password;
    }

    // 회원 정보 수정
    public void update(UserModifyReq userModifyReq, Picture picture) {
        this.name = userModifyReq.getName();
        this.phone = userModifyReq.getPhone();
        this.city = City.from(userModifyReq.getCity());
        this.picture = picture;
    }

}

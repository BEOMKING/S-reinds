package com.project.autonomous.user.entity;

import com.project.autonomous.team.entity.SportCategory;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserInterestId implements Serializable {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sport_category_id")
    private SportCategory sportCategory;

    public UserInterestId(User user, SportCategory sportCategory) {
        this.user = user;
        this.sportCategory = sportCategory;
    }

}

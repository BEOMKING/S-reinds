package com.project.autonomous.matchboard.posts.repository;

import com.project.autonomous.common.entity.City;
import com.project.autonomous.matchboard.posts.dto.response.MatchBoardPostSimpleInfoRes;
import com.project.autonomous.matchboard.posts.entity.MatchBoardCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MatchBoardPostQRepository {

    Page<MatchBoardPostSimpleInfoRes> searchPage(List<City> cities,
        List<String> sportsCategories, List<MatchBoardCategory> matchBoardCategories, Pageable pageable);

}

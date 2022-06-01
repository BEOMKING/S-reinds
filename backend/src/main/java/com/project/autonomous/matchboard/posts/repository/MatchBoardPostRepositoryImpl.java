package com.project.autonomous.matchboard.posts.repository;

import com.project.autonomous.common.entity.City;
import com.project.autonomous.matchboard.posts.dto.response.MatchBoardPostSimpleInfoRes;
import com.project.autonomous.matchboard.posts.dto.response.QMatchBoardPostSimpleInfoRes;
import com.project.autonomous.matchboard.posts.entity.MatchBoardCategory;
import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityManager;
import java.util.List;

import static com.project.autonomous.matchboard.posts.entity.QMatchBoardPost.matchBoardPost;

public class MatchBoardPostRepositoryImpl implements MatchBoardPostQRepository {

    private final JPAQueryFactory queryFactory;

    public MatchBoardPostRepositoryImpl(EntityManager entityManager) {
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

    public Page<MatchBoardPostSimpleInfoRes> searchPage(List<City> cities,
        List<String> sportsCategories, List<MatchBoardCategory> matchBoardCategories, Pageable pageable) {

        QueryResults<MatchBoardPostSimpleInfoRes> results = queryFactory
            .select(new QMatchBoardPostSimpleInfoRes(
                matchBoardPost.id,
                matchBoardPost.user,
                matchBoardPost.createdAt,
                matchBoardPost.recruited,
                matchBoardPost.sportCategory.name,
                matchBoardPost.matchBoardCategory.stringValue(),
                matchBoardPost.city.stringValue(),
                matchBoardPost.recruitmentCount))
            .from(matchBoardPost)
            .where(matchBoardPost.city.in(cities)
                .and(matchBoardPost.sportCategory.name.in(sportsCategories))
                .and(matchBoardPost.matchBoardCategory.in(matchBoardCategories)))
//            .orderBy()
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetchResults();

        List<MatchBoardPostSimpleInfoRes> content = results.getResults();
        long total = results.getTotal();

        return new PageImpl<>(content, pageable, total);
    }

//    private BooleanExpression cityIn(String[] city) {
//
//
//    }
//
//    private BooleanExpression sprotsCategoryIn(String[] sportsCategories) {
//
//    }
//
//    private BooleanExpression matchCategoryIn(String[] matchCategory) {
//
//    }
//
//    private BooleanExpression PostSearchCondition(String[] city, String[] sportsCategories, String matchCategory) {
//
//    }
}

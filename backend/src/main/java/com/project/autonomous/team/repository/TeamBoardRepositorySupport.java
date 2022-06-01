package com.project.autonomous.team.repository;

import com.project.autonomous.team.dto.response.PostViewRes;
import com.project.autonomous.team.entity.QTeamBoard;
import com.project.autonomous.team.entity.TeamBoard;
import com.project.autonomous.user.repository.UserRepository;
import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

@Repository
public class TeamBoardRepositorySupport {

    private final JPAQueryFactory jpaQueryFactory;
    QTeamBoard qTeamBoard = QTeamBoard.teamBoard;

    @Autowired
    UserRepository userRepository;

    public TeamBoardRepositorySupport(EntityManager entityManager) {
        this.jpaQueryFactory = new JPAQueryFactory(entityManager);
    }

    public Page<PostViewRes> getList(long teamId, Pageable pageable) {

        QueryResults<TeamBoard> results = jpaQueryFactory.select(qTeamBoard).from(qTeamBoard).where(qTeamBoard.teamId.eq(teamId)).offset(pageable.getOffset()).limit(pageable.getPageSize()).fetchResults();

        List<PostViewRes> content = new ArrayList<>();

        for(TeamBoard teamBoard : results.getResults()){
            content.add(PostViewRes.from(teamBoard, userRepository.findById(teamBoard.getWriterId()).get()));
        }

        long total = results.getTotal();


        return new PageImpl<>(content, pageable, total);
    }
}

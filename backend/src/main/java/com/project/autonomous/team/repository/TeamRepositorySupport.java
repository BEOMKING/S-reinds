package com.project.autonomous.team.repository;

import com.project.autonomous.team.dto.response.TeamListRes;
import com.project.autonomous.team.entity.QTeam;
import com.project.autonomous.team.entity.Team;
import com.project.autonomous.user.dto.response.UserSimpleInfoRes;
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
public class TeamRepositorySupport{

    private final JPAQueryFactory jpaQueryFactory;
    QTeam qTeam = QTeam.team;

    @Autowired
    UserRepository userRepository;

    @Autowired
    SportCategoryRepository sportCategoryRepository;

    public TeamRepositorySupport(EntityManager entityManager){
        this.jpaQueryFactory = new JPAQueryFactory(entityManager);
    }

    public Page<TeamListRes> getList(List<Long> sportsCategoryList, String city, Pageable pageable) {

        QueryResults<Team> results = jpaQueryFactory.select(qTeam).from(qTeam).where(qTeam.city.eq(city).and(qTeam.sportCategoryId.in(sportsCategoryList))).offset(pageable.getOffset()).limit(pageable.getPageSize()).fetchResults();

        List<TeamListRes> content = new ArrayList<>();

        for(Team team : results.getResults()){
            TeamListRes teamListRes = new TeamListRes();
            teamListRes.setId(team.getId());
            if(team.getPicture() == null){
                teamListRes.setPictureDownloadUri(null);
            }else{
                teamListRes.setPictureDownloadUri(team.getPicture().getImageUrl());
            }
            teamListRes.setName(team.getName());
            teamListRes.setLeader(UserSimpleInfoRes.from(userRepository.findById(team.getLeaderId()).get()));
            teamListRes.setDescription(team.getDescription());
            teamListRes.setMembershipFee(team.isMembershipFee());
            teamListRes.setMemberCount(team.getMemberCount());
            teamListRes.setSportsCategory(sportCategoryRepository.findById(team.getSportCategoryId()).get().getName());
            teamListRes.setCity(team.getCity());

            content.add(teamListRes);
        }

        long total = results.getTotal();


        return new PageImpl<>(content, pageable, total);
    }
}

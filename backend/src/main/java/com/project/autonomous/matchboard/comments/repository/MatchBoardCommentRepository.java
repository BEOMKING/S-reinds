package com.project.autonomous.matchboard.comments.repository;

import com.project.autonomous.matchboard.comments.entity.MatchBoardComment;
import com.project.autonomous.matchboard.posts.entity.MatchBoardPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MatchBoardCommentRepository extends JpaRepository<MatchBoardComment, Long> {

    // 댓글 조회 (생성일자 오름차순)
    @Query("select mbc from MatchBoardComment mbc where mbc.matchBoardPost = :matchBoardPost and mbc.parentId = :parentId order by mbc.createdAt")
    List<MatchBoardComment> findAllComment(@Param("matchBoardPost") MatchBoardPost matchBoardPost, @Param("parentId") Long parentId);
}
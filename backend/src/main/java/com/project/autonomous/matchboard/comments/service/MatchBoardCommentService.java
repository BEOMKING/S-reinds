package com.project.autonomous.matchboard.comments.service;

import com.project.autonomous.common.exception.CustomException;
import com.project.autonomous.common.exception.ErrorCode;
import com.project.autonomous.jwt.util.SecurityUtil;
import com.project.autonomous.matchboard.comments.dto.request.MatchBoardCreateCommentReq;
import com.project.autonomous.matchboard.comments.dto.request.MatchBoardUpdateCommentReq;
import com.project.autonomous.matchboard.comments.dto.response.MatchBoardCommentRes;
import com.project.autonomous.matchboard.comments.entity.MatchBoardComment;
import com.project.autonomous.matchboard.comments.repository.MatchBoardCommentRepository;
import com.project.autonomous.matchboard.posts.entity.MatchBoardPost;
import com.project.autonomous.matchboard.posts.repository.MatchBoardPostRepository;
import com.project.autonomous.notification.entity.NoticeType;
import com.project.autonomous.notification.service.NotificationService;
import com.project.autonomous.user.entity.User;
import com.project.autonomous.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MatchBoardCommentService {

    private final UserRepository userRepository;
    private final MatchBoardPostRepository matchBoardPostRepository;
    private final MatchBoardCommentRepository matchBoardCommentRepository;
    private final NotificationService notificationService;

    // 댓글 생성
    @Transactional
    public MatchBoardCommentRes createComment(Long postId, MatchBoardCreateCommentReq matchBoardCreateCommentReq) {
        MatchBoardComment parentComment = null;
        if(matchBoardCreateCommentReq.getParentId() > 0) { // 부모 댓글이 존재한다면
            parentComment = findMatchBoardComment(matchBoardCreateCommentReq.getParentId());
            parentComment.addReplyCount();
        }

        MatchBoardPost matchBoardPost = findMatchBoardPost(postId);
        User user = findMember(SecurityUtil.getCurrentMemberId());

        MatchBoardComment matchBoardComment = matchBoardCreateCommentReq.toMatchBoardComment(matchBoardPost, user);
        matchBoardComment.setDepth(matchBoardCreateCommentReq.getParentId());
        matchBoardPost.getComments().add(matchBoardComment);
        user.getComments().add(matchBoardComment);
        matchBoardCommentRepository.save(matchBoardComment);

        sendNotification(user, matchBoardPost, parentComment); // 알림

        return MatchBoardCommentRes.from(matchBoardComment);
    }

    public void sendNotification(User writer, MatchBoardPost post, MatchBoardComment parentComment) {
        if(!post.getUser().equals(writer)) { // 본인 게시글에 본인이 댓글을 남긴다면 알림 X
            if(parentComment != null) { // 대댓글이라면
                if(post.getUser().equals(parentComment.getUser())) { // 게시글의 주인과 댓글의 주인이 같다면
                    notificationService.createNotification(parentComment.getUser(), NoticeType.COMMENT, post.getId()); // 댓글 부모에게 알림
                } else {
                    notificationService.createNotification(post.getUser(), NoticeType.MATCH, post.getId()); // 게시글 주인에게 알림
                    notificationService.createNotification(parentComment.getUser(), NoticeType.COMMENT, post.getId()); // 댓글 부모에게 알림
                }
            } else {
                notificationService.createNotification(post.getUser(), NoticeType.MATCH, post.getId()); // 게시글 주인에게 알림
            }

        }
    }

    // 댓글 조회
    public List<MatchBoardCommentRes> getAllComment(Long postId, Long parentId) {
        return matchBoardCommentRepository.findAllComment(findMatchBoardPost(postId), parentId)
            .stream().map(MatchBoardCommentRes::from).collect(Collectors.toList());
    }

    // 댓글 수정
    @Transactional
    public MatchBoardCommentRes updateComment(Long commentId, MatchBoardUpdateCommentReq matchBoardUpdateCommentReq) {
        MatchBoardComment matchBoardComment = findMatchBoardComment(commentId);
        checkAuthority(SecurityUtil.getCurrentMemberId(), matchBoardComment.getUser().getId());

        matchBoardComment.changeContent(matchBoardUpdateCommentReq.getContent());
        return MatchBoardCommentRes.from(matchBoardComment);
    }

    // 댓글 삭제
    @Transactional
    public void deleteComment(Long commentId) {
        MatchBoardComment matchBoardComment = findMatchBoardComment(commentId);
        checkAuthority(SecurityUtil.getCurrentMemberId(), matchBoardComment.getUser().getId());

        // 대댓글을 제거할 시점에는 부모 댓글이 이미 삭제되어있을 수 있음
        if(matchBoardComment.getParentId() != commentId) {
            Optional<MatchBoardComment> parentComment = matchBoardCommentRepository.findById(
                matchBoardComment.getParentId());

            if(parentComment.isPresent()) {
                parentComment.get().removeReplyCount();
            }
        }
        // 자식 댓글을 가지고 있는 부모 댓글의 삭제는?
        // 유저의 탈퇴로 인한 댓글의 삭제시 부모 댓글의 replyCount는 어떻게 변경할 수 있을까?

        matchBoardCommentRepository.delete(matchBoardComment);
    }

    public User findMember(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

    public MatchBoardPost findMatchBoardPost(Long postId) {
        return matchBoardPostRepository.findById(postId)
            .orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));
    }

    public MatchBoardComment findMatchBoardComment(Long commentId) {
        return matchBoardCommentRepository.findById(commentId)
            .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));
    }

    public void checkAuthority(Long userId, Long writerId) {
        if(userId != writerId) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_CHANGE);
        }
    }

}

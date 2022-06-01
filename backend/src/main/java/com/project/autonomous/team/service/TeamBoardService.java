package com.project.autonomous.team.service;

import com.project.autonomous.common.exception.CustomException;
import com.project.autonomous.common.exception.ErrorCode;
import com.project.autonomous.jwt.util.SecurityUtil;
import com.project.autonomous.notification.entity.NoticeType;
import com.project.autonomous.notification.service.NotificationService;
import com.project.autonomous.team.dto.request.CommentModifyPutReq;
import com.project.autonomous.team.dto.request.CommentPostReq;
import com.project.autonomous.team.dto.request.PostingReq;
import com.project.autonomous.team.dto.response.CommentListRes;
import com.project.autonomous.team.dto.response.CommentRes;
import com.project.autonomous.team.dto.response.PostViewRes;
import com.project.autonomous.team.entity.TeamBoard;
import com.project.autonomous.team.entity.TeamBoardComment;
import com.project.autonomous.team.repository.TeamBoardCommentRepository;
import com.project.autonomous.team.repository.TeamBoardRepository;
import com.project.autonomous.team.repository.TeamBoardRepositorySupport;
import com.project.autonomous.user.dto.response.UserSimpleInfoRes;
import com.project.autonomous.user.entity.User;
import com.project.autonomous.user.repository.UserRepository;
import com.project.autonomous.user.repository.UserTeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TeamBoardService {

    @Autowired
    TeamBoardRepository teamBoardRepository;

    @Autowired
    UserTeamRepository userTeamRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    TeamBoardCommentRepository teamBoardCommentRepository;

    @Autowired
    TeamBoardRepositorySupport teamBoardRepositorySupport;

    private final NotificationService notificationService;

    public PostViewRes posting(PostingReq postingReq, long teamId) {
        long userId = SecurityUtil.getCurrentMemberId();
        if(!userTeamRepository.findByUserIdAndTeamId(userId, teamId).isPresent()){//공지사항은 회원만 가능
            throw new CustomException(ErrorCode.AUTHORITY_NOT_FOUND);
        }

        TeamBoard teamBoard = new TeamBoard();
        teamBoard.setWriterId(userId);
        teamBoard.setTitle(postingReq.getTitle());
        teamBoard.setContent(postingReq.getContent());
        teamBoard.setCreateDate(LocalDateTime.now());
        teamBoard.setTeamId(teamId);

        teamBoard = teamBoardRepository.save(teamBoard);

        return PostViewRes.from(teamBoard, userRepository.findById(userId).get());
    }

    public PostViewRes postingModify(PostingReq postingReq, long teamId, long boardId) {
        long userId = SecurityUtil.getCurrentMemberId();

        TeamBoard teamBoard = teamBoardRepository.findById(boardId).get();
        if(teamBoard.getWriterId() != userId){//공지사항은 회원만 가능
            throw new CustomException(ErrorCode.AUTHORITY_NOT_FOUND);
        }

        teamBoard.setTitle(postingReq.getTitle());
        teamBoard.setContent(postingReq.getContent());
        teamBoard = teamBoardRepository.save(teamBoard);

        return PostViewRes.from(teamBoard, userRepository.findById(userId).get());
    }

    public boolean postingDelete(long teamId, long boardId) {
        long userId = SecurityUtil.getCurrentMemberId();

        TeamBoard teamBoard = teamBoardRepository.findById(boardId).get();
        if(teamBoard.getWriterId() != userId){//공지사항은 회원만 가능
            return false;
        }

        teamBoardRepository.delete(teamBoard);
        return true;
    }

    public PostViewRes postingView(long teamId, long boardId) {
        long userId = SecurityUtil.getCurrentMemberId();

        TeamBoard teamBoard = teamBoardRepository.findById(boardId).get();

        return PostViewRes.from(teamBoard, userRepository.findById(teamBoard.getWriterId()).get());
    }

    public Page<PostViewRes> postingViewList(long teamId, Pageable pageable) {
        long userId = SecurityUtil.getCurrentMemberId();

        Page<PostViewRes> ret = teamBoardRepositorySupport.getList(teamId, pageable);

        if(ret.isEmpty())
            throw new CustomException(ErrorCode.LIST_NOT_FOUND);

        return ret;
    }

    public TeamBoardComment comment(CommentPostReq commentPostReq, long boardId) {
        long userId = SecurityUtil.getCurrentMemberId();

        User writer = findMember(userId);
        TeamBoard post = findTeamBoard(boardId);
        TeamBoardComment parent = null;

        if (commentPostReq.getParentId() != 0) {
            TeamBoardComment teamBoardComment = new TeamBoardComment();
            TeamBoardComment parentComment = teamBoardCommentRepository.findById(commentPostReq.getParentId()).get();
            parentComment.setReplyCount(parentComment.getReplyCount()+1);
            teamBoardCommentRepository.save(parentComment);

            parent = parentComment; // 부모 댓글

            teamBoardComment.setTeamBoardId(boardId);
            teamBoardComment.setWriterId(userId);
            teamBoardComment.setParentId(parentComment.getId());
            teamBoardComment.setDepth(2);
            teamBoardComment.setCreateDate(LocalDateTime.now());
            teamBoardComment.setModified(false);
            teamBoardComment.setModifyDate(LocalDateTime.now());
            teamBoardComment.setContent(commentPostReq.getContent());
            teamBoardComment.setReplyCount(0);

            sendNotification(writer, post, parent);

            return teamBoardCommentRepository.save(teamBoardComment);

        } else {
            TeamBoardComment teamBoardComment = new TeamBoardComment();
            teamBoardComment.setTeamBoardId(boardId);
            teamBoardComment.setWriterId(userId);
            teamBoardComment.setParentId(0);
            teamBoardComment.setDepth(1);
            teamBoardComment.setCreateDate(LocalDateTime.now());
            teamBoardComment.setModified(false);
            teamBoardComment.setModifyDate(LocalDateTime.now());//modified가 false면 이거 무시
            teamBoardComment.setContent(commentPostReq.getContent());
            teamBoardComment.setReplyCount(0);

            sendNotification(writer, post, parent);

            return teamBoardCommentRepository.save(teamBoardComment);

        }

    }

    public void sendNotification(User writer, TeamBoard post, TeamBoardComment parentComment) {
        User postWriter = findMember(post.getWriterId());
        User parentCommentWriter = null;

        if(!postWriter.equals(writer)) { // 본인 게시글에 본인이 댓글을 남긴다면 알림 X
            if(parentComment != null) { // 대댓글이라면
                parentCommentWriter = findMember(parentComment.getWriterId());
                if(postWriter.equals(parentCommentWriter)) { // 게시글의 주인과 댓글의 주인이 같다면
                    notificationService.createNotification(parentCommentWriter, NoticeType.COMMENT, post.getId()); // 댓글 부모에게 알림
                } else {
                    notificationService.createNotification(postWriter, NoticeType.TEAMBOARD, post.getId()); // 게시글 주인에게 알림
                    notificationService.createNotification(parentCommentWriter, NoticeType.COMMENT, post.getId()); // 댓글 부모에게 알림
                }
            } else {
                notificationService.createNotification(postWriter, NoticeType.TEAMBOARD, post.getId()); // 게시글 주인에게 알림
            }

        }
    }

    public TeamBoardComment modifyComment(CommentModifyPutReq commentModifyPutReq, long commentId) {
        if(!teamBoardCommentRepository.findById(commentId).isPresent()){
            throw new CustomException(ErrorCode.COMMENT_NOT_FOUND);
        }
        long userId = SecurityUtil.getCurrentMemberId();
        TeamBoardComment teamBoardComment = teamBoardCommentRepository.findById(commentId).get();

        teamBoardComment.setModified(true);
        teamBoardComment.setModifyDate(LocalDateTime.now());
        teamBoardComment.setContent(commentModifyPutReq.getContent());

        return teamBoardCommentRepository.save(teamBoardComment);
    }

    public boolean deleteComment(long commentId) {
        if(!teamBoardCommentRepository.findById(commentId).isPresent()){
            throw new CustomException(ErrorCode.COMMENT_NOT_FOUND);
        }

        teamBoardCommentRepository.deleteById(commentId);
        return true;
    }

    public CommentRes getComment(long commentId) {
        if(!teamBoardCommentRepository.findById(commentId).isPresent()){
            throw new CustomException(ErrorCode.COMMENT_NOT_FOUND);
        }

        TeamBoardComment teamBoardComment = teamBoardCommentRepository.findById(commentId).get();
        CommentRes commentRes = new CommentRes();

        commentRes.setId(teamBoardComment.getId());
        commentRes.setWriter(UserSimpleInfoRes.from(userRepository.findById(teamBoardComment.getWriterId()).get()));
//        commentRes.setName(userRepository.findById(teamBoardComment.getWriterId()).get().getName());
        commentRes.setContent(teamBoardComment.getContent());
        commentRes.setCreateDate(teamBoardComment.getCreateDate());
        commentRes.setModifyDate(teamBoardComment.getModifyDate());
        commentRes.setModified(teamBoardComment.isModified());
        commentRes.setParentId(teamBoardComment.getParentId());
        commentRes.setDepth(teamBoardComment.getDepth());
        commentRes.setReplyCount(teamBoardComment.getReplyCount());

        return commentRes;
    }

    public CommentListRes getCommentList(long boardId, long parentId) {
        System.out.println("댓글리스트 받기");
        if(!teamBoardRepository.findById(boardId).isPresent()){
            throw new CustomException(ErrorCode.BOARD_NOT_FOUND);
        }
        if(teamBoardCommentRepository.findAllByParentIdAndTeamBoardId(parentId, boardId).isEmpty()){
            throw new CustomException(ErrorCode.LIST_NOT_FOUND);
        }
        System.out.println("댓글리스트 받기2");
        CommentListRes commentListRes = new CommentListRes();

        List<TeamBoardComment> teamBoardCommentList = teamBoardCommentRepository.findAllByParentIdAndTeamBoardId(parentId,boardId);
        commentListRes.setReplyCount(teamBoardCommentList.size());

        ArrayList<CommentRes> commentResArrayList = new ArrayList<>();
        for (TeamBoardComment teamBoardComment : teamBoardCommentList){
            CommentRes commentRes = new CommentRes();
            commentRes.setId(teamBoardComment.getId());
            commentRes.setWriter(UserSimpleInfoRes.from(userRepository.findById(teamBoardComment.getWriterId()).get()));
//            commentRes.setName(userRepository.findById(teamBoardComment.getWriterId()).get().getName());
            commentRes.setContent(teamBoardComment.getContent());
            commentRes.setCreateDate(teamBoardComment.getCreateDate());
            commentRes.setModifyDate(teamBoardComment.getModifyDate());
            commentRes.setModified(teamBoardComment.isModified());
            commentRes.setParentId(teamBoardComment.getParentId());
            commentRes.setDepth(teamBoardComment.getDepth());
            commentRes.setReplyCount(teamBoardComment.getReplyCount());

            commentResArrayList.add(commentRes);
        }
        commentListRes.setCommentsList(commentResArrayList);

        return commentListRes;
    }

    public User findMember(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

    public TeamBoard findTeamBoard(Long postId) {
        return teamBoardRepository.findById(postId)
            .orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));
    }

    public TeamBoardComment findTeamBoardComment(Long commentId) {
        return teamBoardCommentRepository.findById(commentId)
            .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));
    }
}

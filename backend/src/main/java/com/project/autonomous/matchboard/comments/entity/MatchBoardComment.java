package com.project.autonomous.matchboard.comments.entity;

import com.project.autonomous.common.entity.BaseEntity;
import com.project.autonomous.matchboard.posts.entity.MatchBoardPost;
import com.project.autonomous.user.entity.User;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.time.LocalDateTime;

@Getter
@Entity
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class MatchBoardComment extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_board_post_id")
    private MatchBoardPost matchBoardPost;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_id")
    private User user;

    private String content;

    private LocalDateTime createdAt;

    private int depth;

    private long parentId;

//    private long groupId;

    private int replyCount;

    private boolean modified;

    public void addReplyCount(){
        replyCount++;
    }
    public void removeReplyCount(){
        replyCount--;
    } // 회원 탈퇴로 인해 삭제되면 어떻게?

    public void setDepth(Long parentId){
        if(parentId > 0){
            this.depth = 1;
        }else{
            this.depth = 0;
        }
        this.parentId = parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public void changeContent(String content) {
        this.content = content;
        this.modified = true;
    }

}

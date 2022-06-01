package com.project.autonomous.chat.entity;

import com.project.autonomous.common.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage extends BaseEntity {

    @ManyToOne
    @JoinColumn(name="room_id")
    private ChatRoom chatRoom; // 방번호

    private Long sender; // 메시지 보낸사람
    private String message; // 메시지
    private LocalDateTime time; // 보낸 시간

}


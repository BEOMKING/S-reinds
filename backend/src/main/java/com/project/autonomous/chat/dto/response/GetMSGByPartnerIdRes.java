package com.project.autonomous.chat.dto.response;

import com.project.autonomous.chat.entity.ChatMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class GetMSGByPartnerIdRes {

    private Long sender;

    private String message;

    private LocalDateTime time;

    public static GetMSGByPartnerIdRes from(ChatMessage message) {
        return GetMSGByPartnerIdRes.builder()
            .sender(message.getSender())
            .message(message.getMessage())
            .time(message.getTime())
            .build();
    }

}

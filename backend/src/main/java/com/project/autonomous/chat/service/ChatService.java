package com.project.autonomous.chat.service;

import com.project.autonomous.chat.dto.request.ChatMessageReq;
import com.project.autonomous.chat.dto.response.ChatRoomListRes;
import com.project.autonomous.chat.dto.response.GetMSGByPartnerIdRes;
import com.project.autonomous.chat.dto.response.GetMSGByPartnerIdRoomIdRes;

import java.util.List;

public interface ChatService {

    List<ChatRoomListRes> getChatRoomList();
    GetMSGByPartnerIdRoomIdRes getMessageByPartnerId(Long partnerId);
    List<GetMSGByPartnerIdRes> getMessageByRoomId(Long roomId);
    void saveMessage(ChatMessageReq messageReq);
}

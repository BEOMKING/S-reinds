package com.project.autonomous.chat.controller;

import com.project.autonomous.chat.dto.response.ChatRoomListRes;
import com.project.autonomous.chat.dto.response.GetMSGByPartnerIdRes;
import com.project.autonomous.chat.dto.response.GetMSGByPartnerIdRoomIdRes;
import com.project.autonomous.chat.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/chat")
public class ChatRoomController {

   private final ChatService chatRoomservice;

//    @Autowired
//    ChatRoomService chatRoomservice;

    //채팅방 전체 조회
    @GetMapping("/list")
    @Operation(summary = "본인 채팅 조회", description = "본인 채팅목록 조회")
    public ResponseEntity<List<ChatRoomListRes>> getTeamList(){

        List<ChatRoomListRes> list = chatRoomservice.getChatRoomList();
        return ResponseEntity.ok(list);
    }

    //채팅방 채팅내역 조회 by 유저상세정보에서
    @GetMapping("/room/by-user/{partnerId}")
    @Operation(summary = "채팅방 채팅내역 조회, 유저상세정보", description = "유저상세정보에서 채팅방의 채팅내역 조회시")
    public ResponseEntity<GetMSGByPartnerIdRoomIdRes> getMessageByUserInfo(@PathVariable Long partnerId){
        return ResponseEntity.ok(chatRoomservice.getMessageByPartnerId(partnerId));
    }

    //채팅방 채팅내역 조회 by 채팅리스트에서
    @GetMapping("/room/by-list/{roomId}")
    @Operation(summary = "채팅방 채팅내역 조회, 채팅리스트", description = "채팅리스트에서 채팅방의 채팅내역 조회시")
    public ResponseEntity<List<GetMSGByPartnerIdRes>> getMessageByChatList(@PathVariable Long roomId){
        return ResponseEntity.ok(chatRoomservice.getMessageByRoomId(roomId));
    }

}

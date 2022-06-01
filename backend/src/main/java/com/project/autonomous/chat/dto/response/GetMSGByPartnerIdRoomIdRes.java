package com.project.autonomous.chat.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class GetMSGByPartnerIdRoomIdRes {

    private Long roomId;

    private List<GetMSGByPartnerIdRes> list;

}

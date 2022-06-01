package com.project.autonomous.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.util.List;

@Getter
public class InterestReq {

    @Schema(description = "관심 스포츠", example = "[\"축구\", \"풋살\"]")
    private List<String> interests;

}

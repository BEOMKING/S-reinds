package com.project.autonomous.notification.controller;

import com.project.autonomous.common.exception.ErrorResponse;
import com.project.autonomous.notification.dto.NotificationRes;
import com.project.autonomous.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notice")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    @Operation(summary = "알림 리스트 조회", description = "type을 이용해 해당 postId의 게시글로 이동할 수 있게 한다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "알림 리스트 조회",
            content = @Content(schema = @Schema(implementation = NotificationRes.class))),
        @ApiResponse(responseCode = "404", description = "USER_NOT_FOUND",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    public ResponseEntity<List<NotificationRes>> getNotification() {
        return ResponseEntity.ok(notificationService.getNotification());
    }

    @PutMapping("/{notificationId}")
    @Operation(summary = "알림 읽기", description = "notificationId를 이용해 해당 알림을 읽음 처리한다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "알림 리스트 조회", content = @Content),
        @ApiResponse(responseCode = "404", description = "USER_NOT_FOUND\n\nNOTIFICATION_NOT_FOUND",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    public ResponseEntity checkNotification(@PathVariable("notificationId") Long id) {
        notificationService.checkNotification(id);
        return ResponseEntity.ok("알림 읽기");
    }

    @PutMapping
    @Operation(summary = "알림 전체 읽기", description = "해당 사용자의 전체 알림을 읽음 처리한다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "알림 리스트 조회",
            content = @Content(schema = @Schema(implementation = NotificationRes.class))),
        @ApiResponse(responseCode = "404", description = "USER_NOT_FOUND\n\nNOTIFICATION_NOT_FOUND",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    public ResponseEntity<List<NotificationRes>> checkAllNotification() {
        return ResponseEntity.ok(notificationService.checkAllNotification());
    }

    @DeleteMapping("/{notificationId}")
    @Operation(summary = "알림 제거", description = "notificationId를 이용해 해당 알림을 삭제한다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "알림 리스트 조회",
            content = @Content(schema = @Schema(implementation = NotificationRes.class))),
        @ApiResponse(responseCode = "404", description = "USER_NOT_FOUND\n\nNOTIFICATION_NOT_FOUND",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    public ResponseEntity<List<NotificationRes>> deleteNotification(@PathVariable("notificationId") Long id) {
        return ResponseEntity.ok(notificationService.deleteNotification(id));
    }

    @DeleteMapping
    @Operation(summary = "알림 전체 제거", description = "notificationId를 이용해 전체 알림을 제거한다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "알림 리스트 조회",
            content = @Content(schema = @Schema(implementation = NotificationRes.class))),
        @ApiResponse(responseCode = "404", description = "USER_NOT_FOUND",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    public ResponseEntity<List<NotificationRes>> deleteAllNotification() {
        return ResponseEntity.ok(notificationService.deleteAllNotification());
    }
}

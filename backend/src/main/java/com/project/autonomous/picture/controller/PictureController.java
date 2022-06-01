package com.project.autonomous.picture.controller;

import com.project.autonomous.picture.dto.PictureInfoDto;
import com.project.autonomous.picture.entity.Picture;
import com.project.autonomous.picture.service.DBFileStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Tag(name = "Picture", description = "사진 업로드")
@RestController
@RequestMapping("/picture")
@RequiredArgsConstructor
public class PictureController {

    private final DBFileStorageService dbFileStorageService;

    @PostMapping(path = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "단일 사진 업로드", description = "<strong>받은 multipart file</strong>를 저장한다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "파일 정보", content = @Content(schema = @Schema(implementation = PictureInfoDto.class))),
        @ApiResponse(responseCode = "400", description = "NOT_ALLOW_TYPE\n\nFileStorageException", content = @Content),
    })
    public ResponseEntity<Picture> uploadFile(@RequestPart("file") MultipartFile file)
        throws IOException {
        return ResponseEntity.ok(dbFileStorageService.storeFile(file));
    }

//    @PostMapping("/upload/multi")
//    @Operation(summary = "다중 사진 업로드", description = "<strong>받은 multipart file들</strong>을 저장한다.")
//    public ResponseEntity<List<Picture>> uploadMultipleFiles(@RequestParam("files") MultipartFile[] files) {
//        return ResponseEntity.ok(Arrays.asList(files)
//            .stream()
//            .map(file -> dbFileStorageService.storeFile(file))
//            .collect(Collectors.toList()));
//    }

//    @GetMapping("/download/{uuid}")
//    @Operation(summary = "파일 다운로드", description = "<strong>uuid로</strong>로 파일 정보를 다운로드 받는다.")
//    public ResponseEntity<Resource> downloadFile(@PathVariable String fileId) {
//        // Load file from database
//        DBFile dbFile = dbFileStorageService.getFile(fileId);
//
//        return ResponseEntity.ok()
//            .contentType(MediaType.parseMediaType(dbFile.getFile_type()))
//            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + dbFile.getFile_name() + "\"")
//            .body(new ByteArrayResource(dbFile.getData()));
//    }
//

    @DeleteMapping("/{fileName}")
    @Operation(summary = "파일 삭제", description = "<strong>받은 파일명</strong>을 사용해 삭제한다.")
    public ResponseEntity<String> deleteFile(@PathVariable("fileName") String fileName) {
        dbFileStorageService.deleteFile(fileName);
        return ResponseEntity.ok("삭제되었습니다.");
    }

}

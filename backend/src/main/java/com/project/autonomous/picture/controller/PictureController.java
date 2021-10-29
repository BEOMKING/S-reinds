package com.project.autonomous.picture.controller;

import com.project.autonomous.picture.dto.PictureInfoDto;
import com.project.autonomous.picture.service.DBFileStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Picture", description = "사진 업로드")
@RestController
@RequestMapping("/picture")
@RequiredArgsConstructor
public class PictureController {

    private final DBFileStorageService dbFileStorageService;

    @PostMapping(path = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "단일 사진 업로드", description = "<strong>받은 multipart file</strong>를 저장한다.")
    public ResponseEntity<PictureInfoDto> uploadFile(@RequestPart("file") MultipartFile file) {
        return ResponseEntity.ok(dbFileStorageService.storeFile(file));
    }

    @PostMapping("/upload/multi")
    @Operation(summary = "다중 사진 업로드", description = "<strong>받은 multipart file들</strong>을 저장한다.")
    public ResponseEntity<List<PictureInfoDto>> uploadMultipleFiles(@RequestParam("files") MultipartFile[] files) {
        return ResponseEntity.ok(Arrays.asList(files)
            .stream()
            .map(file -> dbFileStorageService.storeFile(file))
            .collect(Collectors.toList()));
    }

//    @GetMapping("/download/{pictureId}")
//    @Operation(summary = "파일 다운로드", description = "<strong>pictureId로</strong>로 파일 정보를 다운로드 받는다.")
//    public ResponseEntity<Resource> downloadFile(@PathVariable String fileId) {
//        // Load file from database
//        DBFile dbFile = dbFileStorageService.getFile(fileId);
//
//        return ResponseEntity.ok()
//            .contentType(MediaType.parseMediaType(dbFile.getFile_type()))
//            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + dbFile.getFile_name() + "\"")
//            .body(new ByteArrayResource(dbFile.getData()));
//    }

}

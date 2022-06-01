package com.project.autonomous.picture.service;

import com.project.autonomous.common.exception.CustomException;
import com.project.autonomous.common.exception.ErrorCode;
import com.project.autonomous.picture.entity.Picture;
import com.project.autonomous.picture.repository.PictureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DBFileStorageService {

    private final PictureRepository pictureRepository;
    private final S3Uploader s3Uploader;

    @Transactional
    public Picture storeFile(MultipartFile file) throws IOException {
        // DB에 저장할 내용
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        String fileType = file.getContentType();
        String fileExtension;

        // 사진 파일만 받음
        if(fileType.contains("image/jpeg") || fileType.contains("image/jpg")){
            fileExtension = ".JPG";
        }else if(fileType.contains("image/png")){
            fileExtension = ".PNG";
        } else {
            throw new CustomException(ErrorCode.NOT_ALLOW_TYPE);
        }

        String filePath = s3Uploader.upload(file, uuid + fileExtension);
        return pictureRepository.save(Picture.of(uuid + fileExtension, fileName, fileType, filePath));

    }

//    public byte[] downloadFile(String uuid) throws IOException {
//        return s3Uploader.download(uuid);
//    }

    @Transactional
    public void deleteFile(String fileName) {
        s3Uploader.delete(fileName);
    }

}

package com.project.autonomous.picture.service;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.URLEncoder;

@Component
public class S3Uploader {

    private AmazonS3 amazonS3;

    @Value("${cloud.aws.credentials.accessKey}")
    private String accessKey;

    @Value("${cloud.aws.credentials.secretKey}")
    private String secretKey;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.region.static}")
    private String region;

    @PostConstruct
    public void setS3Uploader() {
        AWSCredentials credentials = new BasicAWSCredentials(this.accessKey, this.secretKey);

        amazonS3 = AmazonS3ClientBuilder.standard()
            .withCredentials(new AWSStaticCredentialsProvider(credentials))
            .withRegion(this.region)
            .build();
    }

    public String upload(MultipartFile file, String uuid) throws IOException {
//        String fileName = file.getOriginalFilename();
        amazonS3.putObject(new PutObjectRequest(bucket, uuid, file.getInputStream(), null)
            .withCannedAcl(CannedAccessControlList.PublicRead));
        return amazonS3.getUrl(bucket, uuid).toString();
    }

    public byte[] download(String uuid) throws IOException {
        S3Object o = amazonS3.getObject(new GetObjectRequest(bucket, uuid));
        S3ObjectInputStream objectInputStream = o.getObjectContent();
        byte[] bytes = IOUtils.toByteArray(objectInputStream);

        String fileName = URLEncoder.encode(uuid, "UTF-8").replaceAll("\\+", "%20");
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
        httpHeaders.setContentLength(bytes.length);
        httpHeaders.setContentDispositionFormData("attachment", fileName);

        return bytes;
    }

    public void delete(String fileName) {
        amazonS3.deleteObject(new DeleteObjectRequest(bucket, fileName));
    }

}

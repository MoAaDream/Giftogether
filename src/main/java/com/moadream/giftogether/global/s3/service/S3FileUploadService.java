package com.moadream.giftogether.global.s3.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.Upload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3FileUploadService {

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.s3.bucket.url}")
    private String defaultUrl;

    private final AmazonS3Client amazonS3Client;

    @Transactional
    public String uploadFile(MultipartFile uploadFile, String folder) throws AmazonS3Exception {
        if(!folder.equals("products") && !folder.equals("wishlists"))
            throw new AmazonS3Exception("이미지 링크 오류");

        String origName = uploadFile.getOriginalFilename();
        String ext = origName.substring(origName.lastIndexOf('.'));
        String saveFileName = UUID.randomUUID().toString().replaceAll("-","") + ext;
        String s3FolderPath = folder + "/" + saveFileName;

        File file = new File(System.getProperty("user.dir") + saveFileName);

        try {
            uploadFile.transferTo(file);
        } catch (IOException e) {
            throw new AmazonS3Exception("이미지 변환 오류");
        }

        TransferManager transferManager = new TransferManager(this.amazonS3Client);

        PutObjectRequest request = new PutObjectRequest(bucket, s3FolderPath, file);

        Upload upload = transferManager.upload(request);

        try {
            upload.waitForCompletion();
        } catch (InterruptedException e) {
            throw new AmazonS3Exception("이미지 업로드 오류");
        }

        String imageUrl = defaultUrl +  s3FolderPath;

        file.delete();
        log.info("SERVICE Uri = " + imageUrl);
        return imageUrl;
    }

    public void deleteFile(String imageUrl, String folder) {
        String deleteUrl = imageUrl.replaceAll("^.*?/wishlists/", "");
        amazonS3Client.deleteObject(bucket + "/" + folder, deleteUrl);
        log.info("url = " + deleteUrl);
        log.info("end");
    }

}

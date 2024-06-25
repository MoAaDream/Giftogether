package com.moadream.giftogether.global.s3.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.moadream.giftogether.global.s3.service.S3FileUploadService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/files")
public class S3Controller {

    private final S3FileUploadService s3FileUploadService;

    @PostMapping("/image/{folderType}")
    @ResponseBody
    public ResponseEntity<String> uploadFile(@PathVariable("folderType") String folder, @RequestParam("file") MultipartFile file, Model model) {
        try {
            String fileUri = s3FileUploadService.uploadFile(file, folder);
            model.addAttribute("imageUri", fileUri);

            return new ResponseEntity<>(fileUri, HttpStatus.OK);
        }
        catch (Exception e) {
            model.addAttribute("error", "파일 업로드 실패");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/upload")
    public String getMapping(){
        return "upload";
    }
}

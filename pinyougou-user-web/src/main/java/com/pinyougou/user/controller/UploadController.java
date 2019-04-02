package com.pinyougou.user.controller;

import entity.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import utils.FastDFSClient;

@RestController
public class UploadController {

    @Value("${FILE_SERVER_URL}")
    private String FILE_SERVER_URL;

    @RequestMapping("/upload")
    public Result upload(MultipartFile upload){

        try {
            FastDFSClient client = new FastDFSClient("classpath:config/fdfs_client.conf");

            String filename = upload.getOriginalFilename();
            String extraName = filename.substring(filename.lastIndexOf(".")+1);
            String url = FILE_SERVER_URL+client.uploadFile(upload.getBytes(), extraName);
            return new Result(true,url);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"上传失败!");
        }

    }
}

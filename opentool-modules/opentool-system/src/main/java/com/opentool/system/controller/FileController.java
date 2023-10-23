package com.opentool.system.controller;

import com.opentool.common.core.constant.HttpStatus;
import com.opentool.common.core.domain.R;
import com.opentool.common.core.utils.file.FileUtils;
import com.opentool.system.utils.file.FileUploadUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

/**
 * 本地文件管理接口
 * / @Author: ZenSheep
 * / @Date: 2023/8/7 20:22
 */
@RefreshScope
@RestController
@RequestMapping("/file")
public class FileController{
    @Value("${file-server.root-path}")
    private String rootPath;

    /**
     * 文件上传，入参可以根据具体业务进行添加
     * @param file 文件
     * @return 响应结果
     */
    @PostMapping("/upload")
    public R<?> uploadFile(@RequestParam("uid") String uid, @RequestParam("file") MultipartFile file) {
        // 获取文件的完整名称， 文件名+后缀名
        System.out.println("getOriginalFilename():" + file.getOriginalFilename());
        // 获取文件名(不含后缀)
        System.out.println("getFileName:" + FileUtils.getBaseName(file.getOriginalFilename()));
        // 文件传参的参数名称
        System.out.println("getName():" + file.getName());
        // 文件大小，单位：字节
        System.out.println("getSize():" + file.getSize());
        // 获取文件类型， 并非文件后缀名
        System.out.println("getContentType():" + file.getContentType());
        // 构建上传文件保存路径
        File saveFile = new File("F:\\JavaProject\\file-save-path\\images", file.getOriginalFilename());
        // 保存文件，存储文件到本地
        try {
            file.transferTo(saveFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // 一、本地存储
        String uploadPath = rootPath + uid + "\\upload";
        String savePath = "";

        try {
            savePath = FileUploadUtils.upload(uploadPath, file);
        } catch (IOException e) {
            return R.fail(HttpStatus.BAD_REQUEST,"文件传输失败");
        }
        System.out.println("savePath:" + uploadPath + savePath);
        return R.ok(uploadPath + savePath, "上传成功");

    }
}

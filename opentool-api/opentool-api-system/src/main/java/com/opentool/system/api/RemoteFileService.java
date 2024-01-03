package com.opentool.system.api;

import com.opentool.system.api.config.DefaultFeignConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

/**
 * File Feign: 提供File的远程服务
 * / @Author: ZenSheep
 * / @Date: 2023/8/14 18:48
 */
@FeignClient(name = "opentool-system", contextId="remote-file", configuration = DefaultFeignConfiguration.class)
public interface RemoteFileService {
    @PostMapping(value = "/oss/file/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    String uploadFile(@RequestPart("file") MultipartFile file, @RequestParam("storagePath") String storagePath);
}
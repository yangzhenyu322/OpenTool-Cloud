package com.opentool.system.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/** ImageConvert Feign: 提供ImageConvert的远程服务
 * / @Author: ZenSheep
 * / @Date: 2023/12/11 16:10
 */
@FeignClient(name = "opentool-general-tool", contextId = "remote-image-convert")
public interface RemoteImageConvertService {
    @PostMapping("/imageConvert/conversion/size")
    List<String> convertSize(@RequestParam("urlsStrList") List<String> urlsStrList, @RequestParam("width") int width, @RequestParam("height") int height);
}
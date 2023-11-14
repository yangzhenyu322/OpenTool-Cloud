package com.opentool.ai.tool.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.opentool.ai.tool.auth.TtsAuthentication;
import com.opentool.ai.tool.constant.TtsConstant;
import com.opentool.ai.tool.domain.entity.TtsRole;
import com.opentool.ai.tool.domain.entity.TtsStyle;
import com.opentool.ai.tool.domain.entity.TtsStyleRole;
import com.opentool.ai.tool.domain.vo.TtsRequest;
import com.opentool.ai.tool.http.HttpsConnection;
import com.opentool.ai.tool.mapper.TtsRoleMapper;
import com.opentool.ai.tool.mapper.TtsStyleMapper;
import com.opentool.ai.tool.mapper.TtsStyleRoleMapper;
import com.opentool.ai.tool.service.ITtsService;
import com.opentool.ai.tool.ssml.XmlDom;
import com.opentool.ai.tool.utils.ByteArray;
import com.opentool.common.core.utils.file.MultipartFileUtils;
import com.opentool.system.api.RemoteFileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * TTS服务-微软Azure
 * 文档：https://learn.microsoft.com/zh-cn/azure/ai-services/speech-service/get-started-text-to-speech?tabs=windows%2Cterminal&pivots=programming-language-java
 * / @Author: ZenSheep
 * / @Date: 2023/10/30 20:35
 */
@Slf4j
@Service
public class TtsService implements ITtsService {
    @Autowired
    private TtsRoleMapper ttsRoleMapper;
    @Autowired
    private TtsStyleMapper ttsStyleMapper;
    @Autowired
    private TtsStyleRoleMapper ttsStyleRoleMapper;
    @Autowired
    private RemoteFileService remoteFileService;

    @Override
    public List<String> getLanguages() {
        QueryWrapper<TtsRole> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("DISTINCT language");
        return ttsRoleMapper.selectObjs(queryWrapper).stream().
                map(obj -> (String)obj).
                collect(Collectors.toList());
    }

    @Override
    public List<String> getRolesByLanguageAndGender(String language, String gender) {
        Map<String, Object> roleMap = new HashMap<>();
        roleMap.put("language", language);
        roleMap.put("gender", gender);
        return ttsRoleMapper.selectByMap(roleMap).stream().
                map(role -> (String)role.getRole()).
                collect(Collectors.toList());
    }

    @Override
    public List<TtsStyle> getStyles() {
        return ttsStyleMapper.selectList(null);
    }

    @Override
    public List<TtsStyleRole> getStyleRoles() {
        return ttsStyleRoleMapper.selectList(null);
    }

    @Override
    public String synthesizeVoice(TtsRequest ttsRequest) throws Exception {
        String textToSynthesize = ttsRequest.getText(); // 转音文本
        String voiceName = ttsRequest.getVoiceRole(); // 语音模型

        Map<String, Object> roleMap = new HashMap<>();
        roleMap.put("role", voiceName);
        TtsRole ttsRole = ttsRoleMapper.selectByMap(roleMap).get(0);

        String deviceLanguage = ttsRole.getLocate(); // 区域设置
        String genderName = ttsRole.getGender(); // 人物性别
        String outputFormat = TtsConstant.AUDIO_FORMAT; // 语音输出格式

        byte[] audioBuffer = new byte[0];
        if (textToSynthesize.length() < 1500) {
            ByteArray byteArray = new ByteArray(audioBuffer);
            byteArray.cat(synthesize(textToSynthesize, outputFormat, deviceLanguage, genderName, voiceName, ttsRequest.getStyle(), ttsRequest.getStyleDegree(), ttsRequest.getStyleRole(), ttsRequest.getRate(), ttsRequest.getPitch()));
            audioBuffer = byteArray.getArray();
        } else {
            // 大文件：断点续传
            int blocks = textToSynthesize.length() / 1500;
            for (int i = 0; i < blocks; i++) {
                ByteArray byteArray = new ByteArray(audioBuffer);
                byteArray.cat(synthesize(textToSynthesize.substring(i * 1500, i * 1500 + 1500), outputFormat, deviceLanguage, genderName, voiceName, ttsRequest.getStyle(), ttsRequest.getStyleDegree(), ttsRequest.getStyleRole(), ttsRequest.getRate(), ttsRequest.getPitch()));
                audioBuffer = byteArray.getArray();
            }
            // 拼接末尾不足1500字的部分
            if (textToSynthesize.length() % 1500 != 0) {
                ByteArray byteArray = new ByteArray(audioBuffer);
                byteArray.cat(synthesize(textToSynthesize.substring(blocks * 1500), outputFormat, deviceLanguage, genderName, voiceName, ttsRequest.getStyle(), ttsRequest.getStyleDegree(), ttsRequest.getStyleRole(), ttsRequest.getRate(), ttsRequest.getPitch()));
                audioBuffer = byteArray.getArray();
            }
        }

        // write the pcm data to the file
        File outputAudio = new File(TtsConstant.FILE_TEMP_PATH);
        // 创建路径中的文件夹
        outputAudio.getParentFile().mkdirs();
        try {
            if (outputAudio.createNewFile()) {
                log.info(TtsConstant.FILE_TEMP_PATH+ "文件创建成功");
            } else {
                log.info(TtsConstant.FILE_TEMP_PATH + "文件已存在");
            }
        } catch (IOException e) {
            log.info("文件创建失败：" + e.getMessage());
        }

        FileOutputStream fstream = new FileOutputStream(outputAudio);
        fstream.write(audioBuffer);
        fstream.flush();
        fstream.close();

        // 将合成的音频上传的OSS
        MultipartFile multipartFile = MultipartFileUtils.fileToMultipartFile(outputAudio);
        String fileUploadPath = remoteFileService.uploadFile(multipartFile, TtsConstant.OSS_UPLOAD_PATH);

        // 删除缓存本地文件
//        if (outputAudio.delete()) {
//            log.info("本地缓存文件删除成功");
//        } else {
//            log.info("本地缓存文件删除失败");
//        }

        return fileUploadPath;
    }

    /**
     * 通过指定的参数合成语音
     */
    public byte[] synthesize(String textToSynthesize, String outputFormat, String locale, String genderName, String voiceName, String style, double styleDegree, String styleRole, double rate, double pitch) throws Exception {
        TtsAuthentication auth = new TtsAuthentication();
        String accessToken = auth.genAccessToken();

        HttpsURLConnection webRequest = HttpsConnection.getHttpsConnection(TtsConstant.TTS_SERVER_URI);
        webRequest.setDoInput(true);
        webRequest.setDoOutput(true);
        webRequest.setConnectTimeout(5000);
        webRequest.setReadTimeout(15000);
        webRequest.setRequestMethod("POST");

        webRequest.setRequestProperty("Content-Type", "application/ssml+xml");
        webRequest.setRequestProperty("X-Microsoft-OutputFormat", outputFormat);
        webRequest.setRequestProperty("Authorization", "Bearer " + accessToken);
        webRequest.setRequestProperty("X-Search-AppId", "07D3234E49CE426DAA29772419F436CA");
        webRequest.setRequestProperty("X-Search-ClientID", "1ECFAE91408841A480F00935DC390960");
        webRequest.setRequestProperty("User-Agent", "TTSAndroid");
        webRequest.setRequestProperty("Accept", "*/*");

        String body = XmlDom.createDom(locale, genderName, voiceName, textToSynthesize, style, styleDegree, styleRole, rate, pitch);
        byte[] bytes = body.getBytes();
        webRequest.setRequestProperty("content-length", String.valueOf(bytes.length));
        webRequest.connect();

        DataOutputStream dop = new DataOutputStream(webRequest.getOutputStream());
        dop.write(bytes);
        dop.flush();
        dop.close();

        InputStream inSt = webRequest.getInputStream();
        ByteArray ba = new ByteArray();

        int rn2 = 0;
        int bufferLength = 4096;
        byte[] buf2 = new byte[bufferLength];
        while ((rn2 = inSt.read(buf2, 0, bufferLength)) > 0) {
            ba.cat(buf2, 0, rn2);
        }

        inSt.close();
        webRequest.disconnect();

        return ba.getArray();
    }
}
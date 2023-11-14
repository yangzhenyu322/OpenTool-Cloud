package com.opentool.ai.tool.ssml;

/**
 * / @Author: ZenSheep
 * / @Date: 2023/10/29 22:38
 */

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;


/**
 * SSML类：用于调整合成语言风格
 * 配置参考：https://learn.microsoft.com/zh-cn/azure/ai-services/speech-service/speech-synthesis-markup-voice
 */
public class XmlDom {
    public static String createDom(String locale, String genderName, String voiceName, String textToSynthesize, String style, double styleDegree, String styleRole, double rate, double pitch){
        Document doc = null;
        Element speak, voice, express, prosody;
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = dbf.newDocumentBuilder();
            doc = builder.newDocument();
            if (doc != null){
                speak = doc.createElement("speak");
                speak.setAttribute("version", "1.0");
                speak.setAttribute("xmlns", "http://www.w3.org/2001/10/synthesis");
                speak.setAttribute("xmlns:mstts", "http://www.w3.org/2001/mstts");
                speak.setAttribute("xml:lang", "en-us");

                voice = doc.createElement("voice");
                voice.setAttribute("xml:lang", locale); // 语言
                voice.setAttribute("xml:gender", genderName); // 任务性别
                voice.setAttribute("name", voiceName); // 语音角色

                // 讲话风格和角色
                express = doc.createElement("mstts:express-as");
                express.setAttribute("style", (style.equals("default")) ? null : style); // 讲话风格：hopeful、sad、angry等，如果风格值为null，则会忽略整个 mstts:express-as 元素，服务将使用默认的中性语音。
                String degree = String.valueOf(styleDegree);
                express.setAttribute("styledegree", degree); //风格强度：指定更强或更柔和的风格，可接受范围为0.01~2，默认值为1
                express.setAttribute("role", styleRole); // 模仿角色：Girl、Boy等

                // 调整韵律：指定文本转语音输出的音高、语调曲线、范围、速率和音量的变化
                prosody = doc.createElement("prosody");
                String voiceRate = String.valueOf((rate - 1.0) * 100) + "%";
                String voicePitch = String.valueOf((pitch - 1.0) * 100) + "%";
                prosody.setAttribute("rate", voiceRate); // 速率：可选0.5~2.0倍，默认值为+0.00%，即范围为“-50.00%~+100.00%”
                prosody.setAttribute("pitch", voicePitch); // 音调：可选0.5~1.5被，默认值为+0.00%，即范围为“-50.00%~+50.00%”

                prosody.appendChild(doc.createTextNode(textToSynthesize));
                express.appendChild(prosody);
                voice.appendChild(express);
                speak.appendChild(voice);
                doc.appendChild(speak);
            }
        } catch (ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return transformDom(doc);
    }

    private static String transformDom(Document doc){
        StringWriter writer = new StringWriter();
        try {
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer;
            transformer = tf.newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            transformer.transform(new DOMSource(doc), new StreamResult(writer));
        } catch (TransformerException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return writer.getBuffer().toString().replaceAll("\n|\r", "");
    }
}

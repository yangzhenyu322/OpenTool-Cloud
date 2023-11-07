package com.opentool.ai.tool.TTS;

/**
 * / @Author: ZenSheep
 * / @Date: 2023/10/29 22:37
 */

import com.opentool.ai.tool.constant.TtsConstant;

import javax.sound.sampled.*;
import java.io.File;
import java.io.FileOutputStream;

public class TTSSample {
    public static void main(String[] args) {
//        String textToSynthesize = "This is a demo to call microsoft text to speech service in java.";
//        String textToSynthesize = "为了充分挖掘数据中蕴含的知识，研究人员需要对数据进行有效的关联性分析。然而，真实数据的获取和处理存在诸多限制，而且数据间的关联性挖掘通常依赖于专家知识的人工评审。" +
//                "现有的关联性分析方法存在难以处理高维数据、可解释性不足等问题。本文针对仿真数据变量之间非线性的复杂关联关系和仿真过程的不确定性挑战，提出了一种基于贝叶斯网络的结构自推理与关联性分析方法—AOBNet（Adaptive Organization Bayesian Network）。" +
//                "该方法通过结构自推理模块（MWST-MMPC）从仿真数据挖掘仿真要素与效能的有向无环图（DAG）结构，构建关联性分析模型。并利用贝叶斯网络对仿真数据进行学习分析因素间的关联强度。最后通过关联推理方法寻找数据间的关联规则。" +
//                "实验结果表明，AOBNet能够有效可靠地分析数据因素间的关联性，并提供清晰可解释的推理结果。这一研究为关联性分析领域的进一步探索提供了新的方法和视角。近年来，数据科学发展迅速，数据分析成为了当代各个领域中的关键驱动力和决策支持工具[1]。它在解决实际问题、提取有价值信息、进行预测和优化方面发挥着重要作用。毫无疑问，数据分析已成为各种组织中至关重要的一个方面，从研究机构到企业。" +
//                "为了得出更重要的结论和决策，一些组织开始发展的关联性分析[2]。关联性分析是一种数据挖掘技术，它能够用来进行数据处理、统计分析来描述两个或多个变量之间的相互联系。同时还可基于前一事件的发生而预测将要发生的后一事件。目前，很多组织和学科应用关联性分析，例如金融、市场营销、生物学、风险预测等[3]。\n" +
//                "数据分析发展至今，已经出现了许多的关联性分析方法。但是受各种因素影响，真实世界数据可能包含不完整，不准确，噪音和离散的数据异常，使用真实世界的数据进行关联性分析是非常困难的[4]。同时，真实数据也很难获得和管理，可能包含了各种隐私安全和保护问题。基于此，通过仿真实验获取模拟数据，成为了关联性分析方法研究的重要数据来源。\n" +
//                "仿真数据是在真实数据基础上利用科学方法采取加工处理方法，以更好地反映真实数据的某些特征，以提供数据分析的有用信息。仿真数据有很多好处，例如便于访问和控制，可针对不同的场景分别进行有效的实验和测试[5]。" +
//                "此外，仿真数据还可以更精确地反映某些情况下的潜在情况和短期趋势。这些特性使仿真数据可以成为研究顶级公司的大规模数据和生物组学的实验数据等场景中实现精确度和效率的重要工具。" +
//                "其中应用于仿真数据的关联性分析已然成为了现代研究的一个重要领域。\n";
        String textToSynthesize = "为了充分挖掘数据中蕴含的知识，研究人员需要对数据进行有效的关联性分析。然而，真实数据的获取和处理存在诸多限制，而且数据间的关联性挖掘通常依赖于专家知识的人工评审。现有的关联性分析方法存在难以处理高维数据、可解释性不足等问题。本文针对仿真数据变量之间非线性的复杂关联关系和仿真过程的不确定性挑战，提出了一种基于贝叶斯网络的结构自推理与关联性分析方法—AOBNet（Adaptive Organization Bayesian Network）。该方法通过结构自推理模块（MWST-MMPC）从仿真数据挖掘仿真要素与效能的有向无环图（DAG）结构，构建关联性分析模型。并利用贝叶斯网络对仿真数据进行学习分析因素间的关联强度。最后通过关联推理方法寻找数据间的关联规则。实验结果表明，AOBNet能够有效可靠地分析数据因素间的关联性，并提供清晰可解释的推理结果。这一研究为关联性分析领域的进一步探索提供了新的方法和视角。";
        String deviceLanguage = TtsConstant.LOCALE; // 区域设置
        String genderName = TtsConstant.GENDER; // 人物性别
        String voiceName = TtsConstant.SHORTNAME; // 语音模型
        String outputFormat = TtsConstant.AUDIO_FORMAT; // 语音输出格式
        String outputWave = TtsConstant.FILE_TEMP_PATH; // 控制文件保存的格式

        try{
            byte[] audioBuffer = TTSService.Synthesize(textToSynthesize, outputFormat, deviceLanguage, genderName, voiceName);

            // write the pcm data to the file
            File outputAudio = new File(outputWave);
            FileOutputStream fstream = new FileOutputStream(outputAudio);
            fstream.write(audioBuffer);
            fstream.flush();
            fstream.close();

            // specify the audio format
            AudioFormat audioFormat = new AudioFormat(
                    AudioFormat.Encoding.PCM_SIGNED,
                    24000,
                    16,
                    1,
                    1 * 2,
                    24000,
                    false);

            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(outputWave));  // 支持文件格式：Riff24Khz16BitMonoPcm

            DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class,
                    audioFormat, AudioSystem.NOT_SPECIFIED);
            SourceDataLine sourceDataLine = (SourceDataLine) AudioSystem
                    .getLine(dataLineInfo);
            sourceDataLine.open(audioFormat);
            sourceDataLine.start();
            System.out.println("start to play the wave:");
            /*
             * read the audio data and send to mixer
             */
            int count;
            byte tempBuffer[] = new byte[4096];
            while ((count = audioInputStream.read(tempBuffer, 0, tempBuffer.length)) >0) {
                sourceDataLine.write(tempBuffer, 0, count);
            }

            sourceDataLine.drain();
            sourceDataLine.close();
            audioInputStream.close();
            System.exit(0);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

}

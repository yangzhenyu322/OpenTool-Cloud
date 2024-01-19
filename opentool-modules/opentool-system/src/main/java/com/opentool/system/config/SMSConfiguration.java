package com.opentool.system.config;

import com.aliyun.auth.credentials.Credential;
import com.aliyun.auth.credentials.provider.StaticCredentialProvider;
import com.aliyun.sdk.service.dysmsapi20170525.AsyncClient;
import com.aliyun.sdk.service.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.sdk.service.dysmsapi20170525.models.SendSmsResponse;
import darabonba.core.client.ClientOverrideConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * SMS-Client配置类（Short Message Service，短讯服务）
 * @Author: ZenSheep
 * @Date: 2024/1/17 15:11
 */
@Configuration
public class SMSConfiguration {
    private static String accessKeyId;

    private static String accessKeySecret;

    // 短信签名名称
    private static String signName;

    // 短信模板变量对应的实际值
    private static String templateCode;

    // Region ID
    private static String region;

    // 产品域名
    public static String endpoint;

    // Configure Credentials authentication info, including ak, secret, token
    private volatile static StaticCredentialProvider provider;

    // Configure the Client
    private volatile static AsyncClient smsClient;

    @Value("${aliyun.accessKeyId}")
    public void setAccessKeyId(String accessKeyId) {
        SMSConfiguration.accessKeyId = accessKeyId;
    }

    @Value("${aliyun.accessKeySecret}")
    public void setAccessKeySecret(String accessKeySecret) {
        SMSConfiguration.accessKeySecret = accessKeySecret;
    }

    @Value("${aliyun.sms.signName}")
    public void setSignName(String signName) {
        SMSConfiguration.signName = signName;
    }

    public static String getSignName() {
        return signName;
    }

    @Value("${aliyun.sms.templateCode}")
    public void setTemplateCode(String templateCode) {
        SMSConfiguration.templateCode = templateCode;
    }

    public static String getTemplateCode() {
        return templateCode;
    }

    @Value("${aliyun.sms.region}")
    public void setRegion(String region) {
        SMSConfiguration.region = region;
    }

    @Value("${aliyun.sms.endpoint}")
    public void setEndpoint(String endpoint) {
        SMSConfiguration.endpoint = endpoint;
    }

    public static StaticCredentialProvider provider() {
        if (provider == null) {
            synchronized (SMSConfiguration.class) {
                if (provider == null) {
                    provider = StaticCredentialProvider.create(Credential.builder()
                            .accessKeyId(accessKeyId)
                            .accessKeySecret(accessKeySecret)
                            .build());
                }
            }
        }

        return provider;
    }

    @Bean
    public static AsyncClient smsClient() {
        if (smsClient == null) {
            synchronized (SMSConfiguration.class) {
                if (smsClient == null) {
                    smsClient = AsyncClient.builder()
                            .region(region)
                            .credentialsProvider(provider())
                            .overrideConfiguration(
                                    ClientOverrideConfiguration.create()
                                            .setEndpointOverride(endpoint)
                            ).build();
                }
            }
        }

        return smsClient;
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        // HttpClient Configuration
        /*HttpClient httpClient = new ApacheAsyncHttpClientBuilder()
                .connectionTimeout(Duration.ofSeconds(10)) // Set the connection timeout time, the default is 10 seconds
                .responseTimeout(Duration.ofSeconds(10)) // Set the response timeout time, the default is 20 seconds
                .maxConnections(128) // Set the connection pool size
                .maxIdleTimeOut(Duration.ofSeconds(50)) // Set the connection pool timeout, the default is 30 seconds
                // Configure the proxy
                .proxy(new ProxyOptions(ProxyOptions.Type.HTTP, new InetSocketAddress("<your-proxy-hostname>", 9001))
                        .setCredentials("<your-proxy-username>", "<your-proxy-password>"))
                // If it is an https connection, you need to configure the certificate, or ignore the certificate(.ignoreSSL(true))
                .x509TrustManagers(new X509TrustManager[]{})
                .keyManagers(new KeyManager[]{})
                .ignoreSSL(false)
                .build();*/

        // Configure Credentials authentication information, including ak, secret, token
        StaticCredentialProvider provider = StaticCredentialProvider.create(Credential.builder()
                // Please ensure that the environment variables ALIBABA_CLOUD_ACCESS_KEY_ID and ALIBABA_CLOUD_ACCESS_KEY_SECRET are set.
                .accessKeyId("LTAI5tBLXP1DsNu2hchBoHCh")
                .accessKeySecret("3Z81Vghe5xCARlnCwvcM3k8zH3bxZ0")
                //.securityToken(System.getenv("ALIBABA_CLOUD_SECURITY_TOKEN")) // use STS token
                .build());

        // Configure the Client
        AsyncClient client = AsyncClient.builder()
                .region("cn-hangzhou")
                //.httpClient(httpClient) // Use the configured HttpClient, otherwise use the default HttpClient (Apache HttpClient)
                .credentialsProvider(provider)
                //.serviceConfiguration(Configuration.create()) // Service-level configuration
                // Client-level configuration rewrite, can set Endpoint, Http request parameters, etc.
                .overrideConfiguration(
                        ClientOverrideConfiguration.create()
                                // Endpoint 请参考 https://api.aliyun.com/product/Dysmsapi
                                .setEndpointOverride(endpoint)
                        //.setConnectTimeout(Duration.ofSeconds(30))
                )
                .build();

        // Parameter settings for API request
        SendSmsRequest sendSmsRequest = SendSmsRequest.builder()
                .phoneNumbers("19898804733")
                .signName("OpenTool")
                .templateCode("SMS_464785669")
                .templateParam("{\"code\":\"520520\"}")
                // Request-level configuration rewrite, can set Http request parameters, etc.
                // .requestConfiguration(RequestConfiguration.create().setHttpHeaders(new HttpHeaders()))
                .build();

        // Asynchronously get the return value of the API request
        CompletableFuture<SendSmsResponse> response = client.sendSms(sendSmsRequest);
        // Synchronously get the return value of the API request
        SendSmsResponse resp = response.get();
        System.out.println("RequestId:" + resp.getBody().getRequestId());
        System.out.println("Code:" + resp.getBody().getCode());
        System.out.println("Message:" + resp.getBody().getMessage());
        System.out.println("BizId:" + resp.getBody().getBizId());

//        System.out.println(new Gson().toJson(resp));

        // Asynchronous processing of return values
        /*response.thenAccept(resp -> {
            System.out.println(new Gson().toJson(resp));
        }).exceptionally(throwable -> { // Handling exceptions
            System.out.println(throwable.getMessage());
            return null;
        });*/

        // Finally, close the client
        client.close();
    }

}

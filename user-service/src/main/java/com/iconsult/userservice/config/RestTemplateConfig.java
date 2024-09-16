//package com.iconsult.userservice.config;
//
//
//import org.apache.hc.client5.http.classic.HttpClient;
//import org.apache.http.conn.ssl.NoopHostnameVerifier;
//import org.apache.http.impl.client.CloseableHttpClient;
//import org.apache.http.impl.client.HttpClients;
//import org.apache.http.ssl.SSLContextBuilder;
//import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
//import org.springframework.web.client.RestTemplate;
//
//import javax.net.ssl.SSLContext;
//
//public class RestTemplateConfig {
//    public static RestTemplate createRestTemplate() throws Exception {
//        // Create SSL context that trusts all certificates
//        SSLContext sslContext = SSLContextBuilder.create()
//                .loadTrustMaterial((chain, authType) -> true) // Trust all certificates
//                .build();
//
//        // Create HTTP client with custom SSL context and hostname verifier
//        CloseableHttpClient httpClient = HttpClients.custom()
//                .setSSLContext(sslContext)
//                .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE) // Bypass hostname verification
//                .build();
//
//        // Create and return RestTemplate with custom HTTP client
//        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory((HttpClient) httpClient);
//        return new RestTemplate(factory);
//    }
//}

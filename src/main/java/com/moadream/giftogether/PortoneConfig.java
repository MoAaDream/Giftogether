package com.moadream.giftogether;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.siot.IamportRestClient.IamportClient;

@Configuration
public class PortoneConfig {
    
    @Value("${PORTONE_KEY}")
    private String apiKey;
    
    @Value("${PORTONE_SECRET}")
    private String apiSecret;


    
    @Bean
    public IamportClient iamportClient() {
        return new IamportClient(apiKey, apiSecret);
    }
}
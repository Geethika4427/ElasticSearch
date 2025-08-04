package com.example.config;

import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;

@Configuration
public class ElasticConfig {

    @Bean
    public RestHighLevelClient restHighLevelClient() {
        ClientConfiguration config = ClientConfiguration.builder()
                .connectedTo("localhost:9200") // update if needed
                .build();

        return RestClients.create(config).rest();
    }
}

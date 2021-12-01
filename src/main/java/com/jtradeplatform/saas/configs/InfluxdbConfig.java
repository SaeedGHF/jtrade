package com.jtradeplatform.saas.configs;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.InfluxDBClientOptions;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
@Setter
@ConfigurationProperties(prefix = "influxdb")
public class InfluxdbConfig {
    String url;
    String username;
    String password;
    String bucket;
    String org;
    String token;
    InfluxDBClient influxDBClient;

    InfluxdbConfig() {

    }

    public InfluxDBClient getClient() {
        if (influxDBClient == null) {
            InfluxDBClientOptions options = InfluxDBClientOptions
                    .builder()
                    .url(url)
                    .authenticateToken(token.toCharArray())
                    .org(org)
                    .bucket(bucket)
                    .build();

            influxDBClient = InfluxDBClientFactory.create(options);
        }
        return influxDBClient;
    }
}

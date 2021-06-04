package com.jtradeplatform.saas.configs;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public String getOrg() {
        return org;
    }

    public void setOrg(String org) {
        this.org = org;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public InfluxDBClient getClient() {
        if (influxDBClient == null) {
            influxDBClient = InfluxDBClientFactory.create(url, token.toCharArray(), org, bucket);
        }
        return influxDBClient;
    }
}

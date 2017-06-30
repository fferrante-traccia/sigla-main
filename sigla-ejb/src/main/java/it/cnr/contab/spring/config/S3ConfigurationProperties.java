package it.cnr.contab.spring.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Created by mspasiano on 6/5/17.
 */
@Configuration
public class S3ConfigurationProperties {

    @Value("${cnr.aws.authUrl}")
    private String authUrl;
    @Value("${cnr.aws.accessKey}")
    private String accessKey;
    @Value("${cnr.aws.secretKey}")
    private String secretKey;
    @Value("${cnr.aws.bucketName}")
    private String bucketName;
    @Value("${cnr.aws.deleteAfter}")
    private Integer deleteAfter;

    public String getAuthUrl() {
        return authUrl;
    }

    public void setAuthUrl(String authUrl) {
        this.authUrl = authUrl;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public Integer getDeleteAfter() {
        return deleteAfter;
    }

    public void setDeleteAfter(Integer deleteAfter) {
        this.deleteAfter = deleteAfter;
    }
}

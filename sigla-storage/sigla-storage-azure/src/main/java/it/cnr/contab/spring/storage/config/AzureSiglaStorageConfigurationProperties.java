package it.cnr.contab.spring.storage.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Created by mspasiano on 6/5/17.
 */
@Configuration
@Profile("Azure")
public class AzureSiglaStorageConfigurationProperties {

    @Value("${cnr.azure.connectionString}")
    private String connectionString;

    @Value("${cnr.azure.containerName}")
    private String containerName;

    public String getContainerName() {
        return containerName;
    }

    public void setContainerName(String containerName) {
        this.containerName = containerName;
    }

    public String getConnectionString() {
        return connectionString;
    }

    public void setConnectionString(String connectionString) {
        this.connectionString = connectionString;
    }




}

package dk.runerne.indexingserver.integration.contentapp;

import dk.runerne.indexing.contentclient.api.MediaManagementApi;
import dk.runerne.indexing.contentclient.invoker.ApiClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ContentApiConfiguration {

    @Value("${content-app.api.base-url}")
    private String contentApiBaseUrl;

    @Bean
    public MediaManagementApi mediaManagementApi() {
        return new MediaManagementApi(
            new ApiClient()
                .setBasePath(contentApiBaseUrl)
        );
    }

}
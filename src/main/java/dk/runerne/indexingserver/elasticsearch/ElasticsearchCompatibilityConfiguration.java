package dk.runerne.indexingserver.elasticsearch;

import java.net.URI;

import org.apache.hc.core5.http.EntityDetails;
import org.apache.hc.core5.http.HttpRequest;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;
import org.springframework.data.elasticsearch.client.elc.rest5_client.Rest5Clients;

@Configuration
public class ElasticsearchCompatibilityConfiguration extends ElasticsearchConfiguration {

    @Value("${spring.elasticsearch.uris:http://localhost:9200}")
    private String elasticsearchUri;

    @Override
    public ClientConfiguration clientConfiguration() {
        URI uri = URI.create(elasticsearchUri);
        String hostAndPort = uri.getHost() + ":" + uri.getPort();

        return ClientConfiguration.builder()
            .connectedTo(hostAndPort)
            .withClientConfigurer(
                Rest5Clients.ElasticsearchHttpClientConfigurationCallback.from(
                    httpAsyncClientBuilder -> httpAsyncClientBuilder.addRequestInterceptorLast(
                        (HttpRequest request, EntityDetails entity, HttpContext context) -> {
                            request.removeHeaders("Content-Type");
                            request.removeHeaders("Accept");
                            request.addHeader("Content-Type", "application/json");
                            request.addHeader("Accept", "application/json");
                        }
                    )
                )
            )
            .build();
    }

}

package com.leo.productservice.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import lombok.Data;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
public class ElasticsearchConfig {

    @Bean
    @ConfigurationProperties(prefix = "elasticsearch")
    public EsProps esProps() {
        return new EsProps(); // 会自动绑定属性
    }

    @Bean(destroyMethod = "close")
    public RestClient restClient(EsProps props) {
        List<HttpHost> hosts = Arrays.stream(props.getHosts().split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(h -> {
                    // 支持 host:port 或 http(s)://host:port
                    if (h.startsWith("http://") || h.startsWith("https://")) {
                        return HttpHost.create(h);
                    } else {
                        String[] arr = h.split(":");
                        String host = arr[0];
                        int port = arr.length > 1 ? Integer.parseInt(arr[1]) : 9200;
                        String scheme = props.isSsl() ? "https" : "http";
                        return new HttpHost(host, port, scheme);
                    }
                })
                .collect(Collectors.toList());

        RestClientBuilder builder = RestClient.builder(hosts.toArray(new HttpHost[0]));

        if (props.getUsername() != null && !props.getUsername().isEmpty()) {
            final CredentialsProvider cp = new BasicCredentialsProvider();
            cp.setCredentials(AuthScope.ANY,
                    new UsernamePasswordCredentials(props.getUsername(), props.getPassword()));
            builder.setHttpClientConfigCallback(httpClientBuilder ->
                    httpClientBuilder.setDefaultCredentialsProvider(cp));
        }

        if (props.getConnectTimeoutMillis() != null || props.getSocketTimeoutMillis() != null) {
            builder.setRequestConfigCallback(rcb -> {
                if (props.getConnectTimeoutMillis() != null) {
                    rcb.setConnectTimeout(props.getConnectTimeoutMillis());
                }
                if (props.getSocketTimeoutMillis() != null) {
                    rcb.setSocketTimeout(props.getSocketTimeoutMillis());
                }
                return rcb;
            });
        }

        return builder.build();
    }

    @Bean(destroyMethod = "close")
    public ElasticsearchTransport transport(RestClient restClient) {
        return new RestClientTransport(restClient, new JacksonJsonpMapper());
    }

    @Bean
    public ElasticsearchClient elasticsearchClient(ElasticsearchTransport transport) {
        return new ElasticsearchClient(transport);
    }

    @Data
    public static class EsProps {
        /**
         * 逗号分隔的地址列表，例如：
         * elasticsearch.hosts: http://127.0.0.1:9200,http://127.0.0.1:9201
         */
        private String hosts = "http://127.0.0.1:9200";
        private boolean ssl = false;
        private String username;
        private String password;
        private Integer connectTimeoutMillis = (int) Duration.ofSeconds(3).toMillis();
        private Integer socketTimeoutMillis = (int) Duration.ofSeconds(30).toMillis();
    }
}

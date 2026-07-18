package com.eskcti.algashop.ordering.infrastructure.client.rapidex;

import java.net.http.HttpClient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class RapiDexAPIClientConfig {

  @Bean
  public RapiDexAPIClient rapidexApiClient(
      @Value("${algashop.integrations.rapidex.url}") String rapiDexUrl) {
    HttpClient httpClient = HttpClient.newBuilder()
        .version(HttpClient.Version.HTTP_1_1)
        .build();
    RestClient restClient = RestClient.builder()
        .baseUrl(rapiDexUrl)
        .requestFactory(new JdkClientHttpRequestFactory(httpClient))
        .build();
    RestClientAdapter adapter = RestClientAdapter.create(restClient);
    HttpServiceProxyFactory proxyFactory = HttpServiceProxyFactory.builderFor(adapter).build();
    return proxyFactory.createClient(RapiDexAPIClient.class);
  }

}
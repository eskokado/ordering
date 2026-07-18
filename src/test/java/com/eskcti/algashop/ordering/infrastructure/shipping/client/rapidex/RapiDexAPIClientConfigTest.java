package com.eskcti.algashop.ordering.infrastructure.shipping.client.rapidex;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.eskcti.algashop.ordering.infrastructure.shipping.client.rapidex.RapiDexAPIClient;
import com.eskcti.algashop.ordering.infrastructure.shipping.client.rapidex.RapiDexAPIClientConfig;

class RapiDexAPIClientConfigTest {

  @Test
  void shouldCreateRapidexApiClient() {
    RapiDexAPIClientConfig config = new RapiDexAPIClientConfig();

    RapiDexAPIClient client = config.rapidexApiClient("http://localhost:8780");

    assertThat(client).isNotNull();
  }
}

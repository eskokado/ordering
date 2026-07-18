package com.eskcti.algashop.ordering.infrastructure.client.rapidex;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class RapiDexAPIClientConfigTest {

  @Test
  void shouldCreateRapidexApiClient() {
    RapiDexAPIClientConfig config = new RapiDexAPIClientConfig();

    RapiDexAPIClient client = config.rapidexApiClient("http://localhost:8780");

    assertThat(client).isNotNull();
  }
}

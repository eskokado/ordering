package com.eskcti.algashop.ordering.utils;

import com.eskcti.algashop.ordering.core.application.security.SecurityCheckApplicationService;
import java.util.UUID;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class SecurityCheckTestConfig {

  public static final UUID AUTHENTICATED_USER_ID =
      UUID.fromString("00000000-0000-0000-0000-000000000001");

  @Bean
  public SecurityCheckApplicationService securityCheckApplicationService() {
    return new SecurityCheckApplicationService() {

      @Override
      public UUID getAuthenticatedUserId() {
        return AUTHENTICATED_USER_ID;
      }

      @Override
      public boolean isAuthenticated() {
        return true;
      }

      @Override
      public boolean isMachineAuthenticated() {
        return false;
      }
    };
  }
}

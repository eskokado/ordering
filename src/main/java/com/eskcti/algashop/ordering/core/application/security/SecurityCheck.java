package com.eskcti.algashop.ordering.core.application.security;

import java.util.UUID;

public interface SecurityCheck {
    UUID getAuthenticatedUserId();

    boolean isAuthenticated();

    boolean isMachineAuthenticated();

    boolean isCustomer();

    boolean canOrderFor(UUID customerId);
}

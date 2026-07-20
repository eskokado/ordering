package com.eskcti.algashop.ordering.application.customer.notification;

import java.util.UUID;

public interface CustomerNotificationService {
  void notifyNewRegistration(UUID customerId);
}
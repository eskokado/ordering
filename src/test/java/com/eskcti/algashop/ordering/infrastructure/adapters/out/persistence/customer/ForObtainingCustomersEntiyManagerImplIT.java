package com.eskcti.algashop.ordering.infrastructure.adapters.out.persistence.customer;

import com.eskcti.algashop.ordering.infrastructure.adapters.out.persistence.AbstractPersistenceIT;

import com.eskcti.algashop.ordering.infrastructure.adapters.out.persistence.customer.ForObtainingCustomersEntiyManagerImpl;
import com.eskcti.algashop.ordering.utils.TestcontainerPostgreSQLConfig;
import org.springframework.context.annotation.Import;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.eskcti.algashop.ordering.utils.SecurityCheckTestConfig;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.eskcti.algashop.ordering.core.domain.model.customer.CustomerNotFoundException;
import com.eskcti.algashop.ordering.core.ports.in.customer.CustomerOutput;
import com.eskcti.algashop.ordering.infrastructure.config.auditing.SpringDataAuditingConfig;


@Import({
    ForObtainingCustomersEntiyManagerImpl.class,
    SpringDataAuditingConfig.class,
    TestcontainerPostgreSQLConfig.class,
    SecurityCheckTestConfig.class
})
class ForObtainingCustomersEntiyManagerImplIT extends AbstractPersistenceIT {

    private final ForObtainingCustomersEntiyManagerImpl forObtainingCustomers;
    private final CustomerPersistenceEntityRepository customerPersistenceEntityRepository;

    @Autowired
    public ForObtainingCustomersEntiyManagerImplIT(
            ForObtainingCustomersEntiyManagerImpl forObtainingCustomers,
            CustomerPersistenceEntityRepository customerPersistenceEntityRepository) {
        this.forObtainingCustomers = forObtainingCustomers;
        this.customerPersistenceEntityRepository = customerPersistenceEntityRepository;
    }

    @Test
    public void shouldFindByIdAsOutput() {
        CustomerPersistenceEntity entity = CustomerPersistenceEntityTestDataBuilder.existingCustomer().build();
        customerPersistenceEntityRepository.saveAndFlush(entity);

        CustomerOutput output = forObtainingCustomers.findById(entity.getId());

        assertThat(output)
                .extracting(
                        CustomerOutput::getId,
                        CustomerOutput::getFirstName,
                        CustomerOutput::getLastName,
                        CustomerOutput::getEmail,
                        CustomerOutput::getDocument,
                        CustomerOutput::getPhone,
                        CustomerOutput::getBirthDate,
                        CustomerOutput::getLoyaltyPoints,
                        CustomerOutput::getPromotionNotificationsAllowed,
                        CustomerOutput::getArchived)
                .containsExactly(
                        entity.getId(),
                        entity.getFirstName(),
                        entity.getLastName(),
                        entity.getEmail(),
                        entity.getDocument(),
                        entity.getPhone(),
                        entity.getBirthDate(),
                        entity.getLoyaltyPoints(),
                        entity.getPromotionNotificationsAllowed(),
                        entity.getArchived());
        assertThat(output.getAddress())
                .extracting(
                        address -> address.getStreet(),
                        address -> address.getNumber(),
                        address -> address.getComplement(),
                        address -> address.getNeighborhood(),
                        address -> address.getCity(),
                        address -> address.getState(),
                        address -> address.getZipCode())
                .containsExactly(
                        entity.getAddress().getStreet(),
                        entity.getAddress().getNumber(),
                        entity.getAddress().getComplement(),
                        entity.getAddress().getNeighborhood(),
                        entity.getAddress().getCity(),
                        entity.getAddress().getState(),
                        entity.getAddress().getZipCode());
    }

    @Test
    public void shouldThrowCustomerNotFoundExceptionWhenIdDoesNotExist() {
        assertThatThrownBy(() -> forObtainingCustomers.findById(UUID.randomUUID()))
                .isInstanceOf(CustomerNotFoundException.class);
    }

}

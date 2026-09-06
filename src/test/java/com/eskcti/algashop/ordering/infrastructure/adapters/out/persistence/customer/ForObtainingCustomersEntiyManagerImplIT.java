package com.eskcti.algashop.ordering.infrastructure.adapters.out.persistence.customer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.eskcti.algashop.ordering.core.domain.model.customer.CustomerNotFoundException;
import com.eskcti.algashop.ordering.core.ports.in.customer.CustomerOutput;
import com.eskcti.algashop.ordering.infrastructure.config.auditing.SpringDataAuditingConfig;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({
    ForObtainingCustomersEntiyManagerImpl.class,
    SpringDataAuditingConfig.class
})

@Sql(scripts = "classpath:sql/clean-database.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS, config = @SqlConfig(transactionMode = SqlConfig.TransactionMode.ISOLATED))
@Sql(scripts = "classpath:sql/clean-database.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, config = @SqlConfig(transactionMode = SqlConfig.TransactionMode.ISOLATED))
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@ActiveProfiles("it")
class ForObtainingCustomersEntiyManagerImplIT {

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

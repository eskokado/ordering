package com.eskcti.algashop.ordering.core.application.customer.query;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.SqlConfig;

import com.eskcti.algashop.ordering.core.application.customer.CustomerQueryService;
import com.eskcti.algashop.ordering.core.application.customer.query.CustomerFilter;
import com.eskcti.algashop.ordering.core.application.customer.query.CustomerOutput;
import com.eskcti.algashop.ordering.core.application.customer.query.CustomerSummaryOutput;
import com.eskcti.algashop.ordering.core.domain.model.commons.Email;
import com.eskcti.algashop.ordering.core.domain.model.commons.FullName;
import com.eskcti.algashop.ordering.core.domain.model.customer.Customer;
import com.eskcti.algashop.ordering.core.domain.model.customer.CustomerId;
import com.eskcti.algashop.ordering.core.domain.model.customer.CustomerNotFoundException;
import com.eskcti.algashop.ordering.core.domain.model.customer.CustomerTestDataBuilder;
import com.eskcti.algashop.ordering.core.domain.model.customer.Customers;

@SpringBootTest

@Sql(scripts = "classpath:sql/clean-database.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS, config = @SqlConfig(transactionMode = SqlConfig.TransactionMode.ISOLATED))
@Sql(scripts = "classpath:sql/clean-database.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, config = @SqlConfig(transactionMode = SqlConfig.TransactionMode.ISOLATED))
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@ActiveProfiles("it")
class CustomerQueryServiceIT {
        @Autowired
        private CustomerQueryService queryService;

        @Autowired
        private Customers customers;

        @Test
        public void shouldFindById() {
                Customer customer = CustomerTestDataBuilder.existingCustomer().build();
                customers.add(customer);

                CustomerOutput output = queryService.findById(customer.id().value());

                Assertions.assertThat(output)
                                .extracting(
                                                CustomerOutput::getId,
                                                CustomerOutput::getFirstName,
                                                CustomerOutput::getEmail)
                                .containsExactly(
                                                customer.id().value(),
                                                customer.fullName().firstName(),
                                                customer.email().value());
        }

        @Test
        public void shouldThrowCustomerNotFoundExceptionWhenCustomerDoesNotExist() {
                Assertions.assertThatThrownBy(() -> queryService.findById(new CustomerId().value()))
                                .isInstanceOf(CustomerNotFoundException.class);
        }

        @Test
        public void shouldFilterByPage() {
                customers.add(
                                CustomerTestDataBuilder.existingCustomer().id(new CustomerId())
                                                .fullName(new FullName("Ana", "Silva"))
                                                .email(new Email("ana@example.com"))
                                                .build());
                customers.add(CustomerTestDataBuilder.existingCustomer().id(new CustomerId())
                                .fullName(new FullName("Bruno", "Costa"))
                                .email(new Email("bruno@example.com"))
                                .build());
                customers.add(CustomerTestDataBuilder.existingCustomer().id(new CustomerId())
                                .fullName(new FullName("Carla", "Souza"))
                                .email(new Email("carla@example.com"))
                                .build());
                customers.add(CustomerTestDataBuilder.existingCustomer().id(new CustomerId())
                                .fullName(new FullName("Daniel", "Pereira"))
                                .email(new Email("daniel@example.com"))
                                .build());
                customers.add(CustomerTestDataBuilder.existingCustomer().id(new CustomerId())
                                .fullName(new FullName("Eduarda", "Santos"))
                                .email(new Email("eduarda@example.com"))
                                .build());

                CustomerFilter filter = new CustomerFilter(2, 0);
                Page<CustomerSummaryOutput> page = queryService.filter(filter);

                Assertions.assertThat(page.getTotalPages()).isEqualTo(3);
                Assertions.assertThat(page.getTotalElements()).isEqualTo(5);
                Assertions.assertThat(page.getNumberOfElements()).isEqualTo(2);
        }

        @Test
        public void shouldFilterByFirstName() {
                customers.add(CustomerTestDataBuilder.existingCustomer().id(new CustomerId())
                                .fullName(new FullName("Alice", "Smith"))
                                .email(new Email("alice_smith@example.com"))
                                .build());
                customers.add(CustomerTestDataBuilder.existingCustomer().id(new CustomerId())
                                .fullName(new FullName("Bob", "Johnson"))
                                .email(new Email("bob_johnson@example.com"))
                                .build());
                customers.add(CustomerTestDataBuilder.existingCustomer().id(new CustomerId())
                                .fullName(new FullName("Alice", "Williams"))
                                .email(new Email("alice_williams@example.com"))
                                .build());

                CustomerFilter filter = new CustomerFilter();
                filter.setFirstName("alice");

                Page<CustomerSummaryOutput> page = queryService.filter(filter);

                Assertions.assertThat(page.getTotalElements()).isEqualTo(2);
                Assertions.assertThat(page.getContent())
                                .extracting(CustomerSummaryOutput::getFirstName)
                                .containsOnly("Alice");
        }

        @Test
        public void shouldIgnoreBlankFirstNameWhenFiltering() {
                customers.add(CustomerTestDataBuilder.existingCustomer().id(new CustomerId())
                                .fullName(new FullName("Alice", "Smith"))
                                .email(new Email("alice_smith@example.com"))
                                .build());
                customers.add(CustomerTestDataBuilder.existingCustomer().id(new CustomerId())
                                .fullName(new FullName("Bob", "Johnson"))
                                .email(new Email("bob_johnson@example.com"))
                                .build());

                CustomerFilter filter = new CustomerFilter();
                filter.setFirstName("   ");

                Page<CustomerSummaryOutput> page = queryService.filter(filter);

                Assertions.assertThat(page.getTotalElements()).isEqualTo(2);
        }

        @Test
        public void shouldFilterByEmail() {
                customers.add(
                                CustomerTestDataBuilder.existingCustomer().id(new CustomerId())
                                                .email(new Email("user1@test.com"))
                                                .build());
                customers.add(
                                CustomerTestDataBuilder.existingCustomer().id(new CustomerId())
                                                .email(new Email("test2@algashop.com"))
                                                .build());
                customers.add(
                                CustomerTestDataBuilder.existingCustomer().id(new CustomerId())
                                                .email(new Email("user3@test.com"))
                                                .build());

                CustomerFilter filter = new CustomerFilter();
                filter.setEmail("test");

                Page<CustomerSummaryOutput> page = queryService.filter(filter);

                Assertions.assertThat(page.getTotalElements()).isEqualTo(3);
                Assertions.assertThat(page.getContent())
                                .extracting(CustomerSummaryOutput::getEmail)
                                .containsExactlyInAnyOrder("user1@test.com", "test2@algashop.com", "user3@test.com");
        }

        @Test
        public void shouldIgnoreBlankEmailWhenFiltering() {
                customers.add(
                                CustomerTestDataBuilder.existingCustomer().id(new CustomerId())
                                                .email(new Email("user1@test.com"))
                                                .build());
                customers.add(
                                CustomerTestDataBuilder.existingCustomer().id(new CustomerId())
                                                .email(new Email("user2@test.com"))
                                                .build());

                CustomerFilter filter = new CustomerFilter();
                filter.setEmail("   ");

                Page<CustomerSummaryOutput> page = queryService.filter(filter);

                Assertions.assertThat(page.getTotalElements()).isEqualTo(2);
        }

        @Test
        public void shouldFilterByMultipleParams() {
                customers.add(
                                CustomerTestDataBuilder.existingCustomer().id(new CustomerId())
                                                .fullName(new FullName("John", "Doe"))
                                                .email(new Email("johndoe@email.com")).build());
                customers.add(
                                CustomerTestDataBuilder.existingCustomer().id(new CustomerId())
                                                .fullName(new FullName("Jane", "Doe"))
                                                .email(new Email("janedoe@email.com")).build());
                customers.add(CustomerTestDataBuilder.existingCustomer().id(new CustomerId())
                                .fullName(new FullName("John", "Smith")).email(new Email("johnsmith@email.com"))
                                .build());

                CustomerFilter filter = new CustomerFilter();
                filter.setFirstName("john");
                filter.setEmail("doe");

                Page<CustomerSummaryOutput> page = queryService.filter(filter);

                Assertions.assertThat(page.getTotalElements()).isEqualTo(1);
                Assertions.assertThat(page.getContent().getFirst().getFirstName()).isEqualTo("John");
                Assertions.assertThat(page.getContent().getFirst().getEmail()).isEqualTo("johndoe@email.com");
        }

        @Test
        public void shouldOrderByFirstNameAsc() {
                customers.add(
                                CustomerTestDataBuilder.existingCustomer().id(new CustomerId())
                                                .fullName(new FullName("Zoe", "Doe"))
                                                .email(new Email("zohndoe@email.com"))
                                                .build());
                customers.add(CustomerTestDataBuilder.existingCustomer().id(new CustomerId())
                                .fullName(new FullName("Charlie", "Smith"))
                                .email(new Email("charlesmith@email.com"))
                                .build());
                customers.add(CustomerTestDataBuilder.existingCustomer().id(new CustomerId())
                                .fullName(new FullName("Alice", "Williams"))
                                .email(new Email("alice_williams@example.com"))
                                .build());

                CustomerFilter filter = new CustomerFilter();
                filter.setSortByProperty(CustomerFilter.SortType.FIRST_NAME);
                filter.setSortDirection(Sort.Direction.ASC);

                Page<CustomerSummaryOutput> page = queryService.filter(filter);

                Assertions.assertThat(page.getContent().getFirst().getFirstName()).isEqualTo("Alice");
        }

        @Test
        public void shouldOrderByFirstNameDesc() {
                customers.add(
                                CustomerTestDataBuilder.existingCustomer().id(new CustomerId())
                                                .fullName(new FullName("Zoe", "Doe"))
                                                .email(new Email("zohndoe@email.com"))
                                                .build());
                customers.add(CustomerTestDataBuilder.existingCustomer().id(new CustomerId())
                                .fullName(new FullName("Charlie", "Smith"))
                                .email(new Email("charlesmith@email.com"))
                                .build());
                customers.add(CustomerTestDataBuilder.existingCustomer().id(new CustomerId())
                                .fullName(new FullName("Alice", "Williams"))
                                .email(new Email("alice_williams@example.com"))
                                .build());

                CustomerFilter filter = new CustomerFilter();
                filter.setSortByProperty(CustomerFilter.SortType.FIRST_NAME);
                filter.setSortDirection(Sort.Direction.DESC);

                Page<CustomerSummaryOutput> page = queryService.filter(filter);

                Assertions.assertThat(page.getContent().getFirst().getFirstName()).isEqualTo("Zoe");
        }

        @Test
        public void givenNonMatchingFilter_shouldReturnEmptyPage() {
                customers.add(
                                CustomerTestDataBuilder.existingCustomer().id(new CustomerId())
                                                .fullName(new FullName("John", "Doe"))
                                                .build());

                CustomerFilter filter = new CustomerFilter();
                filter.setFirstName("NonExistent");

                Page<CustomerSummaryOutput> page = queryService.filter(filter);

                Assertions.assertThat(page.isEmpty()).isTrue();
                Assertions.assertThat(page.getTotalElements()).isEqualTo(0);
        }
}
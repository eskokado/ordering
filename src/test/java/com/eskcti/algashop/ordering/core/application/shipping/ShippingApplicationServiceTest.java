package com.eskcti.algashop.ordering.core.application.shipping;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.eskcti.algashop.ordering.core.domain.model.commons.Address;
import com.eskcti.algashop.ordering.core.domain.model.commons.Money;
import com.eskcti.algashop.ordering.core.domain.model.commons.ZipCode;
import com.eskcti.algashop.ordering.core.domain.model.order.shipping.OriginAddressService;
import com.eskcti.algashop.ordering.core.domain.model.order.shipping.ShippingCostService;

@ExtendWith(MockitoExtension.class)
class ShippingApplicationServiceTest {

    @Mock
    private OriginAddressService originAddressService;

    @Mock
    private ShippingCostService shippingCostService;

    @InjectMocks
    private ShippingApplicationService shippingApplicationService;

    @Test
    void shouldPreviewCostSuccessfully() {
        Address originAddress = Address.builder()
                .street("Rua A")
                .number("100")
                .neighborhood("Centro")
                .city("Sao Paulo")
                .state("SP")
                .zipCode(new ZipCode("01000"))
                .build();

        when(originAddressService.originAddress()).thenReturn(originAddress);

        ShippingCostService.CalculationResult calculationResult = new ShippingCostService.CalculationResult(
                new Money("35.50"),
                LocalDate.of(2026, 9, 23)
        );
        when(shippingCostService.calculate(any())).thenReturn(calculationResult);

        ShippingCostPreviewInput input = new ShippingCostPreviewInput();
        input.setZipCode("12345");

        ShippingCostPreviewOutput output = shippingApplicationService.previewCost(input);

        assertThat(output).isNotNull();
        assertThat(output.getCost()).isEqualByComparingTo(new BigDecimal("35.50"));
        assertThat(output.getExpectedDate()).isEqualTo(LocalDate.of(2026, 9, 23));
    }

    @Test
    void shouldBuildCorrectCalculationRequest() {
        Address originAddress = Address.builder()
                .street("Rua B")
                .number("200")
                .neighborhood("Jardins")
                .city("Sao Paulo")
                .state("SP")
                .zipCode(new ZipCode("02000"))
                .build();

        when(originAddressService.originAddress()).thenReturn(originAddress);

        ShippingCostService.CalculationResult calculationResult = new ShippingCostService.CalculationResult(
                new Money("10.00"),
                LocalDate.now().plusDays(5)
        );
        when(shippingCostService.calculate(any())).thenReturn(calculationResult);

        ShippingCostPreviewInput input = new ShippingCostPreviewInput();
        input.setZipCode("54321");

        shippingApplicationService.previewCost(input);

        org.mockito.Mockito.verify(shippingCostService).calculate(
                org.mockito.ArgumentMatchers.eq(
                        ShippingCostService.CalculationRequest.builder()
                                .origin(new ZipCode("02000"))
                                .destination(new ZipCode("54321"))
                                .build()
                )
        );
    }
}

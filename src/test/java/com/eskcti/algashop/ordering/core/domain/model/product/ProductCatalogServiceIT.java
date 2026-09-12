package com.eskcti.algashop.ordering.core.domain.model.product;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("it")
class ProductCatalogServiceIT {

    @Autowired
    private ProductCatalogService productCatalogService;

    private WireMockServer wireMockProductCatalog;

    @BeforeEach
    public void setup() {
        wireMockProductCatalog = new WireMockServer(options()
                .port(8781)
                .usingFilesUnderDirectory("src/test/resources/wiremock/product-catalog"));
        wireMockProductCatalog.start();
    }

    @AfterEach
    public void after() {
        wireMockProductCatalog.stop();
    }

    @Test
    public void concurrency() throws Exception {
        UUID rawProductId = UUID.fromString("fffe6ec2-7103-48b3-8e4f-3b58e43fb75a");
        ProductId productId = new ProductId(rawProductId);

        CopyOnWriteArrayList<Future<Optional<Product>>> futures = new CopyOnWriteArrayList<>();

        try (ExecutorService executorService = Executors.newFixedThreadPool(10)) {
            for (int i = 0; i < 6; i++) {
                futures.add(executorService.submit(() -> productCatalogService.ofId(productId)));
            }
            executorService.shutdown();
            executorService.awaitTermination(60, TimeUnit.SECONDS);
        }

        assertThat(futures).hasSize(6);
        for (Future<Optional<Product>> future : futures) {
            assertThat(future).isDone();
            Optional<Product> result = future.get();
            assertThat(result).isPresent();
            assertThat(result.get().id().value()).isEqualTo(rawProductId);
            assertThat(result.get().name().value()).isEqualTo("Notebook X11");
        }
    }

}

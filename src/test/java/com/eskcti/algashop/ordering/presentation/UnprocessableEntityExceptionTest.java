package com.eskcti.algashop.ordering.presentation;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UnprocessableEntityExceptionTest {

    @Test
    void shouldCreateWithDefaultConstructor() {
        UnprocessableEntityException exception = new UnprocessableEntityException();

        assertThat(exception).isInstanceOf(RuntimeException.class);
        assertThat(exception.getMessage()).isNull();
        assertThat(exception.getCause()).isNull();
    }

    @Test
    void shouldCreateWithMessage() {
        UnprocessableEntityException exception = new UnprocessableEntityException("invalid state");

        assertThat(exception.getMessage()).isEqualTo("invalid state");
        assertThat(exception.getCause()).isNull();
    }

    @Test
    void shouldCreateWithMessageAndCause() {
        RuntimeException cause = new RuntimeException("root cause");
        UnprocessableEntityException exception = new UnprocessableEntityException("invalid state", cause);

        assertThat(exception.getMessage()).isEqualTo("invalid state");
        assertThat(exception.getCause()).isSameAs(cause);
    }
}

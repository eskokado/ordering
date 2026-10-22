package com.eskcti.algashop.ordering.utils;

import org.mockito.Mockito;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;

public class MockJwtDecoderFactory {

    public static final String DEFAULT_ISSUER_URI = "http://auth.algashop.local:8081";

    public static final String[] DEFAULT_SCOPES = new String[] {
            "orders:read",
            "orders:write",
            "customers:read",
            "customers:write",
            "shopping-carts:read",
            "shopping-carts:write",
            "shipping-costs:preview"
    };

    public static final String DEFAULT_SUBJECT = MockJwtFactory.DEFAULT_SUBJECT;

    public static final String OTHER_CUSTOMER_TOKEN_VALUE = "fake.jwt.other-customer";

    public static final String OTHER_CUSTOMER_SUBJECT = "7f259ce6-58a7-4133-b0eb-18dfbb305e8b";

    public static final String DEFAULT_TOKEN_VALUE = "fake.jwt.token";

    public static final String[] DEFAULT_AUDIENCES = new String[] { "algashop-api" };

    public static JwtDecoder createMockJwtDecoder() {
        JwtDecoder jwtDecoder = Mockito.mock(JwtDecoder.class);
        Mockito.when(jwtDecoder.decode(Mockito.anyString()))
                .thenReturn(buildJwt(DEFAULT_SUBJECT, DEFAULT_ISSUER_URI, DEFAULT_SCOPES));
        Mockito.when(jwtDecoder.decode(OTHER_CUSTOMER_TOKEN_VALUE))
                .thenReturn(buildJwt(OTHER_CUSTOMER_SUBJECT, DEFAULT_ISSUER_URI, DEFAULT_SCOPES));
        return jwtDecoder;
    }

    public static Jwt buildJwt(String subject, String issuer, String[] scopes) {
        return MockJwtFactory.buildJwt(DEFAULT_TOKEN_VALUE, subject, issuer, scopes,
                MockJwtFactory.DEFAULT_ROLE, DEFAULT_AUDIENCES);
    }

}

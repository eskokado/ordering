package contracts.order

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    request {
        method GET()
        headers {
            accept 'application/json'
        }
        url("/api/v1/orders")
    }
    response {
        status 200
        headers {
            contentType 'application/json'
        }
        body([
            number: 0,
            size: 0,
            totalPages: 1,
            totalElements: 0,
            content: []
        ])
    }
}

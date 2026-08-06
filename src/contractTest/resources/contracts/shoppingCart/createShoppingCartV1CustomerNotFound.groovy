package contracts.shoppingCart

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    request {
        method POST()
        url "/api/v1/shopping-carts"
        headers {
            contentType("application/json")
        }
        body([
                customerId: "73677343-9c25-4bff-a1d8-fea3830b6d97"
        ])
    }
    response {
        status 422
        headers {
            contentType "application/problem+json"
        }
        body([
                instance: fromRequest().path(),
                type: "/errors/unprocessable-entity",
                title: "Unprocessable Entity"
        ])
    }
}

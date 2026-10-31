package contracts.shoppingCart

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    request {
        method POST()
        urlPath("/api/v1/customers/me/shopping-cart/items")
        headers {
            contentType("application/json")
        }
        body([
                productId: "21651a12-b126-4213-ac21-19f66ff4642e",
                quantity: 2
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

package com.ultimatesoftware.banking.transactions.contracts.consumer;

import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.Pact;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.model.RequestResponsePact;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static io.restassured.RestAssured.given;

@ExtendWith(PactConsumerTestExt.class)
@PactTestFor(providerName = "Customers", port = "8085")
public class CustomerConsumerContractTest {

    @Pact(provider="Customers", consumer="Transactions")
    public RequestResponsePact createPact(PactDslWithProvider builder) {
        return builder
            .given("A customer does not exist.")
            .uponReceiving("Empty customer list")
            .path("/api/v1/customers/")
            .method("GET")
            .headers(getHeaders())
            .willRespondWith()
            .status(200)
            .body("[]")
            .toPact();
    }

    @Test
    void test(MockServer mockServer) throws IOException {
        given()
            .headers(getHeaders()).
        when()
            .get(mockServer.getUrl() + "/api/v1/customers/").
        then()
            .statusCode(200);
    }

    private Map<String, String> getHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        return headers;
    }
}

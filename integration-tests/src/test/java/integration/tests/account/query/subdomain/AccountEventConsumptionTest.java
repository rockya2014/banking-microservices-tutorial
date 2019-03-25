package integration.tests.account.query.subdomain;

import com.ultimatesoftware.banking.account.query.models.Account;
import integration.tests.utils.RestHelper;
import io.restassured.RestAssured;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

// Requires docker-compose-sub-domain-testing.yml be up.
public class AccountEventConsumptionTest {
    private static final Logger logger = LoggerFactory.getLogger(AccountEventConsumptionTest.class);
    private final RestHelper restHelper = new RestHelper();

    @AfterEach
    public void afterEach() {
        restHelper.clearAccounts();
    }

    @Test
    public void givenAccountCreated_whenGettingAccount_ThenAccountIsFound() {
        // Arrange
        String customerId = "5c8ffe2b7c0bec3538855a0a";
        double balance = 0;
        String accountId = restHelper.createAccount(customerId, balance);

        // Act
        RestAssured.baseURI = "http://localhost:8084";
        Account account =  given().urlEncodingEnabled(true)
            .get("/api/v1/accounts/" + accountId)
            .then()
            .statusCode(200)
            .extract()
            .response()
            .as(Account.class);

        // Assert
        assertEquals(accountId, account.getId().toHexString());
        assertEquals(customerId, account.getCustomerId());
        assertEquals(balance, account.getBalance());
    }

    @Test
    public void givenAccountCreatedCalledWithBalanceGreaterThanZero_whenGettingAccount_ThenAccountIsFoundWithZeroBalance() {
        // Arrange
        String customerId = "5c8ffe2b7c0bec3538855a0a";
        double balance = 50.00;
        String accountId = restHelper.createAccount(customerId, balance);

        // Act
        RestAssured.baseURI = "http://localhost:8084";
        Account account =  given().urlEncodingEnabled(true)
            .get("/api/v1/accounts/" + accountId)
            .then()
            .statusCode(200)
            .extract()
            .response()
            .as(Account.class);

        // Assert
        assertEquals(accountId, account.getId().toHexString());
        assertEquals(customerId, account.getCustomerId());
        assertEquals(0, account.getBalance());
    }

    @Test
    public void givenNoAccounts_whenGettingAccounts_thenEmptyListReturned() {
        // Arrange

        // Act
        RestAssured.baseURI = "http://localhost:8084";
        List list =  given().urlEncodingEnabled(true)
            .get("/api/v1/accounts/")
            .then()
            .statusCode(200)
            .extract()
            .response()
            .as(List.class);

        // Assert
        assertEquals(0, list.size());
    }

    @Test
    public void givenOneAccount_whenGettingAccounts_thenListOfOneReturned() {
        // Arrange
        restHelper.createAccount("5c8ffe2b7c0bec3538855a0a", 0.0);
        // Act
        RestAssured.baseURI = "http://localhost:8084";
        List list =  given().urlEncodingEnabled(true)
            .get("/api/v1/accounts/")
            .then()
            .statusCode(200)
            .extract()
            .response()
            .as(List.class);

        // Assert
        assertEquals(1, list.size());
    }

    @Test
    public void givenThreeAccounts_whenGettingAccounts_thenListOfThreeReturned() {
        // Arrange
        restHelper.createAccount("5c8ffe2b7c0bec3538855a0a", 0.0);
        restHelper.createAccount("5c8ffe2b7c0bec3538855a0a", 0.0);
        restHelper.createAccount("5c8ffe2b7c0bec3538855a0a", 0.0);
        // Act
        RestAssured.baseURI = "http://localhost:8084";
        List list =  given().urlEncodingEnabled(true)
            .get("/api/v1/accounts/")
            .then()
            .statusCode(200)
            .extract()
            .response()
            .as(List.class);

        // Assert
        assertEquals(3, list.size());
    }

    @Test
    public void givenAccountDeleted_whenGettingAccount_thenNoAccountReturned() {
        // Arrange
        String accountId = restHelper.createAccount("5c8ffe2b7c0bec3538855a0a", 0.0);
        restHelper.deleteAccount(accountId);

        // Act
        RestAssured.baseURI = "http://localhost:8084";
        given().urlEncodingEnabled(true)
            .get("/api/v1/accounts/" + accountId)
            .then()
            .statusCode(404);

        // Assert
    }
}
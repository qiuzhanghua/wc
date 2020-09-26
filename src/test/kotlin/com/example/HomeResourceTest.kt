package com.example

import io.quarkus.test.junit.QuarkusTest
import org.junit.jupiter.api.Test
import io.restassured.RestAssured.given
import org.hamcrest.CoreMatchers.`is`

@QuarkusTest
class HomeResourceTest {

    @Test
    fun testHomeEndpoint() {
        given()
                .`when`().get("/")
                .then()
                .statusCode(200)
                .body(`is`("Work Checker!"))
    }

}
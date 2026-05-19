package net.krusher.datalinks;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.containsString;

@QuarkusTest
class DatalinksTests {

	@Test
	void contextLoads() {
	}

	@Test
	void testPageIsLoaded() {
		RestAssured.given()
				.when().get("/page/test-page")
				.then()
				.statusCode(200)
				.body(containsString("Test Page"));
	}

}

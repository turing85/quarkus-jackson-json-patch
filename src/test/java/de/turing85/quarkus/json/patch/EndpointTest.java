package de.turing85.quarkus.json.patch;

import java.net.URL;

import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import org.jboss.resteasy.reactive.common.util.Encode;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
@TestHTTPEndpoint(Endpoint.class)
class EndpointTest {

  @TestHTTPEndpoint(Endpoint.class)
  @TestHTTPResource
  URL url;

  @Test
  void whenPatchAlice_thenAllGood() {
    // given
    // @formatter:off
    RestAssured
        .given()
            .contentType(MediaType.APPLICATION_JSON_PATCH_JSON)
            .body("""
                [
                  {
                    "op": "replace",
                    "path": "/name",
                    "value": "alice wonder"
                  },
                  {
                    "op": "replace",
                    "path": "/email",
                    "value": "alice@wonder.land"
                  }
                ]""")

    // when
        .when().patch("alice")

    // then
        .then()
            .statusCode(is(Response.Status.OK.getStatusCode()))
            .contentType(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.LOCATION, "%s/%s".formatted(url, Encode.encodePath("alice wonder")))
            .body("name", is("alice wonder"))
            .body("email", is("alice@wonder.land"));
    RestAssured
        .when().get("alice wonder")
        .then()
            .statusCode(is(Response.Status.OK.getStatusCode()))
            .contentType(MediaType.APPLICATION_JSON)
            .body("name", is("alice wonder"))
            .body("email", is("alice@wonder.land"));
    // @formatter:on
  }

  @Test
  void whenGetBob_thenAllGood() {
    // @formatter:off
    RestAssured
        .when().get("bob")
        .then()
            .statusCode(is(Response.Status.OK.getStatusCode()))
            .contentType(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.LOCATION, "%s/%s".formatted(url, "bob"))
            .body("name", is("bob"))
            .body("email", is("bob@gmail.com"));
    // @formatter:on
  }

  @Test
  void whenGetUnknown_thenGetNotFound() {
    // @formatter:off
    RestAssured
        .when().get("unknown")
        .then()
            .statusCode(is(Response.Status.NOT_FOUND.getStatusCode()))
            .contentType(MediaType.APPLICATION_JSON);
    // @formatter:on
  }

  @Test
  void whenPatchUnknownField_thenGetBadRequest() {
 // @formatter:off
    RestAssured
        .given()
            .contentType(MediaType.APPLICATION_JSON_PATCH_JSON)
            .body("""
                [
                  {
                    "op": "replace",
                    "path": "/email",
                    "value": "claire@clear.com"
                  },
                  {
                    "op": "replace",
                    "path": "/unknown",
                    "value": "boom"
                  }
                ]""")
        .when().patch("claire")
        .then()
            .statusCode(is(Response.Status.BAD_REQUEST.getStatusCode()))
            .contentType(MediaType.APPLICATION_JSON);
    // @formatter:on
  }
}

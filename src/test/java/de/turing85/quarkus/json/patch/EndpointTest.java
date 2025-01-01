package de.turing85.quarkus.json.patch;

import java.net.URI;

import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;

import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;

@QuarkusTest
@TestHTTPEndpoint(Endpoint.class)
class EndpointTest {

  @TestHTTPEndpoint(Endpoint.class)
  @TestHTTPResource
  URI uri;

  @Test
  void whenPatchReplaceAlice_thenAllGood() {
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
            .statusCode(Response.Status.OK.getStatusCode())
            .contentType(MediaType.APPLICATION_JSON)
            .header(
                HttpHeaders.LOCATION,
                UriBuilder.fromUri(uri).path("alice wonder").build().toASCIIString())
            .header(HttpHeaders.CONTENT_LENGTH, is(notNullValue()))
            .body("name", is("alice wonder"))
            .body("email", is("alice@wonder.land"));
    RestAssured
        .when().get("alice wonder")
        .then()
            .statusCode(Response.Status.OK.getStatusCode())
            .contentType(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.CONTENT_LENGTH, is(notNullValue()))
            .body("name", is("alice wonder"))
            .body("email", is("alice@wonder.land"));
    // @formatter:on
  }

  @Test
  void whenPatchDeleteBob_thenAllGood() {
    // given
    // @formatter:off
    RestAssured
        .given()
            .contentType(MediaType.APPLICATION_JSON_PATCH_JSON)
            .body("""
                [
                  {
                    "op": "remove",
                    "path": "/email"
                  }
                ]""")

    // when
        .when().patch("bob")

    // then
        .then()
            .statusCode(Response.Status.OK.getStatusCode())
            .contentType(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.CONTENT_LENGTH, is(notNullValue()))
            .header(
                HttpHeaders.LOCATION,
                UriBuilder.fromUri(uri).path("bob").build().toASCIIString())
            .body("name", is("bob"))
            .body("email", is(nullValue()));
    RestAssured
        .when().get("bob")
        .then()
            .statusCode(Response.Status.OK.getStatusCode())
            .contentType(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.CONTENT_LENGTH, is(notNullValue()))
            .header(
                HttpHeaders.LOCATION,
                UriBuilder.fromUri(uri).path("bob").build().toASCIIString())
            .body("name", is("bob"))
            .body("email", is(nullValue()));
    // @formatter:on
  }

  @Test
  void whenGetUnknown_thenGetNotFound() {
    // @formatter:off
    RestAssured
        .when().get("unknown")
        .then()
            .statusCode(Response.Status.NOT_FOUND.getStatusCode())
            .contentType(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.CONTENT_LENGTH, is(notNullValue()));
    // @formatter:on
  }

  @Test
  void whenPatchUnknownField_thenGetBadRequest() {
    // given
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

    // when
        .when().patch("claire")

    // then
        .then()
            .statusCode(Response.Status.BAD_REQUEST.getStatusCode())
            .contentType(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.CONTENT_LENGTH, is(notNullValue()));
    RestAssured
        .when().get("claire")
        .then()
            .statusCode(Response.Status.OK.getStatusCode())
            .contentType(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.CONTENT_LENGTH, is(notNullValue()))
            .header(
                HttpHeaders.LOCATION,
                UriBuilder.fromUri(uri).path("claire").build().toASCIIString())
            .body("name", is("claire"))
            .body("email", is("claire@gmail.com"));
    // @formatter:on
  }
}

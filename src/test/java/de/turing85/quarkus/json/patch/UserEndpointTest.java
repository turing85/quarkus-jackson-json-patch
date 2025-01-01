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
@TestHTTPEndpoint(UserEndpoint.class)
class UserEndpointTest {
  @TestHTTPEndpoint(UserEndpoint.class)
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
                    "op": "test",
                    "path": "/name",
                    "value": "alice"
                  },
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
  void whenPatchMoveClaire_thenAllGood() {
    // given
    // @formatter:off
    RestAssured
        .given()
        .contentType(MediaType.APPLICATION_JSON_PATCH_JSON)
        .body("""
                [
                  {
                    "op": "move",
                    "from": "/email",
                    "path": "/name"
                  }
                ]""")

        // when
        .when().patch("claire")

        // then
        .then()
        .statusCode(Response.Status.OK.getStatusCode())
        .contentType(MediaType.APPLICATION_JSON)
        .header(HttpHeaders.CONTENT_LENGTH, is(notNullValue()))
        .header(
            HttpHeaders.LOCATION,
            UriBuilder.fromUri(uri).path("claire@gmail.com").build().toASCIIString())
        .body("name", is("claire@gmail.com"))
        .body("email", is(nullValue()));
    RestAssured
        .when().get("claire@gmail.com")
        .then()
        .statusCode(Response.Status.OK.getStatusCode())
        .contentType(MediaType.APPLICATION_JSON)
        .header(HttpHeaders.CONTENT_LENGTH, is(notNullValue()))
        .header(
            HttpHeaders.LOCATION,
            UriBuilder.fromUri(uri).path("claire@gmail.com").build().toASCIIString())
        .body("name", is("claire@gmail.com"))
        .body("email", is(nullValue()));
    // @formatter:on
  }

  @Test
  void whenPatchCopyDaphne_thenAllGood() {
    // given
    // @formatter:off
    RestAssured
        .given()
        .contentType(MediaType.APPLICATION_JSON_PATCH_JSON)
        .body("""
                [
                  {
                    "op": "copy",
                    "from": "/email",
                    "path": "/name"
                  }
                ]""")

        // when
        .when().patch("daphne")

        // then
        .then()
        .statusCode(Response.Status.OK.getStatusCode())
        .contentType(MediaType.APPLICATION_JSON)
        .header(HttpHeaders.CONTENT_LENGTH, is(notNullValue()))
        .header(
            HttpHeaders.LOCATION,
            UriBuilder.fromUri(uri).path("daphne@gmail.com").build().toASCIIString())
        .body("name", is("daphne@gmail.com"))
        .body("email", is("daphne@gmail.com"));
    RestAssured
        .when().get("daphne@gmail.com")
        .then()
        .statusCode(Response.Status.OK.getStatusCode())
        .contentType(MediaType.APPLICATION_JSON)
        .header(HttpHeaders.CONTENT_LENGTH, is(notNullValue()))
        .header(
            HttpHeaders.LOCATION,
            UriBuilder.fromUri(uri).path("daphne@gmail.com").build().toASCIIString())
        .body("name", is("daphne@gmail.com"))
        .body("email", is("daphne@gmail.com"));
    // @formatter:on
  }

  @Test
  void whenPatchUnknownFieldElvira_thenGetBadRequest() {
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
                    "value": "elvira@enve.lope"
                  },
                  {
                    "op": "replace",
                    "path": "/unknown",
                    "value": "boom"
                  }
                ]""")

    // when
        .when().patch("elvira")

    // then
        .then()
            .statusCode(Response.Status.BAD_REQUEST.getStatusCode())
            .contentType(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.CONTENT_LENGTH, is(notNullValue()));
    RestAssured
        .when().get("elvira")
        .then()
            .statusCode(Response.Status.OK.getStatusCode())
            .contentType(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.CONTENT_LENGTH, is(notNullValue()))
            .header(
                HttpHeaders.LOCATION,
                UriBuilder.fromUri(uri).path("elvira").build().toASCIIString())
            .body("name", is("elvira"))
            .body("email", is("elvira@gmail.com"));
    // @formatter:on
  }

  @Test
  void whenPatchTEstFailsElvira_thenGetBadRequest() {
    // given
    // @formatter:off
    RestAssured
        .given()
            .contentType(MediaType.APPLICATION_JSON_PATCH_JSON)
            .body("""
                [
                  {
                    "op": "test",
                    "path": "/name",
                    "value": "not elvira"
                  },
                  {
                    "op": "replace",
                    "path": "/email",
                    "value": "elvira@enve.lope"
                  }
                ]""")

        // when
        .when().patch("elvira")

        // then
        .then()
            .statusCode(Response.Status.BAD_REQUEST.getStatusCode())
            .contentType(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.CONTENT_LENGTH, is(notNullValue()));
    RestAssured
        .when().get("elvira")
        .then()
            .statusCode(Response.Status.OK.getStatusCode())
            .contentType(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.CONTENT_LENGTH, is(notNullValue()))
            .header(
                HttpHeaders.LOCATION,
                UriBuilder.fromUri(uri).path("elvira").build().toASCIIString())
            .body("name", is("elvira"))
            .body("email", is("elvira@gmail.com"));
    // @formatter:on
  }
}

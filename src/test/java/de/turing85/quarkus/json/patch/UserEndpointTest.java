package de.turing85.quarkus.json.patch;

import java.net.URI;

import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;

import de.turing85.quarkus.json.patch.dao.impl.in.memory.InMemoryUser;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;

@QuarkusTest
@DisplayName("User Endpoint")
@TestHTTPEndpoint(UserEndpoint.class)
class UserEndpointTest {
  @TestHTTPEndpoint(UserEndpoint.class)
  @TestHTTPResource
  URI uri;

  private static final InMemoryUser ALICE =
      InMemoryUser.builder().name("alice").email("alice@gmail.com").build();

  @BeforeEach
  void setup() {
    deleteAllUsers();
    // @formatter:off
    createAlice();
    // @formatter:on
  }

  private void deleteAllUsers() {
    // @formatter:off
    RestAssured
        .when().delete()
        .then()
            .statusCode(Response.Status.OK.getStatusCode());
    RestAssured
        .when().get()
        .then()
            .statusCode(Response.Status.OK.getStatusCode())
            .contentType(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.CONTENT_LENGTH, is(notNullValue()))
            .header(HttpHeaders.LOCATION, uri.toASCIIString());
    // @formatter:on
  }

  private void createAlice() {
    // @formatter:off
    RestAssured
        .given()
            .contentType(MediaType.APPLICATION_JSON)
            .body(ALICE)
        .when().post()
        .then()
            .statusCode(Response.Status.CREATED.getStatusCode())
            .header(
                HttpHeaders.LOCATION,
                UriBuilder.fromUri(uri).path(ALICE.getName()).build().toASCIIString())
            .header(HttpHeaders.CONTENT_LENGTH, is(notNullValue()))
            .body("name", is(ALICE.getName()))
            .body("email", is(ALICE.getEmail()));
    RestAssured
        .when().get(ALICE.getName())
        .then()
            .statusCode(Response.Status.OK.getStatusCode())
            .header(
                HttpHeaders.LOCATION,
                UriBuilder.fromUri(uri).path(ALICE.getName()).build().toASCIIString())
            .header(HttpHeaders.CONTENT_LENGTH, is(notNullValue()))
            .body("name", is(ALICE.getName()))
            .body("email", is(ALICE.getEmail()));
    // @formatter:on
  }

  @Test
  @DisplayName("Delete existing → ✅")
  void whenDelete_thenAllGood() {
    // when
    // @formatter:off
    RestAssured
        .when().delete(ALICE.getName())

    // when
        .then()
            .statusCode(Response.Status.OK.getStatusCode())
            .header(
                    HttpHeaders.LOCATION,
                    UriBuilder.fromUri(uri).path(ALICE.getName()).build().toASCIIString())
            .contentType(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.CONTENT_LENGTH, is(notNullValue()))
            .body("name", is(ALICE.getName()))
            .body("email", is(ALICE.getEmail()));
    RestAssured
        .when().get(ALICE.getName())
        .then()
            .statusCode(Response.Status.NOT_FOUND.getStatusCode())
            .contentType(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.CONTENT_LENGTH, is(notNullValue()));
    // @formatter:on
  }

  @Test
  @DisplayName("Delete non-existing → ❌")
  void whenDeleteNonExisting_thenGetNotFound() {
    // @formatter:off
    RestAssured
        .when().delete("does-not-exist")
        .then()
            .statusCode(Response.Status.NOT_FOUND.getStatusCode())
            .contentType(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.CONTENT_LENGTH, is(notNullValue()));
    // @formatter:on
  }

  @Test
  @DisplayName("Patch Replace → ✅")
  void whenPatchReplace_thenAllGood() {
    // given
    // @formatter:off
    final String newName = "alice wonder";
    final String newEmail = "alice@wonder.land";
    RestAssured
        .given()
            .contentType(MediaType.APPLICATION_JSON_PATCH_JSON)
            .body("""
                [
                  {
                    "op": "test",
                    "path": "/name",
                    "value": "%s"
                  },
                  {
                    "op": "replace",
                    "path": "/name",
                    "value": "%s"
                  },
                  {
                    "op": "replace",
                    "path": "/email",
                    "value": "%s"
                  }
                ]""".formatted(ALICE.getName(), newName, newEmail))

    // when
        .when().patch(ALICE.getName())

    // then
        .then()
            .statusCode(Response.Status.OK.getStatusCode())
            .contentType(MediaType.APPLICATION_JSON)
            .header(
                HttpHeaders.LOCATION,
                UriBuilder.fromUri(uri).path(newName).build().toASCIIString())
            .header(HttpHeaders.CONTENT_LENGTH, is(notNullValue()))
            .body("name", is(newName))
            .body("email", is(newEmail));
    RestAssured
        .when().get(newName)
        .then()
            .statusCode(Response.Status.OK.getStatusCode())
            .contentType(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.CONTENT_LENGTH, is(notNullValue()))
            .body("name", is(newName))
            .body("email", is(newEmail));
    // @formatter:on
  }

  @Test
  @DisplayName("Patch Delete → ✅")
  void whenPatchDelete_thenAllGood() {
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
        .when().patch(ALICE.getName())

    // then
        .then()
            .statusCode(Response.Status.OK.getStatusCode())
            .contentType(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.CONTENT_LENGTH, is(notNullValue()))
            .header(
                HttpHeaders.LOCATION,
                UriBuilder.fromUri(uri).path(ALICE.getName()).build().toASCIIString())
            .body("name", is(ALICE.getName()))
            .body("email", is(nullValue()));
    RestAssured
        .when().get(ALICE.getName())
        .then()
            .statusCode(Response.Status.OK.getStatusCode())
            .contentType(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.CONTENT_LENGTH, is(notNullValue()))
            .header(
                HttpHeaders.LOCATION,
                UriBuilder.fromUri(uri).path(ALICE.getName()).build().toASCIIString())
            .body("name", is(ALICE.getName()))
            .body("email", is(nullValue()));
    // @formatter:on
  }

  @Test
  @DisplayName("Patch Move → ✅")
  void whenPatchMove_thenAllGood() {
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
        .when().patch(ALICE.getName())

        // then
        .then()
            .statusCode(Response.Status.OK.getStatusCode())
            .contentType(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.CONTENT_LENGTH, is(notNullValue()))
            .header(
                HttpHeaders.LOCATION,
                UriBuilder.fromUri(uri).path(ALICE.getEmail()).build().toASCIIString())
            .body("name", is(ALICE.getEmail()))
            .body("email", is(nullValue()));
    RestAssured
        .when().get(ALICE.getEmail())
        .then()
            .statusCode(Response.Status.OK.getStatusCode())
            .contentType(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.CONTENT_LENGTH, is(notNullValue()))
            .header(
                HttpHeaders.LOCATION,
                UriBuilder.fromUri(uri).path(ALICE.getEmail()).build().toASCIIString())
            .body("name", is(ALICE.getEmail()))
            .body("email", is(nullValue()));
    // @formatter:on
  }

  @Test
  @DisplayName("Patch Copy → ✅")
  void whenPatchCopy_thenAllGood() {
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
        .when().patch(ALICE.getName())

        // then
        .then()
            .statusCode(Response.Status.OK.getStatusCode())
            .contentType(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.CONTENT_LENGTH, is(notNullValue()))
            .header(
                HttpHeaders.LOCATION,
                UriBuilder.fromUri(uri).path(ALICE.getEmail()).build().toASCIIString())
            .body("name", is(ALICE.getEmail()))
            .body("email", is(ALICE.getEmail()));
    RestAssured
        .when().get(ALICE.getEmail())
        .then()
            .statusCode(Response.Status.OK.getStatusCode())
            .contentType(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.CONTENT_LENGTH, is(notNullValue()))
            .header(
                HttpHeaders.LOCATION,
                UriBuilder.fromUri(uri).path(ALICE.getEmail()).build().toASCIIString())
            .body("name", is(ALICE.getEmail()))
            .body("email", is(ALICE.getEmail()));
    // @formatter:on
  }

  @Test
  @DisplayName("Patch unknown field → ❌")
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
                    "value": "alice@wonder.land"
                  },
                  {
                    "op": "replace",
                    "path": "/unknown",
                    "value": "boom"
                  }
                ]""")

    // when
        .when().patch(ALICE.getName())

    // then
        .then()
            .statusCode(Response.Status.BAD_REQUEST.getStatusCode())
            .contentType(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.CONTENT_LENGTH, is(notNullValue()));
    RestAssured
        .when().get(ALICE.getName())
        .then()
            .statusCode(Response.Status.OK.getStatusCode())
            .contentType(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.CONTENT_LENGTH, is(notNullValue()))
            .header(
                HttpHeaders.LOCATION,
                UriBuilder.fromUri(uri).path(ALICE.getName()).build().toASCIIString())
            .body("name", is(ALICE.getName()))
            .body("email", is(ALICE.getEmail()));
    // @formatter:on
  }

  @Test
  @DisplayName("Patch test fails → ❌")
  void whenPatchTestFails_thenGetBadRequest() {
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
                    "value": "not alice"
                  },
                  {
                    "op": "replace",
                    "path": "/email",
                    "value": "alice@wonder.land"
                  }
                ]""")

        // when
        .when().patch(ALICE.getName())

        // then
        .then()
            .statusCode(Response.Status.BAD_REQUEST.getStatusCode())
            .contentType(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.CONTENT_LENGTH, is(notNullValue()));
    RestAssured
        .when().get(ALICE.getName())
        .then()
            .statusCode(Response.Status.OK.getStatusCode())
            .contentType(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.CONTENT_LENGTH, is(notNullValue()))
            .header(
                HttpHeaders.LOCATION,
                UriBuilder.fromUri(uri).path(ALICE.getName()).build().toASCIIString())
            .body("name", is(ALICE.getName()))
            .body("email", is(ALICE.getEmail()));
    // @formatter:on
  }
}

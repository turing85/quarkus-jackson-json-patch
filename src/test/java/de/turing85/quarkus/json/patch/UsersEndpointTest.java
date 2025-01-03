package de.turing85.quarkus.json.patch;

import java.net.URI;

import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;

import de.turing85.quarkus.json.patch.api.request.CreateUserRequest;
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
import static org.hamcrest.Matchers.emptyString;

@QuarkusTest
@DisplayName("Users Endpoint")
@TestHTTPEndpoint(UsersEndpoint.class)
class UsersEndpointTest {
  private static final CreateUserRequest ALICE = new CreateUserRequest("alice", "alice@gmail.com");

  @TestHTTPEndpoint(UsersEndpoint.class)
  @TestHTTPResource
  URI uri;

  @BeforeEach
  void setup() {
    deleteAllUsers();
    createAlice();
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
            .header(HttpHeaders.CONTENT_LENGTH, is(notNullValue()));
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
                UriBuilder.fromUri(uri).path(ALICE.name()).build().toASCIIString())
            .header(HttpHeaders.CONTENT_LENGTH, is(notNullValue()))
            .body("name", is(ALICE.name()))
            .body("email", is(ALICE.email()));
    RestAssured
        .when().get(ALICE.name())
        .then()
            .statusCode(Response.Status.OK.getStatusCode())
            .header(HttpHeaders.CONTENT_LENGTH, is(notNullValue()))
            .body("name", is(ALICE.name()))
            .body("email", is(ALICE.email()));
    // @formatter:on
  }

  @Test
  @DisplayName("Delete existing → 200 OK ✅")
  void whenDelete_thenAllGood() {
    // when
    // @formatter:off
    RestAssured
        .when().delete(ALICE.name())

    // then
        .then()
            .statusCode(Response.Status.OK.getStatusCode())
            .contentType(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.CONTENT_LENGTH, is(notNullValue()))
            .body("name", is(ALICE.name()))
            .body("email", is(ALICE.email()));
    RestAssured
        .when().get(ALICE.name())
        .then()
            .statusCode(Response.Status.NOT_FOUND.getStatusCode())
            .contentType(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.CONTENT_LENGTH, is(notNullValue()));
    // @formatter:on
  }

  @Test
  @DisplayName("Delete non-existing → 404 NOT FOUND ❌")
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
  @DisplayName("Patch Replace → 200 OK ✅")
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
                ]""".formatted(ALICE.name(), newName, newEmail))

    // when
        .when().patch(ALICE.name())

    // then
        .then()
            .statusCode(Response.Status.OK.getStatusCode())
            .contentType(MediaType.APPLICATION_JSON)
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
  @DisplayName("Patch Replace without change → 204 NO CONTENT ✅")
  void whenPatchReplaceNoChange_thenAllGood() {
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
                    "value": "%1$s"
                  },
                  {
                    "op": "test",
                    "path": "/email",
                    "value": "%2$s"
                  },
                  {
                    "op": "replace",
                    "path": "/name",
                    "value": "%1$s"
                  },
                  {
                    "op": "replace",
                    "path": "/email",
                    "value": "%2$s"
                  }
                ]""".formatted(ALICE.name(), ALICE.email()))

    // when
        .when().patch(ALICE.name())

    // then
        .then()
            .statusCode(Response.Status.NO_CONTENT.getStatusCode())
            .body(is(emptyString()));
    RestAssured
        .when().get(ALICE.name())
        .then()
            .statusCode(Response.Status.OK.getStatusCode())
            .contentType(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.CONTENT_LENGTH, is(notNullValue()))
            .body("name", is(ALICE.name()))
            .body("email", is(ALICE.email()));
    // @formatter:on
  }

  @Test
  @DisplayName("Patch Delete → 200 OK ✅")
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
        .when().patch(ALICE.name())

    // then
        .then()
            .statusCode(Response.Status.OK.getStatusCode())
            .contentType(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.CONTENT_LENGTH, is(notNullValue()))
            .body("name", is(ALICE.name()))
            .body("email", is(nullValue()));
    RestAssured
        .when().get(ALICE.name())
        .then()
            .statusCode(Response.Status.OK.getStatusCode())
            .contentType(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.CONTENT_LENGTH, is(notNullValue()))
            .body("name", is(ALICE.name()))
            .body("email", is(nullValue()));
    // @formatter:on
  }

  @Test
  @DisplayName("Patch Move → 200 OK ✅")
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
        .when().patch(ALICE.name())

    // then
        .then()
            .statusCode(Response.Status.OK.getStatusCode())
            .contentType(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.CONTENT_LENGTH, is(notNullValue()))
            .body("name", is(ALICE.email()))
            .body("email", is(nullValue()));
    RestAssured
        .when().get(ALICE.email())
        .then()
            .statusCode(Response.Status.OK.getStatusCode())
            .contentType(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.CONTENT_LENGTH, is(notNullValue()))
            .body("name", is(ALICE.email()))
            .body("email", is(nullValue()));
    // @formatter:on
  }

  @Test
  @DisplayName("Patch Copy → 200 OK ✅")
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
        .when().patch(ALICE.name())

    // then
        .then()
            .statusCode(Response.Status.OK.getStatusCode())
            .contentType(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.CONTENT_LENGTH, is(notNullValue()))
            .body("name", is(ALICE.email()))
            .body("email", is(ALICE.email()));
    RestAssured
        .when().get(ALICE.email())
        .then()
            .statusCode(Response.Status.OK.getStatusCode())
            .contentType(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.CONTENT_LENGTH, is(notNullValue()))
            .body("name", is(ALICE.email()))
            .body("email", is(ALICE.email()));
    // @formatter:on
  }

  @Test
  @DisplayName("Patch createdAt field → 400 BAD REQUEST ❌")
  void whenPatchCreatedAtField_thenGetBadRequest() {
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
                    "op": "copy",
                    "from": "/createdAt",
                    "path": "/name"
                  }
                ]""")

    // when
        .when().patch(ALICE.name())

    // then
        .then()
            .statusCode(Response.Status.BAD_REQUEST.getStatusCode())
            .contentType(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.CONTENT_LENGTH, is(notNullValue()));
    RestAssured
        .when().get(ALICE.name())
        .then()
            .statusCode(Response.Status.OK.getStatusCode())
            .contentType(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.CONTENT_LENGTH, is(notNullValue()))
            .body("name", is(ALICE.name()))
            .body("email", is(ALICE.email()));
    // @formatter:on
  }

  @Test
  @DisplayName("Patch test fails → 400 BAD REQUEST ❌")
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
        .when().patch(ALICE.name())

    // then
        .then()
            .statusCode(Response.Status.BAD_REQUEST.getStatusCode())
            .contentType(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.CONTENT_LENGTH, is(notNullValue()));
    RestAssured
        .when().get(ALICE.name())
        .then()
            .statusCode(Response.Status.OK.getStatusCode())
            .contentType(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.CONTENT_LENGTH, is(notNullValue()))
            .body("name", is(ALICE.name()))
            .body("email", is(ALICE.email()));
    // @formatter:on
  }

  @Test
  @DisplayName("Broken Patch → 400 BAD REQUEST ❌")
  void whenPatchIsBroken_thenGetBadRequest() {
    // @formatter:off
    RestAssured
        .given()
            .contentType(MediaType.APPLICATION_JSON_PATCH_JSON)
            .body("""
                    [
                      {
                        "op": "broken"
                      }
                    ]""")

        .when().patch("does-not-matter")

        .then()
            .statusCode(Response.Status.BAD_REQUEST.getStatusCode())
            .contentType(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.CONTENT_LENGTH, is(notNullValue()));
    // @formatter:on
  }
}

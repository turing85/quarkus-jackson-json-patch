package de.turing85.quarkus.json.patch;

import java.net.URI;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.turing85.quarkus.json.patch.api.request.CreateUserRequest;
import de.turing85.quarkus.json.patch.api.response.UserResponse;
import de.turing85.quarkus.json.patch.spi.User;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.emptyString;

@QuarkusTest
@DisplayName("Users Endpoint")
@TestHTTPEndpoint(UsersEndpoint.class)
class UsersEndpointTest {
  private static final ObjectMapper MAPPER = new ObjectMapper();
  public static final String ALICE_NAME = "alice";
  private static final CreateUserRequest ALICE =
      CreateUserRequest.of(ALICE_NAME, "alice@gmail.com");

  @Nullable
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
            .statusCode(Response.Status.OK.getStatusCode())
            .contentType(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.CONTENT_LENGTH, is(notNullValue()));
    RestAssured
        .when().get()
        .then()
            .statusCode(Response.Status.OK.getStatusCode())
            .contentType(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.CONTENT_LENGTH, is(notNullValue()))
            .body("", is(Collections.emptyList()));
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
            .contentType(MediaType.APPLICATION_JSON)
            .header(
                HttpHeaders.LOCATION,
                UriBuilder.fromUri(uri).path(ALICE.name()).build().toASCIIString())
            .header(HttpHeaders.CONTENT_LENGTH, is(notNullValue()))
            .body("", is(toMap(ALICE)));
    RestAssured
        .when().get(ALICE.name())
        .then()
            .statusCode(Response.Status.OK.getStatusCode())
            .contentType(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.CONTENT_LENGTH, is(notNullValue()))
            .body("", is(toMap(ALICE)));
    // @formatter:on
  }

  private static Map<String, Object> toMap(User user) {
    return MAPPER.convertValue(UserResponse.of(user), new TypeReference<>() {});
  }

  @Test
  @DisplayName("Delete existing → 400 BAD REQUEST ✅")
  void whenCreateExisting_thenBadRequest() {
    // given
    // @formatter:off
    RestAssured
        .given()
            .contentType(MediaType.APPLICATION_JSON)
            .body(ALICE)
    // when
        .when().post()

    // then
        .then()
            .statusCode(Response.Status.BAD_REQUEST.getStatusCode())
            .contentType(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.CONTENT_LENGTH, is(notNullValue()))
            .body(is(not(emptyString())));
    RestAssured
        .when().get(ALICE.name())
        .then()
            .statusCode(Response.Status.OK.getStatusCode())
            .contentType(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.CONTENT_LENGTH, is(notNullValue()))
            .body("", is(toMap(ALICE)));
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
            .body("", is(toMap(ALICE)));
    RestAssured
        .when().get(ALICE.name())
        .then()
            .statusCode(Response.Status.NOT_FOUND.getStatusCode())
            .contentType(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.CONTENT_LENGTH, is(notNullValue()))
            .body(is(not(emptyString())));
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
            .header(HttpHeaders.CONTENT_LENGTH, is(notNullValue()))
            .body(is(not(emptyString())));
    // @formatter:on
  }

  @Test
  @DisplayName("Patch Replace → 200 OK ✅")
  void whenPatchReplace_thenAllGood() {
    // given
    final CreateUserRequest updatedAlice =
        CreateUserRequest.of("alice wonder", "alice@wonder.land");
    // @formatter:off
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
                ]""".formatted(ALICE.name(), updatedAlice.name(), updatedAlice.email()))

    // when
        .when().patch(ALICE.name())

    // then
        .then()
            .statusCode(Response.Status.OK.getStatusCode())
            .contentType(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.CONTENT_LENGTH, is(notNullValue()))
            .body("", is(toMap(updatedAlice)));
    RestAssured
        .when().get(updatedAlice.name())
        .then()
            .statusCode(Response.Status.OK.getStatusCode())
            .contentType(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.CONTENT_LENGTH, is(notNullValue()))
            .body("", is(toMap(updatedAlice)));
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
            .body("", is(toMap(ALICE)));
    // @formatter:on
  }

  @Test
  @DisplayName("Patch Delete → 200 OK ✅")
  void whenPatchDelete_thenAllGood() {
    // given
    final CreateUserRequest updatedAlice = CreateUserRequest.of(ALICE.name(), null);
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
            .body("", is(toMap(updatedAlice)));
    RestAssured
        .when().get(updatedAlice.name())
        .then()
            .statusCode(Response.Status.OK.getStatusCode())
            .contentType(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.CONTENT_LENGTH, is(notNullValue()))
            .body("", is(toMap(updatedAlice)));
    // @formatter:on
  }

  @Test
  @DisplayName("Patch Move → 200 OK ✅")
  void whenPatchMove_thenAllGood() {
    // given
    final UserResponse updatedAlice = UserResponse.of(Objects.requireNonNull(ALICE.email()), null);
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
            .body("", is(toMap(updatedAlice)));
    RestAssured
        .when().get(updatedAlice.name())
        .then()
            .statusCode(Response.Status.OK.getStatusCode())
            .contentType(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.CONTENT_LENGTH, is(notNullValue()))
            .body("", is(toMap(updatedAlice)));
    // @formatter:on
  }

  @Test
  @DisplayName("Patch Copy → 200 OK ✅")
  void whenPatchCopy_thenAllGood() {
    // given
    final CreateUserRequest updatedAlice =
        CreateUserRequest.of(Objects.requireNonNull(ALICE.email()), ALICE.email());
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
            .body("", is(toMap(updatedAlice)));
    RestAssured
        .when().get(updatedAlice.name())
        .then()
            .statusCode(Response.Status.OK.getStatusCode())
            .contentType(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.CONTENT_LENGTH, is(notNullValue()))
            .body("", is(toMap(updatedAlice)));
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
            .header(HttpHeaders.CONTENT_LENGTH, is(notNullValue()))
            .body(is(not(emptyString())));
    RestAssured
        .when().get(ALICE.name())
        .then()
            .statusCode(Response.Status.OK.getStatusCode())
            .contentType(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.CONTENT_LENGTH, is(notNullValue()))
            .body("", is(toMap(ALICE)));
    // @formatter:on
  }

  @Test
  @DisplayName("Patch test createdAt field → 400 BAD REQUEST ❌")
  void whenPatchTestCreatedAt_thenGetBadRequest() {
    // given
    // @formatter:off
    RestAssured
        .given()
            .contentType(MediaType.APPLICATION_JSON_PATCH_JSON)
            .body("""
                    [
                      {
                        "op": "test",
                        "path": "/createdAt",
                        "value": "anything"
                      }
                    ]""")

        // when
        .when().patch(ALICE.name())

        // then
        .then()
            .statusCode(Response.Status.BAD_REQUEST.getStatusCode())
            .contentType(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.CONTENT_LENGTH, is(notNullValue()))
            .body(is(not(emptyString())));
    // @formatter:on
  }

  @Test
  @DisplayName("Patch test fails → 409 CONFLICT ❌")
  void whenPatchTestFails_thenGetConflict() {
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
            .statusCode(Response.Status.CONFLICT.getStatusCode())
            .contentType(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.CONTENT_LENGTH, is(notNullValue()))
            .body(is(not(emptyString())));
    RestAssured
        .when().get(ALICE.name())
        .then()
            .statusCode(Response.Status.OK.getStatusCode())
            .contentType(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.CONTENT_LENGTH, is(notNullValue()))
            .body("", is(toMap(ALICE)));
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
            .header(HttpHeaders.CONTENT_LENGTH, is(notNullValue()))
            .body(is(not(emptyString())));
    // @formatter:on
  }
}

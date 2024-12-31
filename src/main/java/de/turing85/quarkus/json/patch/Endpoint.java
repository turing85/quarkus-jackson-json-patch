package de.turing85.quarkus.json.patch;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import io.smallrye.mutiny.Uni;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Path(Endpoint.PATH)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RequiredArgsConstructor
@Getter(AccessLevel.PRIVATE)
public final class Endpoint {
  static final String PATH = "users";

  // @formatter:off
  private static final List<User> USERS =
      new ArrayList<>(List.of(
          UserDto.builder()
              .name("alice")
              .email("alice@gmail.com")
              .build(),
          UserDto.builder()
              .name("bob")
              .email("bob@gmail.com")
              .build(),
          UserDto.builder()
              .name("claire")
              .email("claire@gmail.com")
              .build()));
  // @formatter:on
  private static final URI PATH_URI = URI.create(PATH);

  private final Patcher patcher;

  @GET
  public Uni<Response> getAllUsers() {
    return Uni.createFrom().item(allUsersToResponse());
  }

  @GET
  @Path("{name}")
  public Uni<Response> getUserByName(@PathParam("name") String name) {
    final User found = findByName(name);
    return Uni.createFrom().item(toResponse(found));
  }

  @PATCH
  @Path("{name}")
  @Consumes(MediaType.APPLICATION_JSON_PATCH_JSON)
  public Uni<Response> patchUserByName(@PathParam("name") String name, JsonPatch patch)
      throws JsonProcessingException, JsonPatchException {
    final User original = findByName(name);
    final User updated = getPatcher().patch(original, patch);
    USERS.remove(original);
    USERS.add(updated);
    return Uni.createFrom().item(toResponse(updated));
  }

  private static Response allUsersToResponse() {
    return Response.ok(USERS).location(PATH_URI).build();
  }

  private static User findByName(String name) {
    // @formatter:off
    return USERS.stream()
        .filter(user -> user.getName().equals(name))
        .findFirst()
        .orElseThrow(() -> new NoSuchElementException("User not found"));
    // @formatter:on
  }

  private static Response toResponse(User user) {
    // @formatter:off
    return Response
        .ok(user)
        .location(UriBuilder.fromUri(PATH_URI).path(user.getName()).build())
        .build();
    // @formatter:on
  }
}

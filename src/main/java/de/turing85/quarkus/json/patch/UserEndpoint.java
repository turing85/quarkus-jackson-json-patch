package de.turing85.quarkus.json.patch;

import java.net.URI;
import java.util.List;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import de.turing85.quarkus.json.patch.api.User;
import de.turing85.quarkus.json.patch.api.UserDao;
import de.turing85.quarkus.json.patch.openapi.JsonPatchOpenApiFilter;
import de.turing85.quarkus.json.patch.openapi.OpenApiDefinition;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.tuples.Tuple2;
import io.smallrye.mutiny.unchecked.Unchecked;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path(UserEndpoint.PATH)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Users")
@RequiredArgsConstructor
@Getter(AccessLevel.PRIVATE)
public final class UserEndpoint {
  static final String PATH = "users";
  private static final URI PATH_URI = URI.create(PATH);

  private final UserDao userDao;
  private final Patcher patcher;

  @GET
  @Parameter(ref = HttpHeaders.ACCEPT_ENCODING)
  @APIResponse(ref = OpenApiDefinition.RESPONSE_USERS, responseCode = "200")
  @APIResponse(ref = OpenApiDefinition.RESPONSE_INTERNAL_SERVER_ERROR)
  public Uni<Response> getAllUsers() {
    // @formatter:off
    return Uni.createFrom().item(userDao.findAll())
        .onItem().transform(UserEndpoint::toResponse);
    // @formatter:on
  }

  @GET
  @Path("{name}")
  @Parameter(ref = HttpHeaders.ACCEPT_ENCODING)
  @APIResponse(ref = OpenApiDefinition.RESPONSE_USER, responseCode = "200")
  @APIResponse(ref = OpenApiDefinition.RESPONSE_NOT_FOUND)
  @APIResponse(ref = OpenApiDefinition.RESPONSE_INTERNAL_SERVER_ERROR)
  public Uni<Response> getUserByName(
      @Parameter(ref = OpenApiDefinition.PARAM_PATH_NAME) @PathParam("name") String name) {
    // @formatter:off
    return Uni.createFrom().item(name)
        .onItem().transform(userDao::findByName)
        .onItem().transform(UserEndpoint::toResponse);
    // @formatter:on
  }

  @PATCH
  @Path("{name}")
  @Consumes(MediaType.APPLICATION_JSON_PATCH_JSON)
  @Parameter(ref = HttpHeaders.ACCEPT_ENCODING)
  @APIResponse(ref = OpenApiDefinition.RESPONSE_USER, responseCode = "200")
  @APIResponse(ref = OpenApiDefinition.RESPONSE_BAD_REQUEST)
  @APIResponse(ref = OpenApiDefinition.RESPONSE_NOT_FOUND)
  @APIResponse(ref = OpenApiDefinition.RESPONSE_INTERNAL_SERVER_ERROR)
  public Uni<Response> patchUserByName(
      @Parameter(ref = OpenApiDefinition.PARAM_PATH_NAME) @PathParam("name") String name,
      @RequestBody(ref = JsonPatchOpenApiFilter.REQUEST_BODY_JSON_PATCH) JsonNode patch) {
    // @formatter:off
    return Uni.createFrom().item(name)
        .onItem().transform(userDao::findByName)
        .onItem().transform(Unchecked.function(user -> Tuple2.of(user, patcher.patch(user, patch))))
        .onItem().invoke(tuple -> userDao.delete(tuple.getItem1()))
        .onItem().invoke(tuple -> userDao.add(tuple.getItem2()))
        .onItem().transform(Tuple2::getItem2)
        .onItem().transform(UserEndpoint::toResponse);
    // @formatter:on
  }

  private static Response toResponse(List<User> users) {
    // @formatter:off
    return Response
        .ok(users)
        .location(PATH_URI)
        .build();
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

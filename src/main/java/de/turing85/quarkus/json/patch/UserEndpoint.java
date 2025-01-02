package de.turing85.quarkus.json.patch;

import java.net.URI;
import java.util.List;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import de.turing85.quarkus.json.patch.api.request.CreateUserRequest;
import de.turing85.quarkus.json.patch.api.response.User;
import de.turing85.quarkus.json.patch.openapi.JsonPatchOpenApiFilter;
import de.turing85.quarkus.json.patch.openapi.OpenApiDefinition;
import de.turing85.quarkus.json.patch.spi.UserDao;
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
    return Uni.createFrom().item(userDao::findAll)
        .onItem().transform(UserEndpoint::toOkResponse);
    // @formatter:on
  }

  @POST
  @Parameter(ref = HttpHeaders.ACCEPT_ENCODING)
  @APIResponse(ref = OpenApiDefinition.RESPONSE_USER, responseCode = "204")
  @APIResponse(ref = OpenApiDefinition.RESPONSE_INTERNAL_SERVER_ERROR)
  public Uni<Response> createUser(
      @RequestBody(ref = OpenApiDefinition.REQUEST_CREATE_USER) CreateUserRequest request) {
    // @formatter:off
    return Uni.createFrom().item(request)
        .onItem().transform(userDao::create)
        .onItem().transform(UserEndpoint::toCreatedResponse);
    // @formatter:on
  }

  @DELETE
  @APIResponse(ref = OpenApiDefinition.RESPONSE_USERS)
  @APIResponse(ref = OpenApiDefinition.RESPONSE_INTERNAL_SERVER_ERROR)
  public Uni<Response> deleteAllUsers() {
    // @formatter:off
    return Uni.createFrom().item(userDao::findAll)
        .onItem().invoke(ignored -> userDao.deleteAll())
        .onItem().transform(UserEndpoint::toOkResponse);
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
        .onItem().transform(UserEndpoint::toOkResponse);
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
        .onItem().transform(Tuple2::getItem2)
        .onItem().transform(CreateUserRequest::from)
        .onItem().transform(userDao::create)
        .onItem().transform(UserEndpoint::toOkResponse);
    // @formatter:on
  }

  @DELETE
  @Path("{name}")
  @Parameter(ref = HttpHeaders.ACCEPT_ENCODING)
  @APIResponse(ref = OpenApiDefinition.RESPONSE_USER, responseCode = "200")
  @APIResponse(ref = OpenApiDefinition.RESPONSE_NOT_FOUND)
  @APIResponse(ref = OpenApiDefinition.RESPONSE_INTERNAL_SERVER_ERROR)
  public Uni<Response> deleteUserByName(
      @Parameter(ref = OpenApiDefinition.PARAM_PATH_NAME) @PathParam("name") String name) {
    // @formatter:off
    return Uni.createFrom().item(name)
        .onItem().transform(userDao::findByName)
        .onItem().invoke(user -> userDao.deleteByName(user.getName()))
        .onItem().transform(UserEndpoint::toOkResponse);
    // @formatter:on
  }

  private static Response toOkResponse(List<User> users) {
    // @formatter:off
    return Response
        .ok(users)
        .location(PATH_URI)
        .build();
    // @formatter:on
  }

  private static Response toOkResponse(User user) {
    return toResponse(user, Response.Status.OK.getStatusCode());
  }

  private static Response toCreatedResponse(User user) {
    return toResponse(user, Response.Status.CREATED.getStatusCode());
  }

  private static Response toResponse(User user, int status) {
    // @formatter:off
    return Response
            .status(status)
            .entity(user)
            .location(UriBuilder.fromUri(PATH_URI).path(user.getName()).build())
            .build();
    // @formatter:on
  }
}

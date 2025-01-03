package de.turing85.quarkus.json.patch;

import java.net.URI;
import java.util.List;

import jakarta.validation.Valid;
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
import de.turing85.quarkus.json.patch.api.response.UserResponse;
import de.turing85.quarkus.json.patch.openapi.JsonPatchOpenApiFilter;
import de.turing85.quarkus.json.patch.openapi.OpenApiDefinition;
import de.turing85.quarkus.json.patch.spi.User;
import de.turing85.quarkus.json.patch.spi.UserDao;
import de.turing85.quarkus.json.patch.validation.IsJsonPatch;
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
  @APIResponse(ref = OpenApiDefinition.RESPONSE_USERS_OK)
  @APIResponse(ref = OpenApiDefinition.RESPONSE_INTERNAL_SERVER_ERROR)
  public Uni<Response> getAllUsers() {
    // @formatter:off
    return Uni
        .createFrom().item(getUserDao()::findAll)
        .map(UserEndpoint::toOkResponse);
    // @formatter:on
  }

  @POST
  @Parameter(ref = HttpHeaders.ACCEPT_ENCODING)
  @APIResponse(ref = OpenApiDefinition.RESPONSE_USER_CREATED)
  @APIResponse(ref = OpenApiDefinition.RESPONSE_INTERNAL_SERVER_ERROR)
  public Uni<Response> createUser(
      @RequestBody(ref = OpenApiDefinition.REQUEST_USER_CREATE) CreateUserRequest request) {
    // @formatter:off
    return Uni
        .createFrom().item(request)
        .map(getUserDao()::create)
        .map(UserEndpoint::toCreatedResponse);
    // @formatter:on
  }

  @DELETE
  @APIResponse(ref = OpenApiDefinition.RESPONSE_USERS_OK)
  @APIResponse(ref = OpenApiDefinition.RESPONSE_INTERNAL_SERVER_ERROR)
  public Uni<Response> deleteAllUsers() {
    // @formatter:off
    return Uni
        .createFrom().item(getUserDao()::findAll)
        .invoke(ignored -> getUserDao().deleteAll())
        .map(UserEndpoint::toOkResponse);
    // @formatter:on
  }

  @GET
  @Path("{name}")
  @Parameter(ref = HttpHeaders.ACCEPT_ENCODING)
  @APIResponse(ref = OpenApiDefinition.RESPONSE_USER_OK)
  @APIResponse(ref = OpenApiDefinition.RESPONSE_NOT_FOUND)
  @APIResponse(ref = OpenApiDefinition.RESPONSE_INTERNAL_SERVER_ERROR)
  public Uni<Response> getUserByName(
      @Parameter(ref = OpenApiDefinition.PARAM_PATH_NAME) @PathParam("name") String name) {
    // @formatter:off
    return Uni
        .createFrom().item(() -> getUserDao().findByName(name))
        .map(UserEndpoint::toOkResponse);
    // @formatter:on
  }

  @PATCH
  @Path("{name}")
  @Consumes(MediaType.APPLICATION_JSON_PATCH_JSON)
  @Parameter(ref = HttpHeaders.ACCEPT_ENCODING)
  @APIResponse(ref = OpenApiDefinition.RESPONSE_USER_OK)
  @APIResponse(ref = OpenApiDefinition.RESPONSE_BAD_REQUEST)
  @APIResponse(ref = OpenApiDefinition.RESPONSE_NOT_FOUND)
  @APIResponse(ref = OpenApiDefinition.RESPONSE_INTERNAL_SERVER_ERROR)
  public Uni<Response> patchUserByName(
      @Parameter(ref = OpenApiDefinition.PARAM_PATH_NAME) @PathParam("name") String name,
      @RequestBody(ref = JsonPatchOpenApiFilter.REQUEST_BODY_JSON_PATCH) @Valid
      @IsJsonPatch JsonNode patch) {
    // @formatter:off
    return Uni
        .createFrom().item(() -> getUserDao().findByName(name))
        .map(Unchecked.function(user ->
            Tuple2.of(user, getPatcher().patch(CreateUserRequest.from(user), patch))))
        .map(tuple -> getUserDao().update(tuple.getItem1().name(), tuple.getItem2()))
        .map(UserEndpoint::toOkResponse);
    // @formatter:on
  }

  @DELETE
  @Path("{name}")
  @Parameter(ref = HttpHeaders.ACCEPT_ENCODING)
  @APIResponse(ref = OpenApiDefinition.RESPONSE_USER_OK)
  @APIResponse(ref = OpenApiDefinition.RESPONSE_NOT_FOUND)
  @APIResponse(ref = OpenApiDefinition.RESPONSE_INTERNAL_SERVER_ERROR)
  public Uni<Response> deleteUserByName(
      @Parameter(ref = OpenApiDefinition.PARAM_PATH_NAME) @PathParam("name") String name) {
    // @formatter:off
    return Uni
        .createFrom().item(() -> getUserDao().findByName(name))
        .invoke(user -> getUserDao().deleteByName(user.name()))
        .map(UserEndpoint::toOkResponse);
    // @formatter:on
  }

  private static Response toOkResponse(List<User> users) {
    // @formatter:off
    return Response
        .status(Response.Status.OK.getStatusCode())
        .entity(users.stream().map(UserResponse::from).toList())
        .build();
    // @formatter:on
  }

  private static Response toOkResponse(User user) {
    // @formatter:off
    return Response
            .status(Response.Status.OK.getStatusCode())
            .entity(UserResponse.from(user))
            .build();
    // @formatter:on
  }

  private static Response toCreatedResponse(User user) {
    return Response.created(UriBuilder.fromUri(PATH_URI).path(user.name()).build())
        .entity(UserResponse.from(user)).build();
  }

}

package de.turing85.quarkus.json.patch.openapi;

import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;

import de.turing85.quarkus.json.patch.api.request.CreateUserRequest;
import de.turing85.quarkus.json.patch.api.response.UserResponse;
import de.turing85.quarkus.json.patch.exception.mapper.ErrorResponse;
import org.eclipse.microprofile.openapi.annotations.Components;
import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.enums.ParameterIn;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.headers.Header;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

// @formatter:off
@OpenAPIDefinition(
    info = @Info(
        title = "title-dummy",
        version = "version-dummy"),
    components = @Components(
        headers = {
            @Header(
                name = HttpHeaders.CONTENT_ENCODING,
                description = "The Content-Encoding",
                schema = @Schema(type = SchemaType.STRING)),
            @Header(
                name = HttpHeaders.CONTENT_LENGTH,
                description = "The length of the content",
                required = true,
                schema = @Schema(type = SchemaType.NUMBER)),
            @Header(
                name = HttpHeaders.CONTENT_TYPE,
                description = "The Content-Type",
                required = true,
                schema = @Schema(type = SchemaType.STRING)),
            @Header(
                name = HttpHeaders.LOCATION,
                description = "The Location of the entity",
                required = true,
                schema = @Schema(type = SchemaType.STRING)),
        },
        parameters = {
            @Parameter(
                name = HttpHeaders.ACCEPT_ENCODING,
                description = "The accepted Content-Encoding",
                in = ParameterIn.HEADER,
                schema = @Schema(
                    enumeration = {
                        "gzip",
                        "deflate",
                    })),
            @Parameter(
                name = OpenApiDefinition.PARAM_PATH_NAME,
                description = "The name of the entity",
                in = ParameterIn.PATH,
                required = true,
                schema = @Schema(type = SchemaType.STRING)),
        },
        requestBodies = {
          @RequestBody(
              name = OpenApiDefinition.REQUEST_USER_CREATE,
              content = @Content(
                  mediaType = MediaType.APPLICATION_JSON,
                  schema = @Schema(ref = CreateUserRequest.SCHEMA_NAME))),
        },
        responses = {
            @APIResponse(
                name = OpenApiDefinition.RESPONSE_USER_OK,
                responseCode = "200",
                description = "The user",
                headers = {
                    @Header(ref = HttpHeaders.CONTENT_ENCODING),
                    @Header(ref = HttpHeaders.CONTENT_LENGTH),
                    @Header(ref = HttpHeaders.CONTENT_TYPE),
                },
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(ref = UserResponse.SCHEMA_NAME))),
            @APIResponse(
                name = OpenApiDefinition.RESPONSE_USER_CREATED,
                responseCode = "204",
                description = "The user",
                headers = {
                    @Header(ref = HttpHeaders.CONTENT_ENCODING),
                    @Header(ref = HttpHeaders.CONTENT_LENGTH),
                    @Header(ref = HttpHeaders.CONTENT_TYPE),
                    @Header(ref = HttpHeaders.LOCATION),
                },
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(ref = UserResponse.SCHEMA_NAME))),
            @APIResponse(
                name = OpenApiDefinition.RESPONSE_USERS_OK,
                description = "The users",
                responseCode = "200",
                headers = {
                    @Header(ref = HttpHeaders.CONTENT_ENCODING),
                    @Header(ref = HttpHeaders.CONTENT_TYPE),
                    @Header(ref = HttpHeaders.CONTENT_LENGTH),
                },
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(ref = UserResponse.SCHEMA_NAME_LIST))),
            @APIResponse(
                name = OpenApiDefinition.RESPONSE_NO_CONTENT,
                description = "No content",
                responseCode = "204"),
            @APIResponse(
                name = OpenApiDefinition.RESPONSE_BAD_REQUEST,
                description = "The request could not be processed",
                responseCode = "400",
                headers = {
                    @Header(ref = HttpHeaders.CONTENT_ENCODING),
                    @Header(ref = HttpHeaders.CONTENT_TYPE),
                    @Header(ref = HttpHeaders.CONTENT_LENGTH),
                },
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(ref = ErrorResponse.SCHEMA_NAME),
                    example = """
                        {
                          "message": "Bad request"
                        }""")),
            @APIResponse(
                name = OpenApiDefinition.RESPONSE_NOT_FOUND,
                description = "Entity not found",
                responseCode = "404",
                headers = {
                    @Header(ref = HttpHeaders.CONTENT_ENCODING),
                    @Header(ref = HttpHeaders.CONTENT_TYPE),
                    @Header(ref = HttpHeaders.CONTENT_LENGTH),
                },
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(ref = ErrorResponse.SCHEMA_NAME),
                    example = """
                        {
                          "message": "Entity not found"
                        }""")),
            @APIResponse(
                name = OpenApiDefinition.RESPONSE_INTERNAL_SERVER_ERROR,
                description = "An internal server error occurred",
                responseCode = "500",
                headers = {
                    @Header(ref = HttpHeaders.CONTENT_ENCODING),
                    @Header(ref = HttpHeaders.CONTENT_TYPE),
                    @Header(ref = HttpHeaders.CONTENT_LENGTH),
                },
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(ref = ErrorResponse.SCHEMA_NAME),
                    example = """
                        {
                          "message": "Internal Server Error"
                        }"""))}))
// @formatter:on
public final class OpenApiDefinition extends Application {
  public static final String PARAM_PATH_NAME = "name";
  public static final String REQUEST_USER_CREATE = "Create User";
  public static final String RESPONSE_NO_CONTENT = "NoContent";
  public static final String RESPONSE_BAD_REQUEST = "BadRequest";
  public static final String RESPONSE_INTERNAL_SERVER_ERROR = "InternalServerError";
  public static final String RESPONSE_NOT_FOUND = "NotFound";
  public static final String RESPONSE_USER_CREATED = "UserCreated";
  public static final String RESPONSE_USER_OK = "UserOk";
  public static final String RESPONSE_USERS_OK = "UsersOk";
}

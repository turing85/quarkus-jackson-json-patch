package de.turing85.quarkus.json.patch.openapi;

import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;

import de.turing85.quarkus.json.patch.User;
import de.turing85.quarkus.json.patch.exception.mapper.Error;
import org.eclipse.microprofile.openapi.annotations.Components;
import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.headers.Header;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
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
        schemas = {
            @Schema(
                name = OpenApiDefinition.SCHEMA_USER,
                implementation = User.class),
            @Schema(
                name = OpenApiDefinition.SCHEMA_USERS,
                ref = OpenApiDefinition.SCHEMA_USER,
                type = SchemaType.ARRAY),
            @Schema(
                name = OpenApiDefinition.SCHEMA_ERROR,
                implementation = Error.class),
        },
        responses = {
            @APIResponse(
                name = OpenApiDefinition.RESPONSE_USER,
                description = "The user",
                headers = {
                    @Header(ref = HttpHeaders.CONTENT_ENCODING),
                    @Header(ref = HttpHeaders.CONTENT_LENGTH),
                    @Header(ref = HttpHeaders.CONTENT_TYPE),
                    @Header(ref = HttpHeaders.LOCATION),
                },
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(ref = OpenApiDefinition.SCHEMA_USER),
                    example = """
                        {
                          "name": "alice",
                          "email": "alice@email.com"
                        }""")),
            @APIResponse(
                name = OpenApiDefinition.RESPONSE_USERS,
                description = "The users",
                headers = {
                    @Header(ref = HttpHeaders.CONTENT_ENCODING),
                    @Header(ref = HttpHeaders.CONTENT_TYPE),
                    @Header(ref = HttpHeaders.CONTENT_LENGTH),
                    @Header(ref = HttpHeaders.LOCATION),
                },
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(ref = OpenApiDefinition.SCHEMA_USERS),
                    example = """
                        [
                          {
                            "name": "alice",
                            "email": "alice@email.com"
                          }
                        ]""")),
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
                    schema = @Schema(ref = OpenApiDefinition.SCHEMA_ERROR),
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
                    schema = @Schema(ref = OpenApiDefinition.SCHEMA_ERROR),
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
                    schema = @Schema(ref = OpenApiDefinition.SCHEMA_ERROR),
                    example = """
                        {
                          "message": "Internal Server Error"
                        }"""))}))
// @formatter:on
public class OpenApiDefinition extends Application {
  public static final String RESPONSE_BAD_REQUEST = "BadRequest";
  public static final String RESPONSE_NOT_FOUND = "NotFound";
  public static final String RESPONSE_INTERNAL_SERVER_ERROR = "InternalServerError";
  public static final String RESPONSE_USER = "User";
  public static final String RESPONSE_USERS = "Users";
  public static final String SCHEMA_ERROR = "Error";
  public static final String SCHEMA_USER = "User";
  public static final String SCHEMA_USERS = "Users";
}

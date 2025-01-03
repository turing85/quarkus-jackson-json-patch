package de.turing85.quarkus.json.patch.openapi;

import java.util.List;

import jakarta.ws.rs.core.MediaType;

import io.quarkus.smallrye.openapi.OpenApiFilter;
import io.smallrye.openapi.internal.models.Components;
import io.smallrye.openapi.internal.models.examples.Example;
import io.smallrye.openapi.internal.models.media.Content;
import io.smallrye.openapi.internal.models.media.Schema;
import io.smallrye.openapi.internal.models.parameters.RequestBody;
import org.eclipse.microprofile.openapi.OASFilter;
import org.eclipse.microprofile.openapi.models.OpenAPI;

@OpenApiFilter(OpenApiFilter.RunStage.BUILD)
public final class JsonPatchOpenApiFilter implements OASFilter {
  public static final String REQUEST_BODY_JSON_PATCH = "JsonPatch";
  public static final String SCHEMA_JSON_PATCH = "Json Patch";

  @Override
  public void filterOpenAPI(final OpenAPI openApi) {
    if (openApi.getComponents() == null) {
      openApi.setComponents(new Components());
    }
    final Schema jsonPatchSchemaRef = registerJsonPatchSchema(openApi);
    registerJsonPatchRequestBody(openApi, jsonPatchSchemaRef);
  }

  private static Schema registerJsonPatchSchema(final OpenAPI openAPI) {
    final Schema jsonPatchSchema = new Schema();
    jsonPatchSchema
        .setType(List.of(org.eclipse.microprofile.openapi.models.media.Schema.SchemaType.ARRAY));
    jsonPatchSchema.setName(SCHEMA_JSON_PATCH);
    openAPI.getComponents().addSchema(SCHEMA_JSON_PATCH, jsonPatchSchema);
    jsonPatchSchema.setRef("https://json.schemastore.org/json-patch");
    openAPI.getComponents().addSchema(SCHEMA_JSON_PATCH, jsonPatchSchema);

    final Schema jsonPatchSchemaRef = new Schema();
    jsonPatchSchemaRef.setRef(SCHEMA_JSON_PATCH);
    return jsonPatchSchemaRef;
  }

  private static void registerJsonPatchRequestBody(final OpenAPI openAPI,
      final Schema jsonPatchSchemaRef) {
    final RequestBody jsonPatchRequestBody = new RequestBody();
    jsonPatchRequestBody.setName(REQUEST_BODY_JSON_PATCH);
    jsonPatchRequestBody.setContent(constructJsonPatchContent(jsonPatchSchemaRef));
    openAPI.getComponents().addRequestBody(REQUEST_BODY_JSON_PATCH, jsonPatchRequestBody);
  }

  private static Content constructJsonPatchContent(final Schema jsonPatchSchemaRef) {
    final Content jsonPatchContent = new Content();
    jsonPatchContent.addMediaType(MediaType.APPLICATION_JSON_PATCH_JSON,
        constructJsonPatchMediaType(jsonPatchSchemaRef));
    return jsonPatchContent;
  }

  private static io.smallrye.openapi.internal.models.media.MediaType constructJsonPatchMediaType(
      final Schema jsonPatchSchemaRef) {
    final io.smallrye.openapi.internal.models.media.MediaType jsonPatchMediaType =
        new io.smallrye.openapi.internal.models.media.MediaType();
    jsonPatchMediaType.setSchema(jsonPatchSchemaRef);
    // @formatter:off
    jsonPatchMediaType
        .addExample(
            "replace",
            constructExample("""
                [
                  {
                    "op": "replace",
                    "path": "/email",
                    "value": "new@email.com"
                  }
                ]"""))
        .addExample(
            "remove",
            constructExample("""
                [
                  {
                    "op": "remove",
                    "path": "/email"
                  }
                ]"""))
        .addExample(
            "copy",
            constructExample("""
                [
                  {
                    "op": "copy",
                    "from": "/email",
                    "path": "/name"
                  }
                ]"""))
        .addExample(
            "move",
            constructExample("""
                [
                  {
                    "op": "move",
                    "from": "/email",
                    "path": "/name"
                  }
                ]"""))
        .addExample(
            "test",
            constructExample("""
                [
                  {
                    "op": "test",
                    "path": "/name",
                    "value": "alice"
                  }
                ]"""))
        .addExample(
            "test and copy",
            constructExample("""
                [
                  {
                    "op": "test",
                    "path": "/name",
                    "value": "alice"
                  },
                  {
                    "op": "copy",
                    "from": "/email",
                    "path": "/name"
                  }
                ]"""));
    // @formatter:on
    return jsonPatchMediaType;
  }

  private static Example constructExample(final String value) {
    final Example example = new Example();
    example.setValue(value);
    return example;
  }
}

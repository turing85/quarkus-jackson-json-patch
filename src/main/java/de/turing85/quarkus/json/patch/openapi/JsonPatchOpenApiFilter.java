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
public class JsonPatchOpenApiFilter implements OASFilter {
  public static final String REQUEST_BODY_JSON_PATCH = "JsonPatch";
  public static final String SCHEMA_JSON_PATCH = "Json Patch";

  @Override
  public void filterOpenAPI(OpenAPI openApi) {
    if (openApi.getComponents() == null) {
      openApi.setComponents(new Components());
    }
    Schema jsonPatchSchemaRef = registerJsonPatchSchema(openApi);
    registerJsonPatchRequestBody(openApi, jsonPatchSchemaRef);
  }

  private static Schema registerJsonPatchSchema(OpenAPI openAPI) {
    Schema jsonPatchSchema = new Schema();
    jsonPatchSchema
        .setType(List.of(org.eclipse.microprofile.openapi.models.media.Schema.SchemaType.ARRAY));
    jsonPatchSchema.setName(SCHEMA_JSON_PATCH);
    openAPI.getComponents().addSchema(SCHEMA_JSON_PATCH, jsonPatchSchema);
    jsonPatchSchema.setRef("https://json.schemastore.org/json-patch");
    openAPI.getComponents().addSchema(SCHEMA_JSON_PATCH, jsonPatchSchema);

    Schema jsonPatchSchemaRef = new Schema();
    jsonPatchSchemaRef.setRef(SCHEMA_JSON_PATCH);
    return jsonPatchSchemaRef;
  }

  private static void registerJsonPatchRequestBody(OpenAPI openAPI, Schema jsonPatchSchemaRef) {
    RequestBody jsonPatchRequestBody = new RequestBody();
    jsonPatchRequestBody.setName(REQUEST_BODY_JSON_PATCH);
    jsonPatchRequestBody.setContent(constructJsonPatchContent(jsonPatchSchemaRef));
    openAPI.getComponents().addRequestBody(REQUEST_BODY_JSON_PATCH, jsonPatchRequestBody);
  }

  private static Content constructJsonPatchContent(Schema jsonPatchSchemaRef) {
    Content jsonPatchContent = new Content();
    jsonPatchContent.addMediaType(MediaType.APPLICATION_JSON_PATCH_JSON,
        constructJsonPatchMediaType(jsonPatchSchemaRef));
    return jsonPatchContent;
  }

  private static io.smallrye.openapi.internal.models.media.MediaType constructJsonPatchMediaType(
      Schema jsonPatchSchemaRef) {
    io.smallrye.openapi.internal.models.media.MediaType jsonPatchMediaType =
        new io.smallrye.openapi.internal.models.media.MediaType();
    jsonPatchMediaType.setSchema(jsonPatchSchemaRef);
    // @formatter:off
    jsonPatchMediaType
        .addExample(
            "replace",
            constructEXample("""
                [
                  {
                    "op": "replace",
                    "path": "/email",
                    "value": "new@email.com"
                  }
                ]"""))
        .addExample(
            "remove",
            constructEXample("""
                [
                  {
                    "op": "remove",
                    "path": "/email"
                  }
                ]"""))
        .addExample(
            "copy",
            constructEXample("""
                [
                  {
                    "op": "copy",
                    "from": "/email",
                    "path": "/name"
                  }
                ]"""))
        .addExample(
            "move",
            constructEXample("""
                [
                  {
                    "op": "move",
                    "from": "/email",
                    "path": "/name"
                  }
                ]"""))
        .addExample(
            "test",
            constructEXample("""
                [
                  {
                    "op": "test",
                    "path": "/name",
                    "value": "alice"
                  }
                ]"""))
        .addExample(
            "test and copy",
            constructEXample("""
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

  private static Example constructEXample(String value) {
    Example example = new Example();
    example.setValue(value);
    return example;
  }
}

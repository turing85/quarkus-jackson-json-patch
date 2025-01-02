package de.turing85.quarkus.json.patch;

import jakarta.enterprise.context.ApplicationScoped;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flipkart.zjsonpatch.JsonPatch;
import io.quarkus.logging.Log;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Getter(AccessLevel.PRIVATE)
public class Patcher {
  private final ObjectMapper objectMapper;

  public <T> T patch(T t, JsonNode patch) throws JsonProcessingException {
    final JsonNode tAsNode = getObjectMapper().convertValue(t, JsonNode.class);
    Log.debugf("Original (type: %s): %s", t.getClass().getCanonicalName(), tAsNode);
    if (Log.isDebugEnabled()) {
      Log.debugf("Patch: %s", getObjectMapper().writeValueAsString(patch));
    }
    final JsonNode patchAsNode = JsonPatch.apply(patch, tAsNode);
    @SuppressWarnings("unchecked")
    final T patched = (T) getObjectMapper().treeToValue(patchAsNode, t.getClass());
    Log.debugf("Patched (type: %s): %s", patched.getClass().getCanonicalName(), patchAsNode);
    return patched;
  }
}

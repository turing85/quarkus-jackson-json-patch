package de.turing85.quarkus.json.patch.config;

import io.quarkus.runtime.annotations.RegisterForReflection;

// @formatter:off
@RegisterForReflection(
    targets = {
        com.github.fge.jackson.NodeType.class,
        com.github.fge.jackson.jsonpointer.JsonPointer.class,
        com.github.fge.jackson.jsonpointer.ReferenceToken.class,
        com.github.fge.jackson.jsonpointer.TreePointer.class,

        com.github.fge.jsonpatch.AddOperation.class,
        com.github.fge.jsonpatch.CopyOperation.class,
        com.github.fge.jsonpatch.DualPathOperation.class,
        com.github.fge.jsonpatch.Iterables.class,
        com.github.fge.jsonpatch.JsonPatch.class,
        com.github.fge.jsonpatch.JsonPatchOperation.class,
        com.github.fge.jsonpatch.MoveOperation.class,
        com.github.fge.jsonpatch.Patch.class,
        com.github.fge.jsonpatch.PathValueOperation.class,
        com.github.fge.jsonpatch.RemoveOperation.class,
        com.github.fge.jsonpatch.ReplaceOperation.class,
        com.github.fge.jsonpatch.TestOperation.class,
        com.github.fge.jsonpatch.diff.JsonDiff.class,
        com.github.fge.jsonpatch.mergepatch.JsonMergePatch.class,
    },
    classNames = {
        "com.github.fge.jsonpatch.diff.DiffOperation",
        "com.github.fge.jsonpatch.mergepatch.NonObjectMergePatch",
        "com.github.fge.jsonpatch.mergepatch.ObjectMergePatch",
    })
// @formatter:on
@SuppressWarnings("unused")
final class ReflectionConfig {
  private ReflectionConfig() {
    throw new UnsupportedOperationException("This utility class cannot be instantiated");
  }
}

package de.turing85.quarkus.json.patch.config;

import com.flipkart.zjsonpatch.mapping.jackson2.Jackson2ArrayNodeWrapper;
import com.flipkart.zjsonpatch.mapping.jackson2.Jackson2NodeFactory;
import com.flipkart.zjsonpatch.mapping.jackson2.Jackson2NodeWrapper;
import com.flipkart.zjsonpatch.mapping.jackson2.Jackson2ObjectNodeWrapper;
import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.experimental.UtilityClass;

// @formatter:off
@RegisterForReflection(targets = {
    Jackson2ArrayNodeWrapper.class,
    Jackson2NodeFactory.class,
    Jackson2NodeWrapper.class,
    Jackson2ObjectNodeWrapper.class
})
// @formatter:on
@UtilityClass
@SuppressWarnings("unused")
public class ReflectionConfig {
}

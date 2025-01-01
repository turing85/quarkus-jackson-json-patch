package de.turing85.quarkus.json.patch;

import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusIntegrationTest;

@QuarkusIntegrationTest
@TestHTTPEndpoint(UserEndpoint.class)
class UserEndpointNativeIT extends UserEndpointTest {
}

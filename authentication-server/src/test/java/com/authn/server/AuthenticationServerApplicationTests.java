package com.authn.server;

import static com.authn.server.constants.AuthenticationServerConstants.API_BASE_PATH;
import static com.authn.server.constants.AuthenticationServerConstants.CLIENT_REGISTRATION;
import static com.authn.server.constants.AuthenticationServerConstants.COLON;
import static com.authn.server.constants.AuthenticationServerConstants.TOKEN;
import static com.authn.server.constants.AuthenticationServerConstants.VALIDATE_TOKEN;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.authn.server.controller.ClientRequest;
import com.authn.server.controller.JwtTokenRequest;
import com.authn.server.controller.JwtTokenResponse;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import java.util.Base64;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class AuthenticationServerApplicationTests {

  @LocalServerPort
  private int localPort;

  private ClientRequest clientRequest;

  @BeforeEach
  void setup() {
    RestAssured.requestSpecification = new RequestSpecBuilder()
        .setBaseUri("http://localhost")
        .setPort(localPort)
        .setContentType(ContentType.JSON)
        .build();
  }

  @Test
  void testAuthenticationServer() {
    Response clientResponse = createClient();
    assertThat(clientResponse.getStatusCode()).isEqualTo(200);
    String clientIdSecret = new StringBuilder().append(clientRequest.getClientId())
        .append(COLON).append(clientRequest.getClientSecret()).toString();
    Response tokenResponse = generateToken(
        new String(Base64.getEncoder().encode(clientIdSecret.getBytes())));
    assertThat(tokenResponse.getStatusCode()).isEqualTo(200);
    JwtTokenResponse jwtTokenResponse = tokenResponse.getBody().as(JwtTokenResponse.class);
    Response tokenValid = verifyToken(
        JwtTokenRequest.builder().token(jwtTokenResponse.getAccess_token()).build());
    assertThat(tokenValid.getStatusCode()).isEqualTo(200);
    assertTrue(tokenValid.getBody().as(Boolean.class));
  }

  private Response createClient() {
    clientRequest = TestDataUtil.getClientRequest();
    return given().body(clientRequest).when().post(API_BASE_PATH + CLIENT_REGISTRATION);
  }

  private Response generateToken(String token) {
    return given().header("Authorization", "Basic " + token).when().post(API_BASE_PATH + TOKEN);
  }

  private Response verifyToken(JwtTokenRequest jwtTokenRequest) {
    return given().body(jwtTokenRequest).when().post(API_BASE_PATH + VALIDATE_TOKEN);
  }

}

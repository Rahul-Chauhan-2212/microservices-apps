package com.authn.server.constants;

import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

public class AuthenticationServerConstants {

  private AuthenticationServerConstants() {
  }

  public static final String API_BASE_PATH = "/api/v1/oauth";

  public static final String TOKEN = "/token";

  public static final String VALIDATE_TOKEN = TOKEN + "/validate";

  public static final String CLIENT_REGISTRATION = "/client/registration";

  public static final String ALGORITHM = "alg";

  public static final String TYPE = "type";

  public static final String JWT = "JWT";

  public static final String AUTHORIZATION_HEADER = "Authorization";

  public static final String BASIC_AUTH = "Basic ";

  public static final String BEARER = "Bearer";

  public static final String SCOPES = "scopes";

  public static final String EXTRA_CLAIMS = "extra_claims";

  public static final String AUTHENTICATION_SERVER = ServletUriComponentsBuilder.fromCurrentContextPath()
      .toUriString();

  public static final String COLON = ":";

  public static final String CLIENT_SECRET_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,15}$";

  public static final String FULL_NAME_REGEX = "^[a-zA-Z ]*$";
}

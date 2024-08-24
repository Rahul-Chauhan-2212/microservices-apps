package com.authn.server.controller;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author rahulchauhan
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JwtTokenResponse {

  private String access_token;

  private Long expires_in;

  private String scopes;

  private String token_type;

}

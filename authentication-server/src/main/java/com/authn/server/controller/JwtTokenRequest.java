package com.authn.server.controller;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author rahulchauhan
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class JwtTokenRequest {

  @NotEmpty(message = "JWT Token can't be empty")
  private String token;

}
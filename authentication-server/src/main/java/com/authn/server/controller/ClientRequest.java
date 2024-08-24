package com.authn.server.controller;

import static com.authn.server.constants.AuthenticationServerConstants.CLIENT_SECRET_REGEX;
import static com.authn.server.constants.AuthenticationServerConstants.FULL_NAME_REGEX;

import com.authn.server.entity.Scope;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.util.Set;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author rahulchauhan
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ClientRequest {

  @NotNull(message = "Client Id must be a valid UUID")
  private UUID clientId;

  @NotEmpty(message = "Client Secret should be present")
  @Pattern(regexp = CLIENT_SECRET_REGEX, message = "Client secret must contain minimum eight and maximum 15 characters, at least one uppercase letter, one lowercase letter, one number and one special character")
  private String clientSecret;

  @NotEmpty(message = "Full Name should be present")
  @Pattern(regexp = FULL_NAME_REGEX, message = "Full name should contain only alphabets")
  private String fullName;

  @NotEmpty(message = "Email Id should be present")
  @Email
  private String email;

  private Set<Scope> scopes;

}

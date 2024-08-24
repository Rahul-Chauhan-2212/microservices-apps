package com.authn.server.controller;

import static com.authn.server.constants.AuthenticationServerConstants.API_BASE_PATH;
import static com.authn.server.constants.AuthenticationServerConstants.AUTHORIZATION_HEADER;
import static com.authn.server.constants.AuthenticationServerConstants.BASIC_AUTH;
import static com.authn.server.constants.AuthenticationServerConstants.CLIENT_REGISTRATION;
import static com.authn.server.constants.AuthenticationServerConstants.COLON;
import static com.authn.server.constants.AuthenticationServerConstants.TOKEN;
import static com.authn.server.constants.AuthenticationServerConstants.VALIDATE_TOKEN;

import com.authn.server.exception.GenericException;
import com.authn.server.service.ClientService;
import com.authn.server.util.JwtUtilService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.Base64;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author rahulchauhan
 */
@RestController
@RequestMapping(API_BASE_PATH)
@Slf4j
@Tag(name = "Authentication Server APIs")
public class AuthController {

  private final AuthenticationManager authenticationManager;

  private final ClientService clientService;

  private final JwtUtilService jwtUtilService;

  private final PasswordEncoder passwordEncoder;

  public AuthController(AuthenticationManager authenticationManager, ClientService clientService,
      JwtUtilService jwtUtilService, PasswordEncoder passwordEncoder) {
    this.authenticationManager = authenticationManager;
    this.clientService = clientService;
    this.jwtUtilService = jwtUtilService;
    this.passwordEncoder = passwordEncoder;
  }

  @Operation(summary = "Creates a client to generate JWT Token")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Client Created",
          content = {@Content(mediaType = "application/json",
              schema = @Schema(implementation = String.class))}),
      @ApiResponse(responseCode = "400", description = "Bad Request",
          content = @Content),
      @ApiResponse(responseCode = "500", description = "Internal Server Error",
          content = @Content)})
  @PostMapping(CLIENT_REGISTRATION)
  public ResponseEntity<String> registerUser(@RequestBody @Valid ClientRequest clientRequest) {
    clientRequest.setClientSecret(passwordEncoder.encode(clientRequest.getClientSecret()));
    clientService.saveClient(clientRequest);
    return ResponseEntity.ok("Client registered successfully!");
  }

  @Operation(summary = "Generates JWT Token for provided Client Id/Secret in form of Basic Authorization")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "JWT Token Created",
          content = {@Content(mediaType = "application/json",
              schema = @Schema(implementation = JwtTokenResponse.class))}),
      @ApiResponse(responseCode = "400", description = "Bad Request",
          content = @Content),
      @ApiResponse(responseCode = "401", description = "Bad Credentials",
          content = @Content),
      @ApiResponse(responseCode = "500", description = "Internal Server Error",
          content = @Content)})
  @PostMapping(TOKEN)
  public ResponseEntity<JwtTokenResponse> generateJwtToken(
      @RequestHeader(AUTHORIZATION_HEADER) String authorizationHeader) {
    String[] clientCredentialsArray;
    try {
      if (authorizationHeader == null || !authorizationHeader.startsWith(BASIC_AUTH)) {
        throw new BadCredentialsException("Invalid Authorization header");
      }
      authorizationHeader = authorizationHeader.substring(BASIC_AUTH.length());
      clientCredentialsArray = new String(Base64.getDecoder().decode(authorizationHeader)).split(
          COLON);

      authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(clientCredentialsArray[0],
              clientCredentialsArray[1])
      );
    } catch (BadCredentialsException exception) {
      log.error("Bad credentials", exception);
      throw exception;
    } catch (Exception e) {
      log.error("Unexpected exception", e);
      throw new GenericException("Unknown Exception Occurred.");
    }
    UserDetails userDetails = clientService.loadUserByUsername(clientCredentialsArray[0]);
    return ResponseEntity.ok(jwtUtilService.generateToken(userDetails));
  }

  @Operation(summary = "Validates a JWT Token")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "JWT Token Validation",
          content = {@Content(mediaType = "application/json",
              schema = @Schema(implementation = Boolean.class))}),
      @ApiResponse(responseCode = "400", description = "Bad Request",
          content = @Content),
      @ApiResponse(responseCode = "401", description = "Invalid Token",
          content = @Content),
      @ApiResponse(responseCode = "500", description = "Internal Server Error",
          content = @Content)})
  @PostMapping(VALIDATE_TOKEN)
  public ResponseEntity<Boolean> validateToken(
      @RequestBody @Valid JwtTokenRequest tokenRequest) {
    return ResponseEntity.status(HttpStatus.OK)
        .body(jwtUtilService.isTokenValid(tokenRequest.getToken()));
  }
}
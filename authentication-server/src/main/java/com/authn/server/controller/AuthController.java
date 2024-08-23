package com.authn.server.controller;

import com.authn.server.entity.Client;
import com.authn.server.service.ClientService;
import com.authn.server.service.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

  private final AuthenticationManager authenticationManager;

  private final ClientService clientService;

  private final JwtService jwtService;

  private final PasswordEncoder passwordEncoder;

  public AuthController(AuthenticationManager authenticationManager, ClientService clientService,
      JwtService jwtService, PasswordEncoder passwordEncoder) {
    this.authenticationManager = authenticationManager;
    this.clientService = clientService;
    this.jwtService = jwtService;
    this.passwordEncoder = passwordEncoder;
  }

  @PostMapping("/register")
  public ResponseEntity<String> registerUser(@RequestBody Client client) {
    client.setClientSecret(passwordEncoder.encode(client.getClientSecret()));
    clientService.saveClient(client);
    return ResponseEntity.ok("Client registered successfully!");
  }

  @PostMapping("/login")
  public ResponseEntity<String> loginUser(@RequestBody AuthenticationRequest request) {
    try {
      authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(request.getClientId(), request.getClientSecret())
      );
    } catch (BadCredentialsException e) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
    }

    UserDetails userDetails = clientService.loadUserByUsername(request.getClientId().toString());
    String token = jwtService.generateToken(userDetails);

    return ResponseEntity.ok(token);
  }
}
package com.authn.server.util;

import static com.authn.server.constants.AuthenticationServerConstants.ALGORITHM;
import static com.authn.server.constants.AuthenticationServerConstants.AUTHENTICATION_SERVER;
import static com.authn.server.constants.AuthenticationServerConstants.BEARER;
import static com.authn.server.constants.AuthenticationServerConstants.EXTRA_CLAIMS;
import static com.authn.server.constants.AuthenticationServerConstants.JWT;
import static com.authn.server.constants.AuthenticationServerConstants.SCOPES;
import static com.authn.server.constants.AuthenticationServerConstants.TYPE;

import com.authn.server.controller.JwtTokenResponse;
import com.authn.server.exception.GenericException;
import com.authn.server.exception.InvalidJwtTokenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Jwts.SIG;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.crypto.SecretKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

/**
 * @author rahulchauhan
 * <p>
 * Used to create/validate a JWT token for a client id
 */
@Service
@Slf4j
public class JwtUtilService {

  @Value("${security.jwt.secret-key}")
  private String secretKey;

  @Value("${security.jwt.expiration-time}")
  private long jwtExpiration;

  private final UserDetailsService userDetailsService;

  public JwtUtilService(UserDetailsService userDetailsService) {
    this.userDetailsService = userDetailsService;
  }

  private String extractUsername(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  public JwtTokenResponse generateToken(UserDetails userDetails) {
    return generateToken(new HashMap<>(), userDetails);
  }

  public JwtTokenResponse generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
    return buildToken(extraClaims, userDetails, jwtExpiration);
  }

  private JwtTokenResponse buildToken(Map<String, Object> extraClaims, UserDetails userDetails,
      long expiration) {
    try {
      UUID jwtTokenId = UUID.randomUUID();
      return JwtTokenResponse.builder().access_token(Jwts
              .builder()
              .header().add(ALGORITHM, SIG.HS512.toString()).add(TYPE, JWT)
              .and()
              .id(jwtTokenId.toString())
              .claims(Jwts.claims().subject(userDetails.getUsername())
                  .issuedAt(new Date(System.currentTimeMillis()))
                  .expiration(new Date(System.currentTimeMillis() + expiration))
                  .issuer(AUTHENTICATION_SERVER)
                  .add(SCOPES, userDetails.getAuthorities().stream().map(
                      GrantedAuthority::getAuthority).toList())
                  .add(EXTRA_CLAIMS, extraClaims).build())
              .signWith(getSignInKey(), SIG.HS512)
              .compact())
          .token_type(BEARER)
          .scopes(userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(
              Collectors.joining(" ")))
          .expires_in(expiration / 1000L).build();
    } catch (Exception e) {
      log.error("Error generating token for client id: {}", userDetails.getUsername(), e);
      throw new GenericException(
          "Error Generating JWT Token for client id: " + userDetails.getUsername());
    }
  }

  public boolean isTokenValid(String token) {
    try {
      final String username = extractUsername(token);
      UserDetails userDetails = userDetailsService.loadUserByUsername(username);
      return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    } catch (ExpiredJwtException exception) {
      log.warn("JWT Token expired: ", exception);
      throw new InvalidJwtTokenException("JWT Token Expired");
    } catch (Exception exception) {
      log.error("Error checking if token is valid: ", exception);
      throw new InvalidJwtTokenException("Invalid Token");
    }
  }

  private boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
  }

  private Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }

  private Claims extractAllClaims(String token) {
    return Jwts
        .parser()
        .verifyWith(getSignInKey())
        .build()
        .parseSignedClaims(token)
        .getPayload();
  }

  private SecretKey getSignInKey() {
    byte[] keyBytes = Decoders.BASE64.decode(secretKey);
    return Keys.hmacShaKeyFor(keyBytes);
  }
}
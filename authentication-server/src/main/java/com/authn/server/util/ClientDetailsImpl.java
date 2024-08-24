package com.authn.server.util;

import com.authn.server.entity.Client;
import com.authn.server.entity.Scope;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * @author rahulchauhan
 * <p>
 * Used to convert Client Entity to Spring Security User Details
 */
public class ClientDetailsImpl implements UserDetails {

  Client client;

  public ClientDetailsImpl(Client client) {
    this.client = client;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    Set<Scope> scopes = client.getScopes();
    List<SimpleGrantedAuthority> authorities = new ArrayList<>();
    for (Scope scope : scopes) {
      authorities.add(new SimpleGrantedAuthority(scope.name()));
    }
    return authorities;
  }

  @Override
  public String getPassword() {
    return client.getClientSecret();
  }

  @Override
  public String getUsername() {
    return client.getClientId().toString();
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }

  public Client getClient() {
    return client;
  }
}

package com.authn.server.service;

import com.authn.server.controller.ClientRequest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * @author rahulchauhan
 */
public interface ClientService extends UserDetailsService {

  UserDetails loadUserByUsername(String clientId);

  void saveClient(ClientRequest clientRequest);
}
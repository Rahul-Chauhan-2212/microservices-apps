package com.authn.server.service;

import com.authn.server.entity.Client;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public interface ClientService extends UserDetailsService {

  UserDetails loadUserByUsername(String clientId);

  void saveClient(Client client);
}
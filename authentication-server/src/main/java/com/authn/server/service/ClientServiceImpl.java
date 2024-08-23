package com.authn.server.service;

import com.authn.server.entity.Client;
import com.authn.server.entity.ClientDetailsImpl;
import com.authn.server.repository.ClientRepository;
import java.util.Optional;
import java.util.UUID;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service("userDetailsService")
public class ClientServiceImpl implements ClientService {

  private final ClientRepository clientRepository;

  public ClientServiceImpl(ClientRepository clientRepository) {
    this.clientRepository = clientRepository;
  }

  @Override
  public UserDetails loadUserByUsername(String clientId) throws UsernameNotFoundException {
    Optional<Client> client = clientRepository.findById(UUID.fromString(clientId));
    if (client.isEmpty()) {
      throw new RuntimeException("Could not find user");
    }
    return new ClientDetailsImpl(client.get());
  }

  @Override
  public void saveClient(Client client) {
    clientRepository.save(client);
  }
}
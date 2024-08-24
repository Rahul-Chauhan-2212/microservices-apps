package com.authn.server.service;

import static org.springframework.beans.BeanUtils.copyProperties;

import com.authn.server.controller.ClientRequest;
import com.authn.server.entity.Client;
import com.authn.server.exception.GenericException;
import com.authn.server.repository.ClientRepository;
import com.authn.server.util.ClientDetailsImpl;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * @author rahulchauhan
 */
@Service("userDetailsService")
@Slf4j
public class ClientServiceImpl implements ClientService {

  private final ClientRepository clientRepository;

  public ClientServiceImpl(ClientRepository clientRepository) {
    this.clientRepository = clientRepository;
  }

  @Override
  public UserDetails loadUserByUsername(String clientId) throws UsernameNotFoundException {
    Optional<Client> client = clientRepository.findById(UUID.fromString(clientId));
    if (client.isEmpty()) {
      log.warn("Client with id {} not found", clientId);
      throw new UsernameNotFoundException("Could not find user with id " + clientId);
    }
    return new ClientDetailsImpl(client.get());
  }

  @Override
  public void saveClient(ClientRequest clientRequest) {
    try {
      Client client = new Client();
      copyProperties(clientRequest, client);
      clientRepository.save(client);
    } catch (Exception e) {
      log.error("Could not save client with client id: {}", clientRequest.getClientId(), e);
      throw new GenericException(
          "Could not save client with client id: " + clientRequest.getClientId());
    }
  }
}
package com.authn.server;

import com.authn.server.controller.ClientRequest;
import com.authn.server.entity.Scope;
import java.util.Set;
import java.util.UUID;

public class TestDataUtil {

  public static ClientRequest getClientRequest() {
    return ClientRequest.builder().clientId(UUID.randomUUID())
        .clientSecret("Rahul@22121995").fullName("Rahul Chauhan").email("rchauhan9102@gmail.com")
        .scopes(
            Set.of(Scope.GET))
        .build();
  }

}

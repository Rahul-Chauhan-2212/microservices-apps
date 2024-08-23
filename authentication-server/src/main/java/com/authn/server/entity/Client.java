package com.authn.server.entity;

import jakarta.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Builder
@Document(collection = "client")
public class Client extends BaseEntity {

  @Id
  private UUID clientId;

  @NotNull
  private String fullName;

  @NotNull
  private String email;

  @NotNull
  private String clientSecret;

  private Set<Scope> scopes = new HashSet<>();
}
package com.authn.server.entity;

import jakarta.validation.constraints.NotEmpty;
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

  @NotEmpty
  private String clientSecret;

  @NotEmpty
  private String fullName;

  @NotEmpty
  private String email;

  private Set<Scope> scopes = new HashSet<>();
}
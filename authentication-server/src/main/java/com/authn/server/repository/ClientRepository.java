package com.authn.server.repository;

import com.authn.server.entity.Client;
import java.util.UUID;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * @author rahulchauhan
 */
@Repository
public interface ClientRepository extends MongoRepository<Client, UUID> {

}

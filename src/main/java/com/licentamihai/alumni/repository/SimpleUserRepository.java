package com.licentamihai.alumni.repository;

import com.licentamihai.alumni.model.SimpleUser;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface SimpleUserRepository extends MongoRepository<SimpleUser, String> {

    public SimpleUser findByFirstName(String firstName);
    public List<SimpleUser> findByLastName(String lastName);
    public SimpleUser findByUsername(String username);
}

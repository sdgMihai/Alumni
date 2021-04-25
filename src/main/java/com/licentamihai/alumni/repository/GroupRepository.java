package com.licentamihai.alumni.repository;

import com.licentamihai.alumni.model.Group;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface GroupRepository  extends MongoRepository<Group, String> {
    @Query(value="{'name' : $0}", delete = true)
    public void deleteByName (String id);
}

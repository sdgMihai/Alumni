package com.licentamihai.alumni.repository;

import com.licentamihai.alumni.model.Group;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupRepository  extends MongoRepository<Group, String> {
    public Group findByName(String name);

    Long deleteGroupByName(String name);
}

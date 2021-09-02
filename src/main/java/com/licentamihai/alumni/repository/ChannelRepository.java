package com.licentamihai.alumni.repository;

import com.licentamihai.alumni.model.Channel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface ChannelRepository extends MongoRepository<Channel, String> {
    public Long deleteChannelByName(String name);
}

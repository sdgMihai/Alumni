package com.licentamihai.alumni.service;

import com.licentamihai.alumni.model.Channel;
import com.licentamihai.alumni.model.Group;
import com.licentamihai.alumni.repository.GroupRepository;
import com.licentamihai.alumni.repository.SimpleUserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

@Service
public class ChannelService {
    private static final Logger log = LogManager.getLogger(UserService.class);
    private final SimpleUserRepository simpleUserRepository;
    private final GroupRepository groupRepository;

    public ChannelService(SimpleUserRepository simpleUserRepository, GroupRepository groupRepository) {
        this.simpleUserRepository = simpleUserRepository;
        this.groupRepository = groupRepository;
    }

    public Channel findByGroupNameAndName(String groupName, String name) {
        final Group group = groupRepository.findByName(groupName);
        return (group == null ? null : group.getChannels().stream()
                .filter(channel -> channel.getName().equals(name))
                .findAny().orElseGet(() -> null));
    }
}

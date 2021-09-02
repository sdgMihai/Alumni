package com.licentamihai.alumni.service;

import com.licentamihai.alumni.dto.BasicUserChannel;
import com.licentamihai.alumni.dto.BasicUserGroup;
import com.licentamihai.alumni.model.Channel;
import com.licentamihai.alumni.model.Group;
import com.licentamihai.alumni.model.SimpleUser;
import com.licentamihai.alumni.repository.GroupRepository;
import com.licentamihai.alumni.repository.SimpleUserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class UserService {
    private static final Logger log = LogManager.getLogger(UserService.class);
    private final SimpleUserRepository simpleUserRepository;
    private final GroupRepository groupRepository;


    public UserService(SimpleUserRepository simpleUserRepository, GroupRepository groupRepository) {
        this.simpleUserRepository = simpleUserRepository;
        this.groupRepository = groupRepository;
    }

    public SimpleUser createTemplateUser() {
        SimpleUser simpleUser = new SimpleUser(null, "password");
        simpleUser.setSuperAdmin(false);
        simpleUser.setGroupAdmin(false);
        simpleUser.setProfileImage("images/default.jpg");
        Group newbies = Optional.ofNullable(groupRepository.findByName("newbies"))
                .orElseGet(() -> {
                    final Group group = new Group("newbies");
                    group.setChannels(Arrays.asList(new Channel("general"), new Channel("help")));
                    return group;
                });

        Group general = Optional.ofNullable(groupRepository.findByName("general"))
                .orElseGet(() -> {
                    final Group group = new Group("general");
                    group.setChannels(Arrays.asList(new Channel("general")
                            , new Channel("chitchat")
                            , new Channel("topic of the day")));
                    return group;
                });

        simpleUser.addGroup(newbies);
        simpleUser.addGroup(general);
        simpleUser.getGroups().stream()
                .flatMap(group -> group.getChannels().stream())
                .collect(Collectors.toList())
                .forEach(channel -> simpleUser.addChannel(channel));

        return simpleUser;
    }

    public SimpleUser createTemplateSuperUser() {
        SimpleUser simpleUser = createTemplateUser();
        simpleUser.setUsername("Super");
        simpleUser.setEmail("super@admin.com");
        simpleUser.setSuperAdmin(true);
        simpleUser.setGroupAdmin(true);
        return simpleUser;
    }

    public Boolean createUser(SimpleUser user) {
        SimpleUser existingUser = simpleUserRepository.findByUsername(user.getUsername());
        log.error(existingUser != null ? "user found" : "user not found");

        if (existingUser == null) {
            SimpleUser templateUser = createTemplateUser();
            if (user.getUsername() != null)
                templateUser.setUsername(user.getUsername());
            if (user.getPassword() != null)
                templateUser.setPassword(user.getPassword());
            if (user.getEmail() != null)
                templateUser.setEmail(user.getEmail());
            simpleUserRepository.save(templateUser);
        }
        return existingUser == null;
    }

    public Boolean addUserToGroup(BasicUserGroup user) {
        SimpleUser simpleUser = simpleUserRepository.findByUsername(user.getUsername());
        final Group group = Optional.ofNullable(groupRepository.findByName(user.getGroupName())).orElseGet(
                () -> {
                    groupRepository.save(new Group(user.getGroupName()));
                    return groupRepository.findByName(user.getGroupName());
                });
        simpleUser.addGroup(group);
        simpleUserRepository.save(simpleUser);
        return true;
    }

    public void addUserToChannel(SimpleUser simpleUser, Channel channel, String groupName) {
        Group group = simpleUser.getGroups().stream()
                .filter(group1 -> group1.getName().equals(groupName))
                .findAny()
                .get();
        final Channel channel2 = group.getChannels().stream()
                .filter(channel1 -> channel1.getName().equals(channel.getName()))
                .findAny()
                .orElseGet(() -> channel);
        simpleUser.addChannel(channel2);
        simpleUserRepository.save(simpleUser);
    }

    public List<SimpleUser> findUsersByGroup(String groupName) {
        return simpleUserRepository.findAll().stream().
                filter(user -> user.getGroups().stream()
                        .anyMatch(group -> group.getName().equals(groupName)))
                .collect(Collectors.toList());
    }

    public List<SimpleUser> findUsersByChannel(String groupName, String channelName) {
        return findUsersByGroup(groupName).stream()
                .filter(user -> user.getChannels().stream()
                        .anyMatch(channel -> channel.getName().equals(channelName)))
                .collect(Collectors.toList());
    }

    public void removeUserFromChannel(SimpleUser simpleUser, String channelName, String groupName) {
        final Group group = groupRepository.findByName(groupName);
        final Channel channel1 = group.getChannels().stream()
                .filter(channel -> channel.getName().equals(channelName))
                .findAny().orElseGet(() -> null);
        log.debug("trying to remove channel {}", channel1);
        simpleUser.getChannels().remove(channel1);
        simpleUserRepository.save(simpleUser);
    }

}

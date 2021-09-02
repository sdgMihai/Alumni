package com.licentamihai.alumni.controller;

import com.licentamihai.alumni.dto.BasicUserGroup;
import com.licentamihai.alumni.dto.BasicUserChannel;
import com.licentamihai.alumni.dto.GroupChannel;
import com.licentamihai.alumni.model.Channel;
import com.licentamihai.alumni.model.Group;
import com.licentamihai.alumni.model.Message;
import com.licentamihai.alumni.model.SimpleUser;
import com.licentamihai.alumni.repository.ChannelRepository;
import com.licentamihai.alumni.repository.GroupRepository;
import com.licentamihai.alumni.repository.MessagesRepository;
import com.licentamihai.alumni.repository.SimpleUserRepository;
import com.licentamihai.alumni.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

//@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/")
public class GroupController {
    private final SimpleUserRepository simpleUserRepository;
    private final GroupRepository groupRepository;
    private final MessagesRepository messagesRepository;
    private static final Logger log = LogManager.getLogger(GroupController.class);
    private final MongoTemplate mongoTemplate;
    private ChannelRepository channelRepository;
    private final UserService userService;

    @Autowired
    public GroupController(SimpleUserRepository simpleUserRepository
            , GroupRepository groupRepository
            , MessagesRepository messagesRepository
            , MongoTemplate mongoTemplate
            , ChannelRepository channelRepository
            , UserService userService) {
        this.simpleUserRepository = simpleUserRepository;
        this.groupRepository = groupRepository;
        this.messagesRepository = messagesRepository;
        this.mongoTemplate = mongoTemplate;
        this.channelRepository = channelRepository;
        this.userService = userService;
    }



    @PostMapping(value = "/createGroup", consumes = "application/json", produces = "application/json")
    public ResponseEntity<List<Group>> createGroup(@RequestBody BasicUserGroup user) {
        log.debug("POST request at /api/createGroup");
        SimpleUser simpleUser = simpleUserRepository.findByUsername(user.getUsername());
        if (simpleUser != null) {
            log.debug("\tCreating new group {} for user {}", user.getGroupName(), user.getUsername());
            Group foundGroup= null;
            if (simpleUser.getGroups() != null) {
                foundGroup = simpleUser.getGroups().stream()
                        .filter(Objects::nonNull)
                        .filter(x -> x.getName().equals(user.getGroupName()))
                        .findAny()
                        .orElse(null);
            }
            log.debug(foundGroup != null ? "found group" : "not found");
            if (foundGroup == null) {
                log.debug("entered saving group");
                Group group = new Group(user.getGroupName());
                simpleUser.addGroup(group);
                simpleUserRepository.save(simpleUser);
            }
        }
        return ResponseEntity.ok(groupRepository.findAll());
    }


    @GetMapping(value="/groups")
    public ResponseEntity<List<Group>> groups() {
        return ResponseEntity.ok(groupRepository.findAll());
    }


    @PostMapping(value = "/removeGroup/{groupName}")
    public ResponseEntity<List<Group>> removeGroup(@PathVariable String groupName) {
        // see route parameters from https://developer.mozilla.org/en-US/docs/Learn/Server-side/Express_Nodejs/routes

        log.debug("DELETE request at /api/removeGroup");
        List<SimpleUser> users = simpleUserRepository.findAll().stream()
                .filter(x -> x.getGroups().stream()
                            .anyMatch(group -> group.getName().equals(groupName)))
                .collect(Collectors.toList());
        if (users.size() != 0) {
            users.forEach(user -> user.getGroups().remove(
                    user.getGroups().stream()
                            .filter(group -> group.getName().equals(groupName))
                            .findFirst()
                            .orElse( null)));
            users.forEach(simpleUserRepository::save);
            final Group byName = groupRepository.findByName(groupName);
            log.debug("found {} ", byName.toString());
            groupRepository.deleteGroupByName(groupName);
        }
        return ResponseEntity.ok(groupRepository.findAll());
    }


    @GetMapping(value = "/{group}/channels")
    public ResponseEntity<List<Channel>> getGroupChannels(@PathVariable String group) {
        log.debug("GET request at /api/{}/channels", group);
        return ResponseEntity.ok(groupRepository.findByName(group).getChannels());
    }


    @GetMapping(value= "/{groupName}/users")
    public ResponseEntity<List<SimpleUser>> getUsersByGroup(@PathVariable String groupName) {
        log.debug("GET request at /api/{}/users", groupName);
        List<SimpleUser> simpleUsers = simpleUserRepository.findAll().stream()
                .filter(simpleUser -> simpleUser.getGroups().stream()
                            .anyMatch(group -> group.getName().equals(groupName)))
                .collect(Collectors.toList());
        log.debug("all users {}", simpleUsers);
        return ResponseEntity.ok(simpleUsers);
    }


    @PostMapping(value="/channel/create")
    public ResponseEntity<List<Channel>> createChannel(@RequestBody BasicUserChannel channelDTO) {
        log.debug("create channel {} in group {}", channelDTO.getChannelName(), channelDTO.getGroupName());
        log.debug("by user with username: {}", channelDTO.getUsername());
        Optional<SimpleUser> user = Optional.ofNullable(simpleUserRepository.findByUsername(channelDTO.getUsername()));
        Group group = null;
        if (user.orElse(new SimpleUser("error")).getGroupAdmin() != null
                && user.get().getGroupAdmin()) {
            log.debug("groupName {}", channelDTO.getGroupName());
            group = groupRepository.findByName(channelDTO.getGroupName());
            log.debug("group found {}", group);
            group.getChannels().add(new Channel(channelDTO.getChannelName()));
            groupRepository.save(group);
        }

        log.debug("channels : {}", group == null ? null : group.getChannels());
        // TODO: in case this is done by a non-admin no json is sent to frontend(just an empty httpmessaje)
        // I should make a workaround to send smth
        return ResponseEntity.ok(Optional.ofNullable(group).orElse(new Group("error")).getChannels());
    }


    @GetMapping(value = "channel/remove/{username}.{groupName}.{channelName}")
    public ResponseEntity<List<Channel>> deleteChannel(@PathVariable String username, @PathVariable String groupName, @PathVariable String channelName) {
        Optional<SimpleUser> user = Optional.ofNullable(simpleUserRepository.findByUsername(username));
        Group group = null;
        if (user.orElse(new SimpleUser("error")).getGroupAdmin() != null
                && user.get().getGroupAdmin()) {
            log.debug("groupName {}", groupName);
            group = groupRepository.findByName(groupName);
            log.debug("group found {}", group);
            final Channel channel1 = group.getChannels().stream()
                    .filter(channel -> channel.getName().equals(channelName))
                    .findAny()
                    .orElse(null);
            group.getChannels().remove(channel1);
            channelRepository.deleteById(channel1.getId());
            groupRepository.save(group);
        }

        log.debug("channels : {}", group == null ? null : group.getChannels());
        return ResponseEntity.ok(Optional.ofNullable(group).orElse(new Group("error")).getChannels());
    }

    @GetMapping(value = "channel/messages")
    public ResponseEntity<List<Message>> getChannelMessages(@RequestParam String groupName, @RequestParam String channelName) {
        return ResponseEntity.ok(
            messagesRepository.findAllByChannelNameAndGroupName(
                channelName
                , groupName));
    }

}

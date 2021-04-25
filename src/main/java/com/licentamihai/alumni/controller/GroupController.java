package com.licentamihai.alumni.controller;

import com.licentamihai.alumni.dto.BasicUserGroup;
import com.licentamihai.alumni.model.Group;
import com.licentamihai.alumni.model.SimpleUser;
import com.licentamihai.alumni.repository.GroupRepository;
import com.licentamihai.alumni.repository.SimpleUserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/")
public class GroupController {
    private final SimpleUserRepository simpleUserRepository;
    private final GroupRepository groupRepository;
    private static final Logger log = LogManager.getLogger(UsersController.class);
    private final MongoTemplate mongoTemplate;

    @Autowired
    public GroupController(SimpleUserRepository simpleUserRepository, GroupRepository groupRepository, MongoTemplate mongoTemplate) {
        this.simpleUserRepository = simpleUserRepository;
        this.groupRepository = groupRepository;
        this.mongoTemplate = mongoTemplate;
    }


    @Transactional
    @PostMapping(value = "/createGroup", consumes = "application/json", produces = "application/json")
    public ResponseEntity<List<Group>> createGroup(@RequestBody BasicUserGroup user) {
        log.debug("POST request at /api/createGroup");
        SimpleUser simpleUser = simpleUserRepository.findByUsername(user.getUsername());
        if (simpleUser != null) {
            log.debug("\tCreating new group {} for user {}", user.getGroupName(), user.getUsername());
            Group foundGroup= null;
            if (simpleUser.getGroups() != null) {
                foundGroup = simpleUser.getGroups().stream()
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
        // TODO: when app is ready check if it is necessary to wait for the save op to take place
        // maybe replace in the frontend with a subsequent call at /groups to assure
        // by transactional that saving is finished before retrieving group names
        return ResponseEntity.ok(groupRepository.findAll());
    }

    @Transactional
    @GetMapping(value="/groups")
    public ResponseEntity<List<Group>> groups() {
        return ResponseEntity.ok(groupRepository.findAll());
    }

    @Transactional
    @PostMapping(value = "/removeGroup")
    public ResponseEntity<List<Group>> removeGroup(@RequestParam String groupName) {
        log.debug("DELETE request at /api/removeGroup");
        List<SimpleUser> users = simpleUserRepository.findAll()
                .stream()
                .filter(x -> x.getGroups().stream()
                            .anyMatch(group -> group.getName().equals(groupName)))
                .collect(Collectors.toList());
        users.forEach(user -> {
            user.getGroups().remove(
                            user.getGroups().stream()
                                .filter(group -> group.getName().equals(groupName))
                                .findFirst()
                                .orElse( null));
                 });
        users.forEach(simpleUserRepository::save);  //  TODO : this does update or duplicate?
        groupRepository.deleteByName(groupName);

        return ResponseEntity.ok(groupRepository.findAll());
    }

}

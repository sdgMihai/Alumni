package com.licentamihai.alumni.controller;

import com.licentamihai.alumni.dto.BasicUserEmail;
import com.licentamihai.alumni.dto.BasicUserGroup;
import com.licentamihai.alumni.dto.BasicUserLogin;
import com.licentamihai.alumni.dto.BasicUserPhoto;
import com.licentamihai.alumni.model.Group;
import com.licentamihai.alumni.model.SimpleUser;
import com.licentamihai.alumni.repository.GroupRepository;
import com.licentamihai.alumni.repository.SimpleUserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/")
public class UsersController {
    private final SimpleUserRepository simpleUserRepository;
    private final GroupRepository groupRepository;
    private static final Logger log = LogManager.getLogger(UsersController.class);
    private final MongoTemplate mongoTemplate;

    @Autowired
    public UsersController(SimpleUserRepository simpleUserRepository, GroupRepository groupRepository, MongoTemplate mongoTemplate) {
        this.simpleUserRepository = simpleUserRepository;
        this.groupRepository = groupRepository;
        this.mongoTemplate = mongoTemplate;
    }

    @Transactional
    @PostMapping(value = "/user/validate", consumes = "application/json", produces = "application/json")
    public ResponseEntity<Boolean> userValidate(@RequestBody BasicUserLogin user) {
        final SimpleUser existingUser = simpleUserRepository.findByUsername(user.getUsername());
        log.debug((existingUser != null) ? "user validated" : "user not validated");
        return ResponseEntity.ok(existingUser != null);
    }

    @Transactional
    @PostMapping(value = "/email", consumes = "application/json", produces = "application/json")
    public ResponseEntity<Boolean> email(@RequestBody BasicUserEmail user) throws NullPointerException {
        final SimpleUser existingUser = simpleUserRepository.findByUsername(user.getUsername());
        log.debug(user.toString());
        log.debug((existingUser != null) ? "user validated" : "user not validated");
        existingUser.setEmail(user.getEmail());
        simpleUserRepository.save(existingUser);
        return ResponseEntity.ok(existingUser != null);
    }


    @Transactional
    @PostMapping(value = "/user/create", consumes = "application/json", produces = "application/json")
    public ResponseEntity<Boolean> userCreate(@RequestBody SimpleUser user) {  // TODO: TO TEST
        log.debug(user.toString());
        SimpleUser existingUser = mongoTemplate.findOne(
                Query.query(Criteria.where("username").is(user.getUsername())), SimpleUser.class);
        log.error(existingUser != null ? "user found with mongoTemplate" : "user not found with mongoTemplate");
        if (existingUser == null) {
            simpleUserRepository.save(user);
        }
        return ResponseEntity.ok(true);
    }

    @Transactional
    @GetMapping(value = "/user")
    public ResponseEntity<SimpleUser> user(@RequestBody SimpleUser user) {
        SimpleUser simpleUser = simpleUserRepository.findByUsername(user.getUsername());
        if (simpleUser == null) {
            log.debug("Creating user {} and saving to file", user.getUsername());
            // TODO add all fields with default implementation
            simpleUserRepository.save(user);
            simpleUser = simpleUserRepository.findByUsername(user.getUsername());
            if (simpleUser == null) {
                log.error("simple user not found , add timeout before or check error");
            }
        }
        return ResponseEntity.ok(simpleUser);
    }


    /**
     * In fact this updates the photo path.
     * @param user This contains the username and his new photo path.
     * @return A boolean meaning operation success.
     */
    @Transactional
    @PostMapping(value = "/user/update")
    public ResponseEntity<Boolean> userUpdate(@RequestBody BasicUserPhoto user) {
        log.debug("POST request at /api/user/update");
        // TODO: Change URI to a more apropiate name(*photo*)
        // TODO: verify if the actual photo is visible in the website
        // how it is stored permanently?

        final SimpleUser existingUser = simpleUserRepository.findByUsername(user.getUsername());
        String imagePath = user.getPath().substring(2);
        existingUser.setProfileImage(imagePath);
        simpleUserRepository.save(existingUser);
        return ResponseEntity.ok(existingUser != null);
    }
}

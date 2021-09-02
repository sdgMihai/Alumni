package com.licentamihai.alumni.controller;

import com.licentamihai.alumni.dto.*;
import com.licentamihai.alumni.model.*;
import com.licentamihai.alumni.repository.ChannelRepository;
import com.licentamihai.alumni.repository.GroupRepository;
import com.licentamihai.alumni.repository.SimpleUserRepository;
import com.licentamihai.alumni.service.ChannelService;
import com.licentamihai.alumni.service.UserService;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.Binary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

//@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/")
public class UsersController {
    private final SimpleUserRepository simpleUserRepository;
    private final GroupRepository groupRepository;
    private static final Logger log = LogManager.getLogger(UsersController.class);
    private final MongoTemplate mongoTemplate;
    private final UserService userService;
    private final ChannelService channelService;
    private ChannelRepository channelRepository;
    private final String imageFolder = "src/main/java/com/licentamihai/alumni/images/";

    @Autowired
    public UsersController(SimpleUserRepository simpleUserRepository, GroupRepository groupRepository, MongoTemplate mongoTemplate, UserService userService, ChannelService channelService, ChannelRepository channelRepository) {
        this.simpleUserRepository = simpleUserRepository;
        this.groupRepository = groupRepository;
        this.mongoTemplate = mongoTemplate;
        this.userService = userService;
        this.channelService = channelService;
        this.channelRepository = channelRepository;
    }


    @PostMapping(value = "/user/validate", consumes = "application/json", produces = "application/json")
    public ResponseEntity<Boolean> userValidate(@RequestBody BasicUserLogin user) {
        log.debug("requested {}", user);
        final SimpleUser existingUser = simpleUserRepository.findByUsernameAndPassword(
                user.getUsername()
                , user.getPassword());
        if (existingUser != null) {
            log.debug( "user validated");
            log.debug("user {}", existingUser.myString());
        } else {
            log.debug("user not validated");
        }
        return ResponseEntity.ok(existingUser != null);
    }


    @PostMapping(value = "/email", consumes = "application/json", produces = "application/json")
    public ResponseEntity<Boolean> email(@RequestBody BasicUserEmail user) throws NullPointerException {
        final SimpleUser existingUser = simpleUserRepository.findByUsername(user.getUsername());
        log.debug(user.toString());
        log.debug((existingUser != null) ? "user validated" : "user not validated");
        existingUser.setEmail(user.getEmail());
        simpleUserRepository.save(existingUser);
        return ResponseEntity.ok(existingUser != null);
    }



    @PostMapping(value = "/user/create", consumes = "application/json", produces = "application/json")
    public ResponseEntity<Boolean> userCreate(@RequestBody SimpleUser user) {  // TODO: TO TEST
        log.debug(user.toString());
        Boolean success = userService.createUser(user);
        return ResponseEntity.ok(true);
    }


    @PostMapping(value = "super/user/create")
    public void superUserCreate() {
        final SimpleUser superUser = userService.createTemplateSuperUser();
        simpleUserRepository.save(superUser);
    }



    @GetMapping(value = "/user")
    public ResponseEntity<SimpleUser> user(@RequestParam String username) {
        SimpleUser simpleUser = simpleUserRepository.findByUsername(username);
        if (simpleUser == null) {
            log.debug("Creating user {} and saving to file", username);
            SimpleUser user = userService.createTemplateUser();
            user.setUsername(username);
            simpleUserRepository.save(user);
            simpleUser = simpleUserRepository.findByUsername(user.getUsername());
            if (simpleUser == null) {
                log.error("simple user not found , add timeout before or check error");
            }
        }
        log.debug("user {}", simpleUser);
        return ResponseEntity.ok(simpleUser);
    }

    @GetMapping(value = "/user/{username}")
    public ResponseEntity<Boolean> userExists(@PathVariable String username) {
        return ResponseEntity.ok(simpleUserRepository.findByUsername(username) != null);
    }

    @GetMapping(value = "/images/{image}", produces = MediaType.IMAGE_JPEG_VALUE)
    public @ResponseBody byte[] getImage(@PathVariable String image) throws IOException {
        log.debug("getting image");
        File tempFile = new File(imageFolder + image);
        log.debug("exists {}", tempFile.exists());
        InputStream in = new FileInputStream(tempFile);
        log.debug("is available {}", in.available());
        return IOUtils.toByteArray(in);
    }

    @GetMapping(value = "/images/profileImage", produces = MediaType.IMAGE_JPEG_VALUE)
    public @ResponseBody byte[] getProfileImage(@RequestParam String username) {
        SimpleUser user = simpleUserRepository.findByUsername(username);
        return user.getImage().getData();
    }

    @PostMapping(value = "/image/upload", produces = "application/json")
    public ResponseEntity<ImageUploadResponse> uploadImage(MultipartHttpServletRequest request, HttpServletResponse response) throws IOException {
        Iterator<String> itr = request.getFileNames();
        MultipartFile file = null;
        log.debug("itr {}", itr.hasNext());

        if (itr.hasNext()) {
            // save file
            file = request.getFile(itr.next());
            String username = request.getParameter("username");
            SimpleUser user = simpleUserRepository.findByUsername(username);
            assert file != null;
            user.setImage(new Binary(file.getBytes()));
            simpleUserRepository.save(user);
        }
        ImageUploadResponse imgUploadRes = new ImageUploadResponse(
                new ImageData(file.getOriginalFilename(), file.getSize())
                , imageFolder + file.getOriginalFilename());
        return ResponseEntity.ok(imgUploadRes);  // todo : test
        // TODO: USE THE PATHS WITH ERROR to decide if user image path should be modified and how
        // todo: check for all endpoints with images
    }

    /**
     * In fact this updates the photo path.
     *
     * @param user This contains the username and his new photo path.
     * @return A boolean meaning operation success.
     */

    @PostMapping(value = "/user/update")
    public ResponseEntity<Boolean> userUpdate(@RequestBody BasicUserPhoto user) {
        log.debug("POST request at /api/user/update -> update image path");
        // TODO: Change URI to a more apropiate name(*photo*)
        // TODO: this is obsolete
        log.debug("image path: {}", user.getProfileImage());
        final SimpleUser existingUser = simpleUserRepository.findByUsername(user.getUsername());
        String imagePath = user.getProfileImage();
        existingUser.setProfileImage(imagePath);
        simpleUserRepository.save(existingUser);
        return ResponseEntity.ok(true);
    }

    /**
     * This is the new version. Turn other old version paths into this template.
     * @param user
     * @return
     */

    @PostMapping(value = "/user/update/groupAdmin")
    public ResponseEntity<Boolean> makeGroupAdmin(@RequestBody BasicUserGroupAdmin user) {
        log.debug("POST request at /api/user/update/groupAdmin");

        final SimpleUser existingUser = simpleUserRepository.findByUsername(user.getUsername());
        existingUser.setGroupAdmin(user.getIsAdmin());
        simpleUserRepository.save(existingUser);
        return ResponseEntity.ok(true);
    }

    /**
     *  isAdmin field to RequestBody.
     *
     * @param user BasicUserGroupAdmin
     * @return
     */

    @PostMapping(value = "makeUserGroupAdmin")
    public ResponseEntity<Boolean> makeUserGroupAdmin(@RequestBody BasicUser user) {
        final SimpleUser existingUser = simpleUserRepository.findByUsername(user.getUsername());
        existingUser.setGroupAdmin(true);
        simpleUserRepository.save(existingUser);
        return ResponseEntity.ok(true);
    }


    @PostMapping(value = "makeUserSuperAdmin")
    public ResponseEntity<Boolean> makeUserSuperAdmin(@RequestBody BasicUser user) {
        final SimpleUser existingUser = simpleUserRepository.findByUsername(user.getUsername());
        existingUser.setSuperAdmin(true);
        simpleUserRepository.save(existingUser);
        return ResponseEntity.ok(true);
    }



    @GetMapping(value = "users/all")
    public ResponseEntity<List<SimpleUser>> getAllUsers() {
        return ResponseEntity.ok(simpleUserRepository.findAll());
    }


    @GetMapping(value = "removeUserFromSystem/{username}")
    public ResponseEntity<Boolean> removeUserFromSystem(@PathVariable String username) {
        simpleUserRepository.deleteByUsername(username);
        log.debug("remove");
        return ResponseEntity.ok(true);
    }


    @GetMapping(value = "remove/{groupName}.{username}")
    public ResponseEntity<List<SimpleUser>> removeFromGroup(@PathVariable String groupName, @PathVariable String username) {
        SimpleUser simpleUser = simpleUserRepository.findByUsername(username);
        simpleUser.getGroups().remove(simpleUser.getGroups().stream()
                .filter(group -> group.getName().equals(groupName))
                .findAny()
                .orElse(null));
        simpleUserRepository.save(simpleUser);
        return ResponseEntity.ok(userService.findUsersByGroup(groupName));
    }


    @PostMapping(value = "groups/add")
    public ResponseEntity<List<SimpleUser>> addToGroup(@RequestBody BasicUserGroup user) {
        SimpleUser simpleUser = new SimpleUser(user.getUsername());
        simpleUser.addGroup(new Group(user.getGroupName()));
        if (!userService.createUser(simpleUser)) {
            userService.addUserToGroup(user);
        }
        return ResponseEntity.ok(userService.findUsersByGroup(user.getGroupName()));
    }


    @PostMapping(value = "group/channel/add")  // todo: partially tested(no user, group or channel and one more tested only)
    public ResponseEntity<List<SimpleUser>> addToChannel(@RequestBody BasicUserChannel user) {
        log.debug("POST at /api/group/channel/add");
        SimpleUser simpleUser =
                Optional.ofNullable(simpleUserRepository.findByUsername(user.getUsername()))
                        .orElseGet(() -> {
                    final SimpleUser templateUser = userService.createTemplateUser();
                    templateUser.setUsername(user.getUsername());
                    return templateUser;
                });
        Group group = groupRepository.findByName(user.getGroupName());
        if (group != null) {
            log.debug("group does exist");
            Group finalGroup = group;
            if(simpleUser.getGroups().stream().noneMatch(group1 -> group1.getName().equals(finalGroup.getName()))) {
                simpleUser.addGroup(group);
            }
            Channel channel = channelService.findByGroupNameAndName(user.getGroupName(), user.getChannelName());
            if (channel == null){
                Channel channel1 = new Channel(user.getChannelName());
                group.addChannel(new Channel(user.getChannelName()));
                simpleUser.addChannel(channel1);
                log.debug("adding channel");
            } else {
                simpleUser.addChannel(channel);
            }
        } else {
            group = new Group(user.getGroupName());
            Channel channel = new Channel(user.getChannelName());
            group.addChannel(channel);
            simpleUser.addGroup(group);
            userService.addUserToChannel(simpleUser, channel, user.getGroupName());
            log.debug("adding group");
        }
        simpleUserRepository.save(simpleUser);

        return ResponseEntity.ok(userService.findUsersByGroup(user.getGroupName()));
    }


    @PostMapping(value = "removeUserFromChannel")
    public ResponseEntity<List<SimpleUser>> removeUserFromChannel(@RequestBody BasicUserChannel basicUserChannel) {
        log.debug("DELETE at /api/remove/{}.{}.{}"
                , basicUserChannel.getGroupName()
                , basicUserChannel.getChannelName()
                , basicUserChannel.getUsername());
        final SimpleUser user = simpleUserRepository.findByUsername(basicUserChannel.getUsername());
        userService.removeUserFromChannel(user, basicUserChannel.getChannelName(), basicUserChannel.getGroupName());
        return ResponseEntity.ok(userService.findUsersByChannel(
                basicUserChannel.getGroupName()
                , basicUserChannel.getChannelName()));
    }
}

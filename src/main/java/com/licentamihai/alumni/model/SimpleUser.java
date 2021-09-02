package com.licentamihai.alumni.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.licentamihai.alumni.annotation.CascadeSave;
import com.licentamihai.alumni.service.UserService;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.Binary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Document(collection="users")
public class SimpleUser {
    @Id
    @JsonIgnore
    public String id;

    private String username;
    private String password;
    private String email;
    private String firstName;
    private String lastName;
    private Boolean superAdmin;
    private Boolean groupAdmin;
    private String profileImage;
    private Binary image;
    @DBRef
    @CascadeSave
    private List<Group> groups;
    @DBRef
    private List<Channel> channels;


    /**
     * Creates a simple user instance.
     * @param args The users fields in order, any number of them.
     *             Useful for user creation, even when some fields are unknown.
     */
    public SimpleUser(String... args) {
        int cnt = args.length;
        int maxFields = 5;
        int it = 0;
        if (cnt > maxFields || cnt < 1)
            return;
        while(it < cnt) {
            switch (it) {
                case 0:
                    this.username = args[0]; break;
                case 1:
                    this.password = args[1]; break;
                case 2:
                    this.email = args[2]; break;
                case 3:
                    this.firstName = args[3]; break;
                case 4:
                    this.lastName = args[4]; break;
            }
            it++;
        }
    }

    public void addGroup(Group group) {
        if (groups == null) {
            groups = new ArrayList<>();
        }
        this.groups.add(group);
    }

    /**
     * This function doesn't verify if the channel is a duplicate.
     * It simply adds an channel object to the user.
     * @param channel The user is now member of this channel.
     */
    public void addChannel(Channel channel) {
        if (channels == null)
            channels  = new ArrayList<>();
        channels.add(channel);
    }

    public String myString() {
        return String.format(
                "SimpleUser[id=%s, firstName='%s', lastName='%s', username='%s', password='%s', groups='%s']"
                        + "\nwhere groups:\n"
                        + (groups != null ? groups.stream()
                            .map(group -> group.toJson())
                            .reduce("", (groupsJson, groupJson) -> groupsJson + groupJson): null)
                        + "\n",
                id, firstName, lastName, username, password, groups);
    }

    @Override
    public boolean equals(Object obj) {  // TODO: there can be multiple accounts for the same person
        // self check
        if (this == obj)
            return true;
        // null check
        if (obj == null)
            return false;
        // type check and cast
        if (getClass() != obj.getClass())
            return false;
        SimpleUser person = (SimpleUser) obj;
        // field comparison TODO
        return  (username != null && person.getUsername() != null && username.equals(person.getUsername()));
    }

    @Override
    public int hashCode() {
        return username.hashCode();
    }
}

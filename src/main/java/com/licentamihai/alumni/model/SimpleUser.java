package com.licentamihai.alumni.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
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
    public String id;

    private String username;
    private String password;
    private String email;
    private String firstName;
    private String lastName;
    private Boolean superAdmin;
    private Boolean groupAdmin;
    private String profileImage;
    @DBRef
    private List<Group> groups;

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
                    this.password = args[2]; break;
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

    @Override
    public String toString() {
        return String.format(
                "SimpleUser[id=%s, firstName='%s', lastName='%s', username='%s', password='%s']",
                id, firstName, lastName, username, password);
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
        // field comparison
        return firstName.equals(person.firstName)
                && lastName.equals(person.lastName) && username.equals(person.getUsername());
    }

    @Override
    public int hashCode() {
        return firstName.hashCode() + lastName.hashCode() + username.hashCode();
    }
}

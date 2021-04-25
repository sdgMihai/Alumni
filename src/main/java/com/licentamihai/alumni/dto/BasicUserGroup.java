package com.licentamihai.alumni.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BasicUserGroup extends BasicUser {
    private String groupName;

    public BasicUserGroup(String username, String group) {
        super(username);
        this.groupName = group;
    }
}

package com.licentamihai.alumni.dto;

import lombok.*;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class BasicUserGroup extends BasicUser {
    private String groupName;

    public BasicUserGroup(String username, String group) {
        super(username);
        this.groupName = group;
    }
}

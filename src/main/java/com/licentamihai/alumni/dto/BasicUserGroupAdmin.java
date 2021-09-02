package com.licentamihai.alumni.dto;

import lombok.*;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class BasicUserGroupAdmin extends BasicUser {
    private Boolean isAdmin;

    public BasicUserGroupAdmin(String username, Boolean isAdmin) {
        super(username);
        this.isAdmin = isAdmin;
    }
}

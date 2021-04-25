package com.licentamihai.alumni.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BasicUserPhoto  extends BasicUser {
    private String path;

    public BasicUserPhoto(String username, String path) {
        super(username);
        this.path = path;
    }
}

package com.licentamihai.alumni.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BasicUser {
    protected String username;

    public BasicUser(String username) {
        this.username = username;
    }
}

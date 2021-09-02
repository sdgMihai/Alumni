package com.licentamihai.alumni.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BasicUser {
    protected String username;

    public BasicUser(String username) {
        this.username = username;
    }
}

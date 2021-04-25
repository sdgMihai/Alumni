package com.licentamihai.alumni.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BasicUserEmail extends BasicUser {
    private String email;

    public BasicUserEmail(String username, String email) {
        super(username);
        this.email = email;
    }

    @Override
    public String toString() {
        return "simpleUserEmail u: " + username + " email: " + email + "\n";
    }
}

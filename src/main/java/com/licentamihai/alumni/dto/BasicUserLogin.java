package com.licentamihai.alumni.dto;

import lombok.*;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class BasicUserLogin extends BasicUser {
    private String password;

    public BasicUserLogin(String username, String password) {
        super(username);
        this.password = password;
    }

    @Override
    public String toString() {
        return "username: " + username + ", password: " + password + '\n';
    }
}

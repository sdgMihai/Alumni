package com.licentamihai.alumni.dto;

import lombok.*;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class BasicUserEmail extends BasicUser {
    private String email;

    @Override
    public String toString() {
        return "simpleUserEmail u: " + username + " email: " + email + "\n";
    }
}

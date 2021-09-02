package com.licentamihai.alumni.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class BasicUserPhoto  extends BasicUser {
    String profileImage;

//    public BasicUserPhoto(String username, String path) {
//        super(username);
//        this.path = path;
//    }
}

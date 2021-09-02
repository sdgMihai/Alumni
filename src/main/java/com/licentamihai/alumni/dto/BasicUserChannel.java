package com.licentamihai.alumni.dto;

import lombok.*;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class BasicUserChannel extends BasicUserGroup{
    private String channelName;

    public BasicUserChannel(String username, String group, String channelName) {
        super(username, group);
        this.channelName = channelName;
    }
}

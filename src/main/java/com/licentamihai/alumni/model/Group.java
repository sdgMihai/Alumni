package com.licentamihai.alumni.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.licentamihai.alumni.annotation.CascadeDelete;
import com.licentamihai.alumni.annotation.CascadeSave;
import com.licentamihai.alumni.dto.GroupDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
@Setter
@Document(collection="groups")
public class Group {
    @Id
    @JsonIgnore
    public String id;

    private String name;
    @DBRef
    @CascadeSave
    @CascadeDelete
    private List<Channel> channels;

    public Group() {
        this.channels = new ArrayList<>();
        this.channels.add(new Channel("general"));
    }

    public Group(String name) {
        this();
        this.name = name;
    }

    public void addChannel(Channel channel) {
        if (channels.stream().noneMatch(channel1 -> channel1.getName().equals(channel.getName())))
            channels.add(channel);
    }

    public String toJson() {
        return String.format(
                "group: {id: %s, name: %s, channels: ["
                        + channels.stream()
                        .map(channel -> "channel: " + channel.toString() + ", ")
                        .reduce("", (channelsJson, channelJson) -> channelsJson + channelJson)+
                "]\n}", id, name
        );
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        // self check
        if (this == obj)
            return true;
        // null check
        if (obj == null)
            return false;
        // type check and cast
        if (getClass() != obj.getClass())
            return false;
        Group group = (Group) obj;
        // field comparison
        /*
            TODO: there can be different schools with the same name in different cities, add cities name to comparison
         */
        return name.equals(group.getName());
    }
}

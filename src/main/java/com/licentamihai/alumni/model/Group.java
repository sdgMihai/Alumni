package com.licentamihai.alumni.model;

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
    public String id;

    private String name;
    @DBRef
    private List<Channel> channels;

    public Group() {
        this.channels = new ArrayList<>();
        this.channels.add(new Channel("general"));
    }

    public Group(GroupDTO groupDTO) {
        // TODO: If this is not necessary maybe field channels of GroupDTO isn't either
        this();
        this.name = groupDTO.getName();

        groupDTO.getChannels().forEach(
                channelName -> {
                    this.channels.add(new Channel(channelName));
                }
        );
    }

    public Group(String name) {
        this();
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}

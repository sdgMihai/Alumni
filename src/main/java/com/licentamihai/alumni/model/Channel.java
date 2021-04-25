package com.licentamihai.alumni.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document(collection="channels")
public class Channel {
    @Id
    public String id;

    private String name;

    public Channel(String name) {
        this.name = name;
    }
}

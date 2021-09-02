package com.licentamihai.alumni.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Document(collection="channels")
public class Channel {
    @Id
    @JsonIgnore
    public String id;

    private String name;

    public Channel(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return String.format("Channel[id=%s,name=%s]", id, name);
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
        Channel channel = (Channel) obj;
        // field comparison
        return id.equals(channel.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}

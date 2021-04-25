package com.licentamihai.alumni.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GroupDTO {
    private String name;
    private List<String> channels;  // TODO : not sure is necessary

    @Override
    public String toString() {
        return name;
    }
}

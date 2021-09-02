package com.licentamihai.alumni.dto;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Data
@NoArgsConstructor
public class GroupDTO {
    private String name;
    private List<String> channels;

    @Override
    public String toString() {
        return name;
    }
}

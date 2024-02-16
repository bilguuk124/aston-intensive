package com.aston.dz2.entity.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentDto {
    private String name;
    private String shortName;
    private String description;

}

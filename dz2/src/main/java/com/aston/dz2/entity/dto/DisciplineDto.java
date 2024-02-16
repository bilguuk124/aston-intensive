package com.aston.dz2.entity.dto;

import lombok.*;

@Builder
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class DisciplineDto {
    private String name;
    private Long lecturer_id;
    private Long assistant_id;
    private Integer semester;
}

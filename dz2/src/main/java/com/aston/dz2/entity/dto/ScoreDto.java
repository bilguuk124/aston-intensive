package com.aston.dz2.entity.dto;

import lombok.*;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ScoreDto {
    private Long studentId;
    private Integer score;
}

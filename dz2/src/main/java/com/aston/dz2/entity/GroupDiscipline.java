package com.aston.dz2.entity;

import lombok.*;

@Data
@Builder
@ToString
@AllArgsConstructor
public class GroupDiscipline {
    private Long groupId;
    private Long disciplineId;
}

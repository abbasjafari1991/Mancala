package com.bol.mancala.entity;


import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
@Document
@EqualsAndHashCode(of = "id")
public class Player {
    @Id
    private String id;
    private String name;
}

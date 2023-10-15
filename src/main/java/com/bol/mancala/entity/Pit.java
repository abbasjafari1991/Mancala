package com.bol.mancala.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Pit {

    @Id
    private Long id;
    private Integer amount;
    private Integer index;

}

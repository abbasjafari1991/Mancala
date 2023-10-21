package com.bol.mancala.entity;


import jakarta.annotation.Nonnull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PlayerBoard {


    @Nonnull
    @DocumentReference(lazy = true)
    private Player player;
    private Map<Integer, Pit> pits;
    private Store store;


}

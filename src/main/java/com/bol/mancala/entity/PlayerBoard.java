package com.bol.mancala.entity;


import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
    @Size(min = 2, max = 2)
    private Map<Integer, Pit> pits;
    @NotNull
    private Store store;


}

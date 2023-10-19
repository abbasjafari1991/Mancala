package com.bol.mancala.service.mapper;

import com.bol.mancala.entity.Pit;
import com.bol.mancala.service.dto.PitDTO;
import org.mapstruct.Mapper;

import java.util.Comparator;
import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface PitMapper {
    PitDTO toDto(Pit pit);

    default List<PitDTO> setPitToList(Set<Pit> pits) {
        return pits.stream().map(this::toDto).sorted(Comparator.comparing(PitDTO::getIndex)).toList();
    }
}

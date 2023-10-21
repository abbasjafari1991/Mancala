package com.bol.mancala.repository;

import com.bol.mancala.entity.Board;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BoardRepository extends MongoRepository<Board, String> {
}

package com.github.J1372.WebBackend.Repositories;

import com.github.J1372.WebBackend.Entities.CompletedGame;
import org.springframework.data.repository.CrudRepository;

public interface GameRepository extends CrudRepository<CompletedGame, Integer> {

}

package com.github.J1372.WebBackend.Repositories;

import com.github.J1372.WebBackend.Entities.OpenGame;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OpenGameRepository extends JpaRepository<OpenGame, UUID> {


}

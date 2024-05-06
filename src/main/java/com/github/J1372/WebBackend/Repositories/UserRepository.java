package com.github.J1372.WebBackend.Repositories;

import com.github.J1372.WebBackend.Entities.CompletedGame;
import com.github.J1372.WebBackend.Entities.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepository extends CrudRepository<User, Integer>, CustomUserRepository {

    User findByUsername(String username);
    boolean existsByUsername(String username);
    void deleteByUsername(String username);

}


interface CustomUserRepository {
    List<CompletedGame> findRecentGames(@Param("username") String username, @Param("amount") int amount);
}

class CustomUserRepositoryImpl implements CustomUserRepository {

    @PersistenceContext
    EntityManager entityManager;

    @Override
    public List<CompletedGame> findRecentGames(String username, int amount) {
        String queryString =
                "select g" +
                " from User user" +
                " inner join user.pastGames as g" +
                " where user.username = :username" +
                " order by g.ended desc";


        return entityManager.createQuery(queryString, CompletedGame.class)
                .setParameter("username", username)
                .setMaxResults(amount)
                .getResultList();
    }
}
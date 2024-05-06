package com.github.J1372.WebBackend.Repositories;

import com.github.J1372.WebBackend.Dto.UsersHistoryPerspective;
import com.github.J1372.WebBackend.Dto.UserStats;
import com.github.J1372.WebBackend.Entities.UsersHistory;
import com.github.J1372.WebBackend.Entities.UsersHistoryId;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.hibernate.Session;
import org.springframework.data.repository.CrudRepository;

public interface UsersHistoryRepository extends CrudRepository<UsersHistory, UsersHistoryId>, CustomizedUsersHistoryRepository {

}

interface CustomizedUsersHistoryRepository {
    UserStats findHistoryWith(String user1, String user2);
}

class CustomizedUsersHistoryRepositoryImpl implements CustomizedUsersHistoryRepository {

    @PersistenceContext
    EntityManager entityManager;

    @Override
    public UserStats findHistoryWith(String user1, String user2) {
        Session session = entityManager.unwrap(Session.class);

        UsersHistoryPerspective res = session.createSelectionQuery(
                "select new com.github.J1372.WebBackend.Dto.UsersHistoryPerspective(a.username, h.user1Wins, h.draws, h.user2Wins)" +
                " from UsersHistory h" +
                " inner join h.a as a" +
                " inner join h.b as b" +
                " where a.username = :user1 and b.username = :user2 or" +
                " a.username = :user2 and b.username = :user1", UsersHistoryPerspective.class)
                .setParameter("user1", user1)
                .setParameter("user2", user2)
                .getSingleResultOrNull();

        if (res != null) {
            UserStats dbStats = res.getStats();
            if (res.getDbPerspective().equals(user1)) {
                return dbStats;
            } else {
                // Return swapped wins and losses.
                return new UserStats(dbStats.getLosses(), dbStats.getDraws(), dbStats.getWins());
            }
        } else {
            return null;
        }
    }
}

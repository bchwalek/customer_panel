package pl.lightbulb.customer_panel;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SessionRepository extends JpaRepository<Session, Long> {

    List<Session> findByUser(User user);
    Optional<Session> findById(Long id);
}

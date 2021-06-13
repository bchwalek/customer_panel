package pl.lightbulb.customer_panel.session;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.lightbulb.customer_panel.user.User;

import java.util.List;
import java.util.Optional;

public interface SessionRepository extends JpaRepository<Session, Long> {

    List<Session> findByUser(User user);
    Optional<Session> findById(Long id);
}

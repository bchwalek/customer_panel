package pl.lightbulb.customer_panel.user;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.lightbulb.customer_panel.user.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByLogin(String login);

    List<User> findByRole(String role);
    Optional<User> findById(Long id);

    User findBySheetName(String sheetName);
}
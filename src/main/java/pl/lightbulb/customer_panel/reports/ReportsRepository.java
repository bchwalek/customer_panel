package pl.lightbulb.customer_panel.reports;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.lightbulb.customer_panel.User;

import java.util.List;

public interface ReportsRepository extends JpaRepository<Reports, Long> {
    List<Reports> findByUser(User user);
}

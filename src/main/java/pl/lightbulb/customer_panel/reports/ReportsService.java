package pl.lightbulb.customer_panel.reports;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.lightbulb.customer_panel.user.User;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReportsService {

 private final ReportsRepository reportsRepository;

    public void add(Reports reports){
        reportsRepository.save(reports);
    }

    public List<Reports> userReports(User user){
        return reportsRepository.findByUser(user);
    }

    public Optional<Reports> findById(Long id) {
        return reportsRepository.findById(id);
    }

    public void delete(Reports reports) {
        reportsRepository.delete(reports);
    }

    public void deleteAll(List<Reports> reports) {
        reportsRepository.deleteAll(reports);
    }
}

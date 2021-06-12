package pl.lightbulb.customer_panel;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class SessionService {
    private final SessionRepository sessionRepository;

    public void add(Session session){
        sessionRepository.save(session);
    }

    public List<Session> userSession(User user){
        return sessionRepository.findByUser(user);
    }

    public Optional<Session> findById(Long id) {
        return sessionRepository.findById(id);
    }

    public void delete(Session session) {
        sessionRepository.delete(session);
    }
    public void deleteAll (List<Session> sessions){
        sessionRepository.deleteAll(sessions);
    }
}

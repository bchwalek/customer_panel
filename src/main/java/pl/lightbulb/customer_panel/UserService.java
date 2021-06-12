package pl.lightbulb.customer_panel;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Transactional
@Service
public class UserService {

    private final UserRepository userRepository;

    public void addUser(User user){
        userRepository.save(user);
    }

    public void delete(User user) { userRepository.delete(user);}

    public User findByLogin(String login){
        return userRepository.findByLogin(login);
    }

    public List<User> findByRole(String role){
        return userRepository.findByRole(role);
    }

    public Optional<User> findById(Long id){
        return userRepository.findById(id);
    }

    public User findByRange(String range) {
       return userRepository.findBySheetName(range);
    }
}
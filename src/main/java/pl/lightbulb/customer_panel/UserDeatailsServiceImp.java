package pl.lightbulb.customer_panel;

import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pl.lightbulb.customer_panel.user.UserService;

@Service
@AllArgsConstructor
public class UserDeatailsServiceImp implements UserDetailsService {

    private final UserService userService;


    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        return userService.findByLogin(s);
    }
}

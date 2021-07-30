package pl.lightbulb.customer_panel;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import pl.lightbulb.customer_panel.user.User;

@Controller
@RequiredArgsConstructor
public class AppController {

    @GetMapping("/")
    public String index() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }


    @PostMapping("/login")
    public String loggedUser(@AuthenticationPrincipal User user) {
        String role = user.getRole();

        if (role.equals("ROLE_ADMIN")) {
            return "redirect:/showcustomer";
        }
        return "redirect:/customer/" + user.getId();
    }

}

package pl.lightbulb.customer_panel.session;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pl.lightbulb.customer_panel.photo.Photo;
import pl.lightbulb.customer_panel.photo.PhotoService;
import pl.lightbulb.customer_panel.user.UserService;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Secured("ROLE_ADMIN")
@RequiredArgsConstructor
@Controller
public class SessionController {

    private final UserService userService;
    private final SessionService sessionService;
    private final PhotoService photoService;
    
    @GetMapping("/sessioncreate/{id}")
    public String photoUploadForm(@PathVariable Long id, Model model) {
        model.addAttribute("session", new Session());
        model.addAttribute("customerId", id);
        return "photosessioncreate";
    }

    @Transactional
    @PostMapping("/sessioncreate")
    public String photoUploaded(Session session,
                                @ModelAttribute("customerId") Long id,
                                BindingResult bindingResult,
                                @RequestParam("document") List<MultipartFile> multipartFiles) throws IOException {

        session.setUser(userService.findById(id).get());

        sessionService.add(session);

        sessionService.addPhotos(multipartFiles, session);

        return "redirect:/customer/"+id;
    }

    @PostMapping("/updatesession")
    public String sessionUpdate(@RequestParam("sessionId") String id, @RequestParam("document") List<MultipartFile> multipartFiles) throws IOException {

        Session session = sessionService.findById(Long.parseLong(id)).get();

        sessionService.addPhotos(multipartFiles, session);

        return "redirect:/session/"+id;
    }

    @Secured({"ROLE_CUSTOMER", "ROLE_ADMIN"})
    @GetMapping("/session/{id}")
    public String photoSession(@PathVariable("id") Long id, Model model) {

        Session session = sessionService.findById(id).get();
        model.addAttribute("sessionObject", session);
        model.addAttribute("photoSessions", photoService.findBySessionId(id));

        return "gallery";
    }


    @GetMapping("/deletesession/{id}")
    public String deleteSession(@PathVariable("id") Long id){

        photoService.deleteAll(photoService.findBySessionId(id));

        sessionService.delete(sessionService.findById(id).get());

        return "redirect:/showcustomer";
    }
}

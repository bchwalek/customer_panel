package pl.lightbulb.customer_panel.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import pl.lightbulb.customer_panel.apisheets.SheetsIdService;
import pl.lightbulb.customer_panel.photo.Photo;
import pl.lightbulb.customer_panel.photo.PhotoService;
import pl.lightbulb.customer_panel.reports.Reports;
import pl.lightbulb.customer_panel.reports.ReportsService;
import pl.lightbulb.customer_panel.session.Session;
import pl.lightbulb.customer_panel.session.SessionService;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Controller
@RequiredArgsConstructor
@Secured("ROLE_ADMIN")
public class UserController {

    private final UserService userService;
    private final PhotoService photoService;
    private final SessionService sessionService;
    private final ReportsService reportsService;
    private final SheetsIdService sheetsIdService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/showcustomer")
    public String showallcustomer(Model model) {

        model.addAttribute("sheetsId", sheetsIdService.get(1L).get().getSpreadsheetId());
        model.addAttribute("customers", userService.findByRole("ROLE_CUSTOMER"));

        return "showcustomer";
    }

    @GetMapping("/showadmin")
    public String showalladmin(Model model) {
        model.addAttribute("customers", userService.findByRole("ROLE_ADMIN"));
        return "showadmin";
    }

    @GetMapping("/adduser")
    public String addUserForm(Model model) {
        model.addAttribute("User", new User());
        return "adduserform";
    }

    @Transactional
    @PostMapping("/adduser")
    public String addUser(User user, @RequestParam("document") MultipartFile multipartFile, BindingResult bindingResult) throws IOException {

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setIsDeactivate(false);

        userService.addUser(user);

        if (user.getRole().equals("ROLE_CUSTOMER")) {

            Photo photo = new Photo();
            String fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
            photo.setName(fileName);
            photo.setContent(multipartFile.getBytes());
            photoService.add(photo);

            user.setPhoto(photo);

            userService.addUser(user);

            return "redirect:/showcustomer";
        }
        return "redirect:/showadmin";
    }

    @GetMapping("/updateuser/{id}")
    public String updateUserForm(Model model, @PathVariable Long id) {
        model.addAttribute("userUpdate", userService.findById(id).get());
        return "updateuserform";
    }

    @Transactional
    @PostMapping("/updateuser")
    public String updateUser(User user, @RequestParam("document") MultipartFile multipartFile, BindingResult bindingResult) throws IOException {

        if (user.getPassword().isEmpty()) {
            user.setPassword(userService.findById(user.getId()).get().getPassword());
        } else user.setPassword(passwordEncoder.encode(user.getPassword()));

        if (multipartFile.isEmpty()){
            user.setPhoto(userService.findById(user.getId()).get().getPhoto());
        } else {

            Photo photo = userService.findById(user.getId()).get().getPhoto();
            String fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
            photo.setName(fileName);
            photo.setContent(multipartFile.getBytes());
            photoService.add(photo);

            user.setPhoto(photo);
        }

        userService.addUser(user);

        if (user.getRole().equals("ROLE_CUSTOMER")) {
            return "redirect:/showcustomer";
        }
        return "redirect:/showadmin";
    }

    @GetMapping("/deactivate/{id}")
    public String deactivate(@PathVariable Long id) {
        User user = userService.findById(id).get();
        user.setIsDeactivate(true);
        userService.addUser(user);

        if (user.getRole().equals("ROLE_CUSTOMER")) {
            return "redirect:/showcustomer";
        }
        return "redirect:/showadmin";
    }


    @GetMapping("/activate/{id}")
    public String activate(@PathVariable Long id) {

        User user = userService.findById(id).get();
        user.setIsDeactivate(false);
        userService.addUser(user);

        if (user.getRole().equals("ROLE_CUSTOMER")) {
            return "redirect:/showcustomer";
        }
        return "redirect:/showadmin";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {

        User user = userService.findById(id).get();

        if(user.getPhoto()!=null){
            photoService.delete(user.getPhoto());}

        List<Session> sessions =  sessionService.userSession(user);

        for (Session session:sessions) {
            photoService.deleteAll(photoService.findBySession(session));
        }
        sessionService.deleteAll(sessions);

        List<Reports> reports= reportsService.userReports(user);
        reportsService.deleteAll(reports);

        userService.delete(user);
        return "redirect:/showcustomer";
    }

    @Secured({"ROLE_CUSTOMER", "ROLE_ADMIN"})
    @GetMapping("customer/{id}")
    public String customerView(@PathVariable Long id, Model model, @AuthenticationPrincipal User userlogged) {

//        if(!userlogged.getId().equals(id)||!userlogged.getRole().equals("ROLE_ADMIN")){
//            return "redirect:/login";
//        }

        User user = userService.findById(id).get();


        model.addAttribute("customerId", id);
        model.addAttribute("sheetName", user.getSheetName());


        return "customerview";
    }

    @Secured({"ROLE_CUSTOMER", "ROLE_ADMIN"})
    @GetMapping("customer/reports/{id}")
    public String customerReports(@PathVariable Long id, Model model) {


        User user = userService.findById(id).get();
        List<Reports> reports = reportsService.userReports(user);
        model.addAttribute("userReports", reports);


        return "reportsview";
    }

    @Secured({"ROLE_CUSTOMER", "ROLE_ADMIN"})
    @GetMapping("customer/photos/{id}")
    public String customerPhotoSession(@PathVariable Long id, Model model) {


        User user = userService.findById(id).get();
        List<Session> sessions = sessionService.userSession(user);
        model.addAttribute("photoSessions", sessions);

        return "photoSessionView";
    }
    @Secured({"ROLE_CUSTOMER", "ROLE_ADMIN"})
    @GetMapping("/changepassword")
    public String changePasswordForm(){
        return "changepassword";
    }

    @Secured({"ROLE_CUSTOMER", "ROLE_ADMIN"})
    @PostMapping("/changepassword")
    public String changePassword(@RequestParam("oldPass") String oldPass,
                                 @RequestParam("newPass") String newPass,
                                 @AuthenticationPrincipal User loggedUser){

        User user = userService.findById(loggedUser.getId()).get();


        System.out.println(user.getPassword());



        if(passwordEncoder.matches(oldPass, user.getPassword())){
            user.setPassword(passwordEncoder.encode(newPass));
            userService.addUser(user);
        }

        return "redirect:/customer/"+loggedUser.getId();
    }
}

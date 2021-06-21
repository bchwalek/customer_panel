package pl.lightbulb.customer_panel;

import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pl.lightbulb.customer_panel.photo.Photo;
import pl.lightbulb.customer_panel.photo.PhotoService;
import pl.lightbulb.customer_panel.reports.Reports;
import pl.lightbulb.customer_panel.reports.ReportsService;
import pl.lightbulb.customer_panel.session.Session;
import pl.lightbulb.customer_panel.session.SessionService;
import pl.lightbulb.customer_panel.user.User;
import pl.lightbulb.customer_panel.user.UserService;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Controller
@RequiredArgsConstructor
public class AppController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final PhotoService photoService;
    private final SessionService sessionService;
    private final ReportsService reportsService;


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

    @GetMapping("/showcustomer")
    public String showallcustomer(Model model) {
        model.addAttribute("customers", userService.findByRole("ROLE_CUSTOMER"));

        return "showcustomer";
    }

    @GetMapping("/showadmin")
    public String showalladmin(Model model) {
        model.addAttribute("customers", userService.findByRole("ROLE_ADMIN"));
        return "showadmin";
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

    @GetMapping("/photoupload/{id}")
    public String photoUploadForm(@PathVariable Long id, Model model) {
        model.addAttribute("session", new Session());
        model.addAttribute("customerId", id);
        return "photosessioncreate";
    }

//    @GetMapping("/photoupload/photo")
//    public ModelAndView photoadd(){
//        ModelAndView modelAndView = new ModelAndView("photosessioncreate");
//        modelAndView.addObject("session", new Session());
//        return modelAndView;
//    }

    @Transactional
    @PostMapping("/photoupload")
    public String photoUploaded(Session session,
                                @ModelAttribute("customerId") Long id,
                                BindingResult bindingResult,
                                @RequestParam("document") List<MultipartFile> multipartFiles) throws IOException {

        session.setUser(userService.findById(id).get());

        sessionService.add(session);

        for (MultipartFile multipartFile : multipartFiles) {
            String fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
            Photo photo = new Photo();
            photo.setName(fileName);
            photo.setContent(multipartFile.getBytes());
            photo.setSession(session);
            photoService.add(photo);
        }
        return "redirect:/customer/"+id;
    }

    @PostMapping("updatesession")
    public String sessionUpdate(@RequestParam("sessionId") String id, @RequestParam("document") List<MultipartFile> multipartFiles) throws IOException {

       Session session = sessionService.findById(Long.parseLong(id)).get();

        for (MultipartFile multipartFile : multipartFiles) {
            String fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
            Photo photo = new Photo();
            photo.setName(fileName);
            photo.setContent(multipartFile.getBytes());
            photo.setSession(session);
            photoService.add(photo);
        }

        return "redirect:/session/"+id;
    }


    @GetMapping("/image/{id}")
    public void showImage(@PathVariable String id,
                                 HttpServletResponse response) throws IOException {
        response.setContentType("image/jpeg");

//        Photo photo = photoService.findById(Long.parseLong(id)).get();
        Photo photo = photoService.findById(Long.parseLong(id)).get();
        InputStream is = new ByteArrayInputStream(photo.getContent());
        IOUtils.copy(is, response.getOutputStream());

    }

    @GetMapping("/userimage/{id}")
    public void showUserImage(@PathVariable Long id,
                                 HttpServletResponse response) throws IOException {
        response.setContentType("image/jpeg");

        User user=userService.findById(id).get();

        Photo photo = user.getPhoto();
        InputStream is = new ByteArrayInputStream(photo.getContent());
        IOUtils.copy(is, response.getOutputStream());
    }


    @Secured({"ROLE_CUSTOMER", "ROLE_ADMIN"})
    @GetMapping("customer/{id}")
    public String customerView(@PathVariable Long id, Model model, @AuthenticationPrincipal User userlogged) {

//        if(!userlogged.getId().equals(id)||!userlogged.getRole().equals("ROLE_ADMIN")){
//            return "redirect:/login";
//        }

        User user = userService.findById(id).get();
        List<Session> sessions = sessionService.userSession(user);
        List<Reports> reports = reportsService.userReports(user);


        model.addAttribute("photoSessions", sessions);
        model.addAttribute("userReports", reports);
        model.addAttribute("sheetName", user.getSheetName());


        return "customerview";
    }

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

    @GetMapping("/imagedelete/{id}")
    public String deleteImgae(@PathVariable("id") Long id){
      Photo photo =  photoService.findById(id).get();
        photoService.delete(photo);
        return "redirect:/session/"+photo.getSession().getId();
    }

    @GetMapping("/changepassword")
    public String changePasswordForm(){
        return "changepassword";
    }

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

package pl.lightbulb.customer_panel.photo;

import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import pl.lightbulb.customer_panel.user.User;
import pl.lightbulb.customer_panel.user.UserService;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;


@Secured("ROLE_ADMIN")
@Controller
@RequiredArgsConstructor
public class PhotoController {

    private final UserService userService;
    private final PhotoService photoService;


    @GetMapping("/image/{id}")
    public void showImageGallery(@PathVariable String id,
                          HttpServletResponse response) throws IOException {
        response.setContentType("image/jpeg");

        Photo photo = photoService.findById(Long.parseLong(id)).get();
        InputStream is = new ByteArrayInputStream(photo.getContent());
        IOUtils.copy(is, response.getOutputStream());

    }

    @GetMapping("/userimage/{id}")
    public void showUserSheetImage(@PathVariable Long id,
                              HttpServletResponse response) throws IOException {
        response.setContentType("image/jpeg");

        User user=userService.findById(id).get();

        Photo photo = user.getPhoto();
        InputStream is = new ByteArrayInputStream(photo.getContent());
        IOUtils.copy(is, response.getOutputStream());
    }

    @GetMapping("/imagedelete/{id}")
    public String deleteImage(@PathVariable("id") Long id){
        Photo photo =  photoService.findById(id).get();
        photoService.delete(photo);
        return "redirect:/session/"+photo.getSession().getId();
    }
}

package pl.lightbulb.customer_panel.session;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import pl.lightbulb.customer_panel.photo.Photo;
import pl.lightbulb.customer_panel.photo.PhotoService;
import pl.lightbulb.customer_panel.user.User;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class SessionService {

    private final SessionRepository sessionRepository;
    private final PhotoService photoService;

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

    public void addPhotos (List<MultipartFile> multipartFiles, Session session) throws IOException {
        for (MultipartFile multipartFile : multipartFiles) {
            String fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
            Photo photo = new Photo();
            photo.setName(fileName);
            photo.setContent(multipartFile.getBytes());
            photo.setSession(session);
            photoService.add(photo);
        }
    }
}

package pl.lightbulb.customer_panel;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;
import pl.lightbulb.customer_panel.PhotoRepository;
import pl.lightbulb.customer_panel.Session;
import pl.lightbulb.customer_panel.Photo;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PhotoService {

    private final PhotoRepository photoRepository;

    public void add(Photo photo){
        photoRepository.save(photo);
    }

    public List<Photo> findBySession (Session session){
       return photoRepository.findBySession(session);
    }


    public List<Photo> findBySessionId (Long id){
       return photoRepository.findPhotoBySessionId(id);
    }


    Optional<Photo> findById (Long id){
        return photoRepository.findById(id);
    }

    public void deleteAll(List<Photo> bySessionId) {
        photoRepository.deleteAll();
    }

    public void delete(Photo photo){
        photoRepository.delete(photo);
    }
}

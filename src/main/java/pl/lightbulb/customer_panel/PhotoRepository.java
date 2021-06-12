package pl.lightbulb.customer_panel;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PhotoRepository extends JpaRepository<Photo, Long> {

    Optional<Photo> findById(Long id);

    List<Photo> findBySession(Session session);


    @Query("SELECT p FROM Photo p WHERE p.session.id=?1")
    List<Photo> findPhotoBySessionId(Long id);


}

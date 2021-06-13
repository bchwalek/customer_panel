package pl.lightbulb.customer_panel.photo;

import lombok.Getter;
import lombok.Setter;
import pl.lightbulb.customer_panel.session.Session;

import javax.persistence.*;

@Getter
@Setter
@Entity
public class Photo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column (nullable = false, unique = true)
    private String name;

    @Lob
    private byte[] content;

    @ManyToOne
    private Session session;


}

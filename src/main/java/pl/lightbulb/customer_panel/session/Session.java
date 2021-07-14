package pl.lightbulb.customer_panel.session;

import lombok.Getter;
import lombok.Setter;
import pl.lightbulb.customer_panel.user.User;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name="sessions")
public class Session {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String url;

    @ManyToOne
    private User user;


}

package pl.lightbulb.customer_panel.reports;

import lombok.Getter;
import lombok.Setter;
import pl.lightbulb.customer_panel.User;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
public class Reports {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    private String reportName;

    private Long size;
    private Date uploadTime;

    @Lob
    private byte[] content;

    @ManyToOne
    private User user;
}

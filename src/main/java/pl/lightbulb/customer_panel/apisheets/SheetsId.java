package pl.lightbulb.customer_panel.apisheets;

import lombok.Getter;
import lombok.Setter;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Getter
@Setter
@Transactional
public class SheetsId {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String spreadsheetId;
}

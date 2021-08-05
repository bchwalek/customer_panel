package pl.lightbulb.customer_panel.apisheets;


import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SheetsIdRepository extends JpaRepository<SheetsId, Long> {


}

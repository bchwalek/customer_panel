package pl.lightbulb.customer_panel.apisheets;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.util.Optional;

@RequiredArgsConstructor
@Service
public class SheetsIdService {

    private final SheetsIdRepository sheetsRepository;

    public SheetsId update (SheetsId sheets) {
      return  sheetsRepository.save(sheets);
    }

    Optional<SheetsId> get(Long id){
        return sheetsRepository.findById(id);
    }
}

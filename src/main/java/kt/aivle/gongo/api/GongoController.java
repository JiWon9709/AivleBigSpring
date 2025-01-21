package kt.aivle.gongo.api;

import kt.aivle.gongo.model.Gongo;
import kt.aivle.gongo.service.GongoService;
import kt.aivle.member.model.UserException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/gongo")
@RequiredArgsConstructor
public class GongoController {
    private final GongoService gongoService;

    @GetMapping("/active")
    public ResponseEntity<?> getActiveGongos() {
        try {
            List<Gongo> activeGongos = gongoService.getActiveGongos();
            return ResponseEntity.ok()
                    .body(new UserException(200, "조회성공", activeGongos));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new UserException(400, e.getMessage()));
        }
    }
}

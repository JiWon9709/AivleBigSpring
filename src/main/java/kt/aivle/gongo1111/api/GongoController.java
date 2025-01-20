package kt.aivle.gongo1111.api;

import kt.aivle.gongo1111.model.dto.GongoDTO;
import kt.aivle.gongo1111.service.GongoService;
import kt.aivle.member.model.dto.ResponseDTO;
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
            List<GongoDTO> activeGongos = gongoService.getActiveGongos();
            return ResponseEntity.ok()
                    .body(new ResponseDTO(200, "조회성공", activeGongos));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ResponseDTO(400, e.getMessage()));
        }
    }
}

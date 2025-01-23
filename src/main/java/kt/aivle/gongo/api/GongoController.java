package kt.aivle.gongo.api;

import kt.aivle.base.BaseMsg;
import kt.aivle.base.BaseResModel;
import kt.aivle.gongo.model.Gongo;
import kt.aivle.gongo.service.GongoService;
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
    public ResponseEntity<BaseResModel<List<Gongo>>> getActiveGongos() {
        try {
            List<Gongo> activeGongos = gongoService.getActiveGongos();
            BaseResModel<List<Gongo>> response = new BaseResModel<>();
            response.setData(activeGongos);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            BaseResModel<List<Gongo>> response = new BaseResModel<>();
            response.setResultCode(BaseMsg.FAILED.getCode());
            response.setResultMsg(e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}

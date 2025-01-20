package kt.aivle.input.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import kt.aivle.input.model.DTO.InputRequestDTO;
import kt.aivle.input.service.InputService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Api(tags = "Input", description = "INPUT API")
@RestController
@RequestMapping("/api/input")
@RequiredArgsConstructor
public class InputController {

    private final InputService inputService;

    // React에서 데이터 수신
    @PostMapping
    public ResponseEntity<String> saveInput(@RequestBody InputRequestDTO inputRequestDTO) {
        try {
            inputService.saveInput(inputRequestDTO); // 데이터 저장
            return ResponseEntity.ok("데이터 저장 성공");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("데이터 저장 실패: " + e.getMessage());
        }
    }
}
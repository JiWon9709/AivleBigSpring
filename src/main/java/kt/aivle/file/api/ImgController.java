package kt.aivle.file.api;

import kt.aivle.file.model.Img;
import kt.aivle.file.service.ImgService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
public class ImgController {

    @Autowired
    private ImgService imgService;

    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("refTable") String refTable,
            @RequestParam("refSn") Integer refSn) {

        try {
            String uploadDir = "uploads/";
            String fileName = file.getOriginalFilename();
            String ext = fileName.substring(fileName.lastIndexOf('.') + 1);

            // 디렉터리 생성
            File directory = new File(uploadDir);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // 파일 저장
            Path filePath = Paths.get(uploadDir + fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            Img img = new Img();
            // DB 저장
            imgService.saveImage(refTable, refSn, filePath.toString(), ext);

            Map<String, String> response = new HashMap<>();
            response.put("url", filePath.toString().replace("\\", "/"));
//            response.put("imgSn", String.valueOf(img.getImgSn()));

            return ResponseEntity.ok(response);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}


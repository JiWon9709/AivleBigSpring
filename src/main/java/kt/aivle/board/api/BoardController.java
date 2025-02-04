package kt.aivle.board.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import kt.aivle.board.model.Board;
import kt.aivle.board.model.BoardListResponse;
import kt.aivle.board.model.BoardRequest;
import kt.aivle.board.service.BoardService;
import kt.aivle.file.service.ImgService;
import kt.aivle.member.service.JwtTokenProvider;
import kt.aivle.member.service.RefreshTokenService;
import kt.aivle.member.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Api(tags = "board", description = "게시판 api")
@RestController
@RequiredArgsConstructor
public class BoardController {
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;

    @Autowired
    private BoardService boardService;

    @Autowired
    private ImgService imgService;

    @ApiOperation(value="게시글 리스트")
    @GetMapping("/board")
    public BoardListResponse getListBoard(){
        BoardListResponse boardlist = new BoardListResponse();
        try {
            boardlist = boardService.getListBoard();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return boardlist;
    }

    @PostMapping("/post-board")
    public ResponseEntity<String> createPost(@RequestParam("title") String title,
                                             @RequestParam("content") String content,
                                             @RequestParam("userSn") int userSn,
                                             @RequestParam(value = "fileUrl", required = false) MultipartFile file) {
        Board b = new Board();
        try {
//            String uploadDir = "uploads/";
//            String fileName = file.getOriginalFilename();
//            String ext = fileName.substring(fileName.lastIndexOf('.') + 1);
//
//            // 디렉터리 생성
//            File directory = new File(uploadDir);
//            if (!directory.exists()) {
//                directory.mkdirs();
//            }
//
//            // 파일 저장
//            Path filePath = Paths.get(uploadDir + fileName);
//            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
//
//            // 파일 저장 로직
//            if (file != null && !file.isEmpty()) {
//                imgService.saveImage('board', refSn, filePath.toString(), ext);
//                System.out.println("Uploaded File Path: " + filePath);
//            }
            b.setUserSn(userSn);
            b.setTitle(title);
            b.setContent(content);
            boardService.savePost(b);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return ResponseEntity.ok("게시글이 등록되었습니다.");
    }
}

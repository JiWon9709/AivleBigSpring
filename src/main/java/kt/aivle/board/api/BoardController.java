package kt.aivle.board.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import kt.aivle.board.model.Board;
import kt.aivle.board.model.BoardListResponse;
import kt.aivle.board.model.BoardRequest;
import kt.aivle.board.model.GongoListResponse;
import kt.aivle.board.service.BoardService;
import kt.aivle.member.service.JwtTokenProvider;
import kt.aivle.member.service.RefreshTokenService;
import kt.aivle.member.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Api(tags = "board", description = "게시판 api")
@RestController
@RequiredArgsConstructor
public class BoardController {
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;

    @Autowired
    private BoardService boardService;

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

    @ApiOperation(value="공고 리스트")
    @GetMapping("/gongoboard")
    public GongoListResponse getListGongo(){
        GongoListResponse gongolist = new GongoListResponse();
        try {
            gongolist = boardService.getListGongo();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return gongolist;
    }

    @PostMapping("/post-board")
    public ResponseEntity<String> createPost(@RequestParam("title") String title,
                                             @RequestParam("content") String content,
                                             @RequestParam("userSn") int userSn,
                                             @RequestParam(value = "files", required = false) List<MultipartFile> files) {
        Board b = new Board();
        b.setUserSn(userSn);
        b.setTitle(title);
        b.setContent(content);
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
//
            // 게시글 저장
            boardService.savePost(b);
            // 이미지 저장 처리
            if (files != null && !files.isEmpty()) {
                for (MultipartFile file : files) {
                    if (!file.isEmpty()) {
                        boardService.uploadAndSaveImage("board", b.getBoardSn(), file);
                    }
                }
            }
        } catch (Exception e) {
//            throw new RuntimeException(e);
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("게시글 등록 중 오류가 발생했습니다.");
        }
        return ResponseEntity.ok("게시글이 등록되었습니다.");
    }
}

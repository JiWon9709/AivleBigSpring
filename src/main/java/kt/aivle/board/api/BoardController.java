package kt.aivle.board.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import kt.aivle.board.model.*;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

    @ApiOperation(value="게시글 이미지불러오기")
    @GetMapping("/board/images")
    public ResponseEntity<List<ImgEntity>> getImagesByBoardSn(@RequestParam("boardSn") int boardSn) {
//        try {
            List<ImgEntity> imgs = boardService.getImagesByBoardSn(boardSn);
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
        return ResponseEntity.ok(imgs);
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

    @ApiOperation(value="게시글 올리기")
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
            // 게시글 저장
            boardService.savePost(b);

            // 이미지 저장 처리
            if (files != null && !files.isEmpty()) {
                for (MultipartFile file : files) {
                    // 이미지 타입 검증
                    String contentType = file.getContentType();
                    if (contentType == null || !contentType.startsWith("image/")) {
                        return ResponseEntity.badRequest().body("이미지 파일만 업로드 가능합니다");
                    }

                    // 확장자 검증
                    String filename = file.getOriginalFilename();
                    String extension = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
                    Set<String> allowedExtensions = Set.of("jpg", "jpeg", "png", "gif");
                    if (!allowedExtensions.contains(extension)) {
                        return ResponseEntity.badRequest().body("허용되지 않는 파일 확장자입니다");
                    }

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

    @ApiOperation(value = "게시글 상세 조회")
    @GetMapping("/board/detail")
    public ResponseEntity<Map<String, Object>> getPostDetail(@RequestParam("boardSn") int boardSn, @RequestParam("userSn") int userSn) {
        try {
            Board post = boardService.getPostByBoardSn(boardSn);
//            List<ImgEntity> imgs = boardService.getImagesByBoardSn(boardSn);
            boolean isOwner = (post.getUserSn() == userSn); // 본인 여부 확인

            Map<String, Object> response = new HashMap<>();
            response.put("post", post);
            response.put("isOwner", isOwner);
//            response.put("imgs", imgs);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @ApiOperation(value = "게시글 소프트 삭제")
    @PutMapping("/board/{boardSn}")
    public ResponseEntity<String> softDeletePost(@PathVariable int boardSn, @RequestParam int userSn) {
        try {
            Board post = boardService.getPostByBoardSn(boardSn);
            if (post == null || post.getUserSn() != userSn) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("권한이 없습니다.");
            }
            boardService.softDeletePost(boardSn);
            // img 데이터도 삭제
            List<ImgEntity> imgs = boardService.getImagesByBoardSn(boardSn);
            if (!imgs.isEmpty()) {
                boardService.softDeleteImg(boardSn);
            }
            return ResponseEntity.ok("게시글이 삭제되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("게시글 삭제 중 오류가 발생했습니다.");
        }
    }

    @ApiOperation(value = "게시글 수정")
    @PostMapping("/board")
    public ResponseEntity<String> updatePost(@RequestParam(value = "boardSn", required = false) Integer boardSn,
                                             @RequestParam("title") String title,
                                             @RequestParam("content") String content,
                                             @RequestParam("userSn") int userSn,
                                             @RequestParam(value = "files", required = false) List<MultipartFile> files,
                                             @RequestParam(value = "deletedFileIds", required = false) List<Integer> deletedFileIds) {
        try {
            Board post = boardService.getPostByBoardSn(boardSn);
            List<ImgEntity> imgs = boardService.getImagesByBoardSn(boardSn);
            if (post == null || post.getUserSn() != userSn) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("권한이 없습니다.");
            }
            // 게시글 정보 설정 및 저장
            post.setTitle(title);
            post.setContent(content);
            post.setUserSn(userSn);
            boardService.updatePost(post);

            // 이미지 파일 삭제 처리
            if (deletedFileIds != null) {
                for (int imgId : deletedFileIds) {
                    boardService.softDeleteImg(imgId);
                }
            }

            // 이미지 파일 저장 처리
            if (files != null) {
                for (MultipartFile file : files) {
                    boardService.uploadAndSaveImage("board", post.getBoardSn(), file);
                }
            }
            return ResponseEntity.ok("게시글이 수정되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("게시글 수정 중 오류가 발생했습니다.");
        }
    }


}

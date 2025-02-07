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

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

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
            List<ImgEntity> imgs = boardService.getImagesByBoardSn(boardSn);
            boolean isOwner = (post.getUserSn() == userSn); // 본인 여부 확인

            Map<String, Object> response = new HashMap<>();
            response.put("post", post);
            response.put("isOwner", isOwner);
            response.put("imgs", imgs);

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
                    boardService.deleteImage(imgId);
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

//    @ApiOperation(value = "PDF 리스트 제공 API")
//    @GetMapping("/pdfs")
//    public ResponseEntity<List<String>> getPdfList() {
//        File pdfDirectory = new File("/path/to/uploads/pdf");
//        if (!pdfDirectory.exists() || !pdfDirectory.isDirectory()) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
//        }
//
//        // 디렉터리 내 PDF 파일 리스트 조회
//        List<String> pdfFiles = Arrays.stream(pdfDirectory.listFiles())
//                .filter(file -> file.isFile() && file.getName().endsWith(".pdf"))
//                .map(File::getName)
//                .collect(Collectors.toList());
//
//        return ResponseEntity.ok(pdfFiles);
//    }

//    @ApiOperation(value = "공고게시글 상세 조회")
//    @GetMapping("/gongoboard/detail")
//    public ResponseEntity<Map<String, Object>> getGongoDetail(@RequestParam("gongoSn") int gongoSn, @RequestParam("userSn") int userSn) {
//        try {
//            Gongo gongo = boardService.getPostByGongoSn(gongoSn);
//            List<PdfFileEntity> pdfs = boardService.getPdfsByBoardSn(gongoSn);
////            boolean isOwner = (gongo.getUserSn() == userSn); // 본인 여부 확인
//
//            Map<String, Object> response = new HashMap<>();
//            response.put("gongo", gongo);
////            response.put("isOwner", isOwner);
//            response.put("pdfs", pdfs);
//
//            return ResponseEntity.ok(response);
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
//        }
//    }

    @ApiOperation(value="공고게시글 올리기")
    @PostMapping("/post-gongoboard")
    public ResponseEntity<String> createGongo(@RequestParam("gongoName") String gongoName,
                                             @RequestParam("content") String content,
                                              @RequestParam("gongoType") int gongoType,
                                              @RequestParam("scheduleStartDt") String scheduleStartDt,
                                              @RequestParam("scheduleEndDt") String scheduleEndDt,
                                             @RequestParam(value = "files", required = false) List<MultipartFile> files) {
        Gongo g = new Gongo();
        g.setGongoName(gongoName);
        g.setContent(content);
        g.setGongoType(gongoType);
        g.setScheduleStartDt(scheduleStartDt);
        g.setScheduleEndDt(scheduleEndDt);
        try {
            // 공고게시글 저장
            boardService.saveGongo(g);

//            log.info("게시글 저장 요청: {}", g);
            if (files != null && !files.isEmpty()) {
                for (MultipartFile file : files) {
                    if (!file.getContentType().equals("application/pdf")) {
                        return ResponseEntity.badRequest().body("PDF 파일만 업로드 가능합니다.");
                    }
                    if (!file.isEmpty()) {
                        boardService.uploadAndSavePdf("gongo", g.getGongoSn(), file);
                    }
                }
            }
        } catch (Exception e) {
//            throw new RuntimeException(e);
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("게시글 등록 중 오류가 발생했습니다.");
        }
        return ResponseEntity.ok("공고게시글이 등록되었습니다.");
    }

    @ApiOperation(value = "게시글 PDF 다운로드")
    @GetMapping("/gongoboard/pdf/download/{pdf_sn}")
    public void downloadPdf(@PathVariable int pdf_sn, HttpServletResponse response) {
        try {
            PdfFileEntity pdfFile = boardService.getPdfFileById(pdf_sn);
            if (pdfFile == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("파일을 찾을 수 없습니다.");
                return;
            }

            Path filePath = Paths.get(pdfFile.getPath());
            if (Files.notExists(filePath)) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("파일이 존재하지 않습니다.");
                return;
            }

            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + pdfFile.getOriFileName()+ "\"");
            Files.copy(filePath, response.getOutputStream());
            response.getOutputStream().flush();
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }



}

package kt.aivle.board.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import kt.aivle.board.mapper.BoardMapper;
import kt.aivle.board.model.*;
import kt.aivle.board.service.BoardService;
import kt.aivle.member.service.JwtTokenProvider;
import kt.aivle.member.service.RefreshTokenService;
import kt.aivle.member.service.UserService;
import kt.aivle.rslt_list.mapper.RsltListMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.*;

import org.springframework.http.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import java.nio.charset.StandardCharsets;
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
    private BoardMapper boardMapper;

    @Autowired
    private BoardService boardService;

    @ApiOperation(value="게시글 리스트")
    @GetMapping("/userboard")
    public BoardListResponse getListBoard(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "7") int size)
    {
        BoardListResponse boardlist = new BoardListResponse();
        try {
            boardlist = boardService.getListBoard(page, size);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return boardlist;
    }

    @ApiOperation(value="공고 리스트")
    @GetMapping("/gongoboard")
    public GongoListResponse getListGongo(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "7") int size)
    {
        GongoListResponse gongolist = new GongoListResponse();
        try {
            gongolist = boardService.getListGongo(page, size);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return gongolist;
    }

    @ApiOperation(value="게시글 올리기")
    @PostMapping("/post-userboard")
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
    @GetMapping("/userboard/detail")
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
    @PutMapping("/userboard/{boardSn}")
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
    @PostMapping("/userboard")
    @Transactional
    public ResponseEntity<String> updatePost(@RequestParam(value = "boardSn", required = false) String strboardSn,
                                             @RequestParam("title") String title,
                                             @RequestParam("content") String content,
                                             @RequestParam("userSn") int userSn,
                                             @RequestParam(value = "files", required = false) List<MultipartFile> files,
                                             @RequestParam(value = "deletedFileIds", required = false) List<Integer> deletedFileIds) {
        try {
            Integer boardSn = Integer.parseInt(strboardSn);

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
            e.printStackTrace(); // 서버 로그에 스택 트레이스 출력
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("게시글 수정 중 오류가 발생했습니다."+ e.getMessage());
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

    @ApiOperation(value = "공고게시글 상세 조회")
    @GetMapping("/gongoboard/detail") // @RequestParam("userSn") int userSn 관리자 추가할거 생각.
    public ResponseEntity<Map<String, Object>> getGongoDetail(@RequestParam("gongoSn") int gongoSn) {
        try {
            Gongo gongo = boardService.getPostByGongoSn(gongoSn);
            List<PdfFileEntity> pdfs = boardService.findPdfsByGongoSn(gongoSn);
//            boolean isOwner = (gongo.getUserSn() == userSn); // 본인 여부 확인

            Map<String, Object> response = new HashMap<>();
            response.put("gongo", gongo);
//            response.put("isOwner", isOwner);
            response.put("pdfs", pdfs);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @ApiOperation(value = "공고게시글 소프트 삭제")
    @PutMapping("/gongoboard/{gongoSn}")
    public ResponseEntity<String> softDeleteGongo(@PathVariable int gongoSn, @RequestParam int userSn) {
        try {
            Gongo post = boardService.getPostByGongoSn(gongoSn);
            if (post == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("권한이 없습니다.");
            }
            boardService.softDeleteGongo(gongoSn);
            // pdf 데이터도 삭제
            List<PdfFileEntity> pdfs = boardService.findPdfsByGongoSn(gongoSn);
            if (!pdfs.isEmpty()) {
                boardService.softDeletePdf(gongoSn);
            }
            return ResponseEntity.ok("게시글이 삭제되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("게시글 삭제 중 오류가 발생했습니다.");
        }
    }


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

    @ApiOperation(
            value = "file.1 파일다운로드",
            notes = "파일다운로드",
            response = Resource.class   // Use Resource.class here
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = Resource.class)
    })
    @GetMapping(
            value = "/gongoboard/pdf/download/{pdfSn}",
            produces = MediaType.APPLICATION_OCTET_STREAM_VALUE  // force the content type
    )
    public ResponseEntity<Resource> downloadPdf(@PathVariable int pdfSn) throws IOException {
        String filePath = boardMapper.getPdfPathById(pdfSn);
        File file = new File(filePath);
        if (!file.exists() || !file.isFile()) {
            return ResponseEntity.notFound().build();
        }
        InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"");

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(file.length())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

}

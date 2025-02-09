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
import org.springframework.core.io.*;
import org.springframework.http.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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
            List<PdfFileEntity> pdfs = boardService.getPdfsByGongoSn(gongoSn);
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
            List<PdfFileEntity> pdfs = boardService.getPdfsByGongoSn(gongoSn);
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

    @GetMapping("/gongoboard/pdf/download/{pdfSn}")
    public ResponseEntity<byte[]> getPdfInfo(@PathVariable("pdfSn") int pdfSn) {
        PdfFileEntity pdfFile = boardService.getPdfFileById(pdfSn);
        if (pdfFile == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        // 외부 URL 가져오기 (pdfFile.getUrl()은 외부 URL이어야 함)
        String fileUrl = pdfFile.getPath() + pdfFile.getFileName();
        // 예: "http://4.217.186.166:8081/uploads/pdf/파일명.pdf"

        try {
            URL url = new URL(fileUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);  // 타임아웃 설정
            connection.setReadTimeout(5000);  // 읽기 타임아웃 설정

            // 파일을 byte 배열로 읽기
            try (InputStream inputStream = connection.getInputStream();
                 ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {

                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    byteArrayOutputStream.write(buffer, 0, bytesRead);
                }

                byte[] fileBytes = byteArrayOutputStream.toByteArray();
                // 파일 다운로드 헤더 설정
                HttpHeaders headers = new HttpHeaders();
                headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=*=UTF-8''" + URLEncoder.encode(pdfFile.getFileName(), StandardCharsets.UTF_8));
                headers.setContentLength(fileBytes.length);

                return ResponseEntity.ok()
                        .headers(headers)
//                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .body(fileBytes);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

//    @ApiOperation(value = "게시글 PDF 다운로드")
//    @GetMapping("/gongoboard/pdf/download/{pdf_sn}")
//    public ResponseEntity<byte[]> download(@PathVariable int pdf_sn) {
//        PdfFileEntity pdfFile = boardService.getPdfFileById(pdf_sn);
//        if (pdfFile == null) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
//        }
//
//        HttpHeaders headers = new HttpHeaders();
//        String fileUrl = pdfFile.getPath() + pdfFile.getFileName();
//
//        try {
//            // HTTP 연결을 통해 파일 다운로드
//            URL url = new URL(fileUrl);
//            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//            connection.setRequestMethod("GET");
//
//            byte[] data;
//            try (InputStream inputStream = connection.getInputStream()) {
//                data = inputStream.readAllBytes();
//            }
//
//            String encodedFileName = URLEncoder.encode(pdfFile.getOriFileName(), StandardCharsets.UTF_8.name());
//            headers.setContentDisposition(ContentDisposition.attachment().filename(encodedFileName).build());
//            headers.setContentType(MediaType.APPLICATION_PDF);
//
//            return ResponseEntity.ok()
//                    .headers(headers)
//                    .body(data);
//
//        } catch (IOException ex) {
//            ex.printStackTrace();
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//        }
//    }


//    @ApiOperation(value = "게시글 PDF 다운로드")
//    @GetMapping("/gongoboard/pdf/download/{pdf_sn}")
//    public ResponseEntity<byte[]> download(@PathVariable int pdf_sn) {
//        PdfFileEntity pdfFile = boardService.getPdfFileById(pdf_sn);
//        if (pdfFile == null) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
//        }
//        HttpHeaders headers = new HttpHeaders();
//        String fileUrl = pdfFile.getPath() + pdfFile.getFileName();
//        URL url = new URL(fileUrl);
//        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//        connection.setRequestMethod("GET");
//
//        Path fileStorageLocation = Paths.get(pdfFile.getPath());
//        try {
//            Path filePath = fileStorageLocation.resolve(pdfFile.getFileName()).normalize();
//            Resource resource = new UrlResource(filePath.toUri());
//
//            if (!resource.exists()) {
//                // Log error and return 404 status
//                return ResponseEntity.notFound().build();
//            }
//
//            byte[] data = Files.readAllBytes(filePath);
//            String encodedFileName = URLEncoder.encode(pdfFile.getFileName(), StandardCharsets.UTF_8.name());
//            headers.setContentDispositionFormData("attachment", URLEncoder.encode(encodedFileName, "UTF-8"));
//
//            return ResponseEntity.ok()
//                    .headers(headers)
//                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
//                    .body(data);
//
//        } catch (IOException ex) {
//            // Log the exception and return a server error status for IOException
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//        } catch (Exception ex) {
//            // Log the exception and return a server error status for other Exceptions
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//        }
//    }
}

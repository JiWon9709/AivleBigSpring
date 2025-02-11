package kt.aivle.board.service;

import kt.aivle.board.mapper.BoardMapper;
import kt.aivle.board.model.*;
import kt.aivle.board.model.ImgEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;

@Slf4j
@Service
@Transactional
public class BoardService {
    @Autowired
    private BoardMapper boardMapper;
    private static final String FTP_UPLOAD_DIR = "/uploads/board/";
    private static final String FTP_URL_PREFIX = "/app/data/uploads/";
    private static final String FTP_URL_IMG = "http://4.217.186.166:8081/uploads/";

    @Value("${file.path}")
    private String dir;

    public BoardListResponse getListBoard(int page, int size) {
        BoardListResponse boardlist = new BoardListResponse();
        try {
            // 페이지 번호를 0부터 시작하도록 변환
            int offset = (page - 1) * size;

            // mapper에서 리스트형식으로 받기위해 set
            List<Board> boardList = boardMapper.getListBoard(offset, size);
//            boardlist.setBoardListResponse(boardList);
            // 전체 게시글 수 조회
            int totalCount = boardMapper.getTotalCount();

            // 응답 데이터 설정
            boardlist.setBoardListResponse(boardList);
            boardlist.setTotalCount(totalCount); // 전체 게시글 수
            boardlist.setPage(page);            // 현재 페이지
            boardlist.setSize(size);            // 페이지 크기
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return boardlist;
    }

    public List<ImgEntity> getImagesByBoardSn(int boardSn) {
        return boardMapper.findImagesByBoardSn(boardSn);
    }

    public GongoListResponse getListGongo(int page, int size) {
        GongoListResponse gongolist = new GongoListResponse();
        try {
            // 페이지 번호를 0부터 시작하도록 변환
            int offset = (page - 1) * size;
            // mapper에서 리스트형식으로 받기위해 set
            List<Gongo> gongoList = boardMapper.getListGongo(offset, size);
            int totalCount = boardMapper.getGongoTotalCount();

            gongolist.setGongoListResponse(gongoList);
            gongolist.setTotalCount(totalCount); // 전체 게시글 수
            gongolist.setPage(page);            // 현재 페이지
            gongolist.setSize(size);            // 페이지 크기
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return gongolist;
    }

    public void savePost(Board board) {
        log.info("게시글 저장 요청: {}", board);
        boardMapper.saveboard(board);
    }

    @Transactional
    public void uploadAndSaveImage(String refTable, int refSn, MultipartFile file) throws IOException {
        // 파일 이름 및 확장자 생성
        String uploadFolder = dir + "img";
        String originalFilename = file.getOriginalFilename();
        String ext = originalFilename.substring(originalFilename.lastIndexOf('.') + 1);
        String uniqueFileName = UUID.randomUUID().toString() + "." + ext;

        // 디렉터리 생성 체크
        File directory = new File(uploadFolder);
        if (!directory.exists()) {
            directory.mkdirs(); // 디렉터리 없으면 생성
        }
        // DB 저장
        ImgEntity imgEntity = new ImgEntity();
        imgEntity.setRefTable(refTable);
        imgEntity.setRefSn(refSn);
        imgEntity.setPath(FTP_URL_IMG + "img/");
        imgEntity.setFileName(uniqueFileName);
        imgEntity.setOriFileName(originalFilename);
        imgEntity.setExt(ext);

        boardMapper.regImg(imgEntity);
        // FTP 업로드
        try {
            File saveFile = new File(uploadFolder, uniqueFileName);
            file.transferTo(saveFile);
        } catch (Exception e) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("이미지를 서버에 저장 중 에러가 발생하였습니다.");
        }

    }

    // 게시글 상세조회
    public Board getPostByBoardSn(int boardSn) {
        return boardMapper.getPostByBoardSn(boardSn);
    }

    // 소프트게시글 삭제
    public void softDeletePost(int boardSn) {
        boardMapper.softDeletePost(boardSn);
    }

    // boardSn 소프트이미지 삭제
    public void softDeleteImg(int boardSn) {
        boardMapper.softDeleteImg(boardSn);
    }

    // 이미지Sn으로 이미지 삭제
    public void deleteImage(int imgSn) {
        boardMapper.deleteImage(imgSn);
    }

    // 게시글 수정
    public void updatePost(Board board) {
        boardMapper.updatePost(board);
    }

    // 공고게시글 저장
    public void saveGongo(Gongo gongo) {
        log.info("게시글 저장 요청: {}", gongo);
        boardMapper.saveGongo(gongo);
    }

    // pdffile
    public PdfFileEntity getPdfFileById(int id) {
        return boardMapper.getPdfFileById(id);
    }

    @Transactional
    public void uploadAndSavePdf(String refTable, int refSn, MultipartFile pdfFile) throws IOException {
        // 파일 이름 및 확장자 생성
        String uploadFolder = dir + "pdf";
        String originalFilename = pdfFile.getOriginalFilename();
        String uniqueFileName = UUID.randomUUID().toString() + ".pdf";

        // 디렉터리 생성 체크
        File directory = new File(uploadFolder);
        if (!directory.exists()) {
            directory.mkdirs(); // 디렉터리 없으면 생성
        }
        // DB 저장
        PdfFileEntity pdf = new PdfFileEntity();
        pdf.setRefTable(refTable);
        pdf.setRefSn(refSn);
        pdf.setPath(FTP_URL_PREFIX +"pdf/");
        pdf.setFileName(uniqueFileName);
        pdf.setOriFileName(originalFilename);

        boardMapper.regPdf(pdf);
        // FTP 업로드
        try {
            File saveFile = new File(uploadFolder, uniqueFileName);
            pdfFile.transferTo(saveFile);
        } catch (Exception e) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("pdf를 서버에 저장 중 에러가 발생하였습니다.");
        }

    }

    // 공고게시글 상세조회
    public Gongo getPostByGongoSn(int gongoSn) {
        return boardMapper.getPostByGongoSn(gongoSn);
    }

    // 공고게시글의 pdf 불러오기
    public List<PdfFileEntity> findPdfsByGongoSn(int gongoSn) {
        return boardMapper.findPdfsByGongoSn(gongoSn);
    }

    // 소프트 공고게시글 삭제
    public void softDeleteGongo(int gongoSn) {
        boardMapper.softDeleteGongo(gongoSn);
    }

    // gongoSn 소프트이미지 삭제
    public void softDeletePdf(int gongoSn) {
        boardMapper.softDeletePdf(gongoSn);
    }

    // pdfSn pdf 삭제
    public void deletePdf(int pdfSn) {
        boardMapper.deletePdf(pdfSn);
    }

}

package kt.aivle.board.mapper;

import kt.aivle.base.config.mapper.DATA_SOURCE;
import kt.aivle.board.model.*;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@DATA_SOURCE
public interface BoardMapper {
    public List<Board> getListBoard(@Param("offset")int offset, @Param("size")int size);
    public int getTotalCount();

    public List<ImgEntity> findImagesByBoardSn(int boardSn);
    public void saveboard(Board board);

    public List<Gongo> getListGongo(@Param("offset")int offset, @Param("size")int size);
    public int getGongoTotalCount();

    public int regImg(ImgEntity imgEntity);
    public Board getPostByBoardSn(int boardSn);
    public void softDeletePost(int boardSn);
    public void softDeleteImg(int boardSn);
    public void deleteImage(int imgSn);
    public void updatePost(Board board);

    public PdfFileEntity getPdfFileById(int id);
    public void saveGongo(Gongo gongo);
    public int regPdf(PdfFileEntity pdfEntity);
    public Gongo getPostByGongoSn(int gongoSn);
    public List<PdfFileEntity> findPdfsByGongoSn(int gongoSn);

    public void softDeleteGongo(int gongoSn);
    public void softDeletePdf(int gongoSn);
    public void deletePdf(int pdfSn);
    public String getPdfPathById(int pdfSn);
}

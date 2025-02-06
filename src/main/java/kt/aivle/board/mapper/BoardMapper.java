package kt.aivle.board.mapper;

import kt.aivle.base.config.mapper.DATA_SOURCE;
import kt.aivle.board.model.Board;
import kt.aivle.board.model.BoardListResponse;
import kt.aivle.board.model.BoardRequest;
import kt.aivle.board.model.Gongo;
import kt.aivle.board.model.ImgEntity;

import java.util.List;

@DATA_SOURCE
public interface BoardMapper {
    public List<Board> getListBoard();
    public List<ImgEntity> findImagesByBoardSn(int boardSn);
    public void saveboard(Board board);
    public List<Gongo> getListGongo();
    public int regImg(ImgEntity imgEntity);
    public Board getPostByBoardSn(int boardSn);
    public void softDeletePost(int boardSn);
    public void softDeleteImg(int boardSn);
    public void updatePost(Board board);
}

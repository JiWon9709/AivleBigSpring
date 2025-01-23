package kt.aivle.board.mapper;

import kt.aivle.base.config.mapper.DATA_SOURCE;
import kt.aivle.board.model.Board;
import kt.aivle.board.model.BoardListResponse;

import java.util.List;

@DATA_SOURCE
public interface BoardMapper {
    public List<Board> getListBoard();
}

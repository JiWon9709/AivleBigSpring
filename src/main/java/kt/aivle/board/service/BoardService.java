package kt.aivle.board.service;

import kt.aivle.board.mapper.BoardMapper;
import kt.aivle.board.model.Board;
import kt.aivle.board.model.BoardListResponse;
import kt.aivle.board.model.BoardRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BoardService {
    @Autowired
    private BoardMapper boardMapper;

    public BoardListResponse getListBoard() {
        BoardListResponse boardlist = new BoardListResponse();
        try {
            // mapper에서 리스트형식으로 받기위해 set
            List<Board> boardList = boardMapper.getListBoard();
            boardlist.setBoardListResponse(boardList);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return boardlist;
    }

    public void savePost(Board board) {
        Board post = new Board();
        post.setTitle(board.getTitle());
        post.setContent(board.getContent());
        post.setUserSn(board.getUserSn());
        boardMapper.saveboard(post);
    }
}

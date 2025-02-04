package kt.aivle.board.service;

import kt.aivle.board.mapper.BoardMapper;
import kt.aivle.board.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
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

    public GongoListResponse getListGongo() {
        GongoListResponse gongolist = new GongoListResponse();
        try {
            // mapper에서 리스트형식으로 받기위해 set
            List<Gongo> gongoList = boardMapper.getListGongo();
            gongolist.setGongoListResponse(gongoList);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return gongolist;
    }

    public void savePost(Board board) {
//        Board post = new Board();
//        post.setTitle(board.getTitle());
//        post.setContent(board.getContent());
//        post.setUserSn(board.getUserSn());
        log.info("게시글 저장 요청: {}", board);
        boardMapper.saveboard(board);
    }
}

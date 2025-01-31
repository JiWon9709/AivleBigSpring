package kt.aivle.board.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import kt.aivle.board.model.Board;
import kt.aivle.board.model.BoardListResponse;
import kt.aivle.board.model.BoardRequest;
import kt.aivle.board.service.BoardService;
import kt.aivle.member.service.JwtTokenProvider;
import kt.aivle.member.service.RefreshTokenService;
import kt.aivle.member.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/post-board")
    public ResponseEntity<String> createPost(@RequestBody BoardRequest board) {
        Board b = new Board();
        try {
            b.setUserSn(board.getUserSn());
            b.setTitle(board.getTitle());
            b.setContent(board.getContent());
            boardService.savePost(b);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return ResponseEntity.ok("게시글이 등록되었습니다.");
    }
}

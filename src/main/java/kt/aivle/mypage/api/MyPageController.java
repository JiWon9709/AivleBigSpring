package kt.aivle.mypage.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import kt.aivle.mypage.model.FavoriteListResponse;
import kt.aivle.mypage.service.MyPageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Api(tags = "mypage", description = "mypage API")
//@CrossOrigin(origins = "http://localhost:3000") // React 앱의 도메인 허용. SecurityConfig에서 전역으로 CORS 설정해서 개별 설정 불필요.
public class MyPageController {

    @Autowired
    private MyPageService myPageService;

    // 관심주택 페이지
    @ApiOperation(value = "관심주택리스트")
//    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK", response = UserInfo.class)})
    @GetMapping(value = "/favorites",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public FavoriteListResponse getUserfavorite(@ApiParam(value = "관심주택페이지", required = true) @RequestParam("userSn") int userSn) {
        FavoriteListResponse response = new FavoriteListResponse();
        try {
            System.out.println("받은 데이터: " + response); // 요청 데이터 로그
            response = myPageService.getuserFavorite(userSn);
        } catch (Exception e) {
            // 예외 처리
        }
        return response;
    }

    @ApiOperation(value = "관심주택 삭제")
    @DeleteMapping("/favorites")
    public ResponseEntity<String> deleteFavorite(@ApiParam(value = "관심 주택 ID", required = true) @RequestParam("favoriteSn") int favoriteSn) {
        try {
            boolean isDeleted = myPageService.deleteFavorite(favoriteSn);

            if (isDeleted) {
                return ResponseEntity.ok("관심 주택이 성공적으로 삭제되었습니다.");
            } else {
                return ResponseEntity.badRequest().body("삭제에 실패했습니다. 해당 ID를 확인하세요.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류가 발생했습니다.");
        }
    }


}

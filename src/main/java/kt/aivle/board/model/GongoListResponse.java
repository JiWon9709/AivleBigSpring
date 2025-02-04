package kt.aivle.board.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class GongoListResponse {
    @ApiModelProperty(notes = "게시글리스트")
    private List<Gongo> GongoListResponse;
}

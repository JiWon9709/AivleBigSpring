package kt.aivle.input.model.DTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InputRequestDTO {
    private Integer userSn;
    private Integer gongoSn;
    private String inputType;
    private Integer inputRank;
    private Integer inputScore;
}

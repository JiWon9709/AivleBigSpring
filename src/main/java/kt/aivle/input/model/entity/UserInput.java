package kt.aivle.input.model.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class UserInput {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long inputSn; // input_sn

    private Integer userSn; // user_sn
    private Integer gongoSn; // gongo_sn
    private Integer inputType; // input_type
    private Integer inputRank; // input_rank
    private Integer inputScore; // input_score
}

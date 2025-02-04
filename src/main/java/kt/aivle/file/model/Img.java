package kt.aivle.file.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.security.Timestamp;

@Entity
@Table(name = "img")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Img {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int imgSn;

    private String refTable;

    private Integer refSn;

    private String path;

    private String ext;

    private String useYn;

    @ApiModelProperty
    private String createdDt;

    @ApiModelProperty
    private String updatedDt;

    // Getters and Setters
}

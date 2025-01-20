package kt.aivle.gongo1111.model.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class GongoDTO {
    private Long gongoSn;
    private String gongoName;
    private String scheduleStartDt;
    private String scheduleEndDt;
    private String documentStartDt;
    private String documentEndDt;
    private LocalDateTime createdDt;
    private LocalDateTime updatedDt;

}

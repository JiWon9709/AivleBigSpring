package kt.aivle.gongo.model;

import lombok.*;

import java.time.LocalDateTime;

@Data
public class Gongo {
    private Long gongoSn;
    private String gongoName;
    private String scheduleStartDt;
    private String scheduleEndDt;
    private String documentStartDt;
    private String documentEndDt;
    private LocalDateTime createdDt;
    private LocalDateTime updatedDt;

}

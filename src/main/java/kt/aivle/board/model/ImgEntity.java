package kt.aivle.board.model;

import lombok.Data;

import java.security.Timestamp;

@Data
public class ImgEntity {
    private int imgSn;
    private String refTable;
    private int refSn;
    private String path;
    private String fileName;
    private String oriFileName;
    private String ext;
    private String useYn;
    private Timestamp createdDt;
}


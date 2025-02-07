package kt.aivle.board.model;

import lombok.Data;

import java.security.Timestamp;

@Data
public class PdfFileEntity {
    private int pdfSn;
    private String refTable;
    private int refSn;
    private String path;
    private String fileName;
    private String oriFileName;
    private String useYn;
    private Timestamp createdDt;
}

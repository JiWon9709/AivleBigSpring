package kt.aivle.file.service;

import kt.aivle.file.mapper.FileMapper;
import kt.aivle.file.model.Img;
import kt.aivle.file.model.ImgRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class ImgService {

//    @Autowired
//    private ImgRepository imgRepository;
    @Autowired
    private FileMapper fileMapper;

    public void saveImage(String refTable, Integer refSn, String path, String ext) {
        Img img = new Img();
        img.setRefTable(refTable);
        img.setRefSn(refSn);
        img.setPath(path);
        img.setExt(ext);
        img.setUseYn("Y");
        fileMapper.saveImage(img);
//        return fileMapper.saveImage(img);
    }
}

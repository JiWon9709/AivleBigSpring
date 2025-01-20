package kt.aivle.gongo1111.service;

import kt.aivle.gongo1111.mapper.GongoMapper;
import kt.aivle.gongo1111.model.dto.GongoDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GongoService {
    private final GongoMapper gongoMapper;

    // 활성화된 공고 목록 조회
    public List<GongoDTO> getActiveGongos() {
        return gongoMapper.selectActiveGongos();
    }

    // 특정 공고 조회
    public GongoDTO getGongo(Integer gongoSn) {
        return gongoMapper.selectGongoByGongoSn(gongoSn);
    }
}

package kt.aivle.gongo.service;

import kt.aivle.gongo.mapper.GongoMapper;
import kt.aivle.gongo.model.Gongo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GongoService {
    private final GongoMapper gongoMapper;

    // 활성화된 공고 목록 조회
    public List<Gongo> getActiveGongos() {
        return gongoMapper.selectActiveGongos();
    }

    // 특정 공고 조회
    public Gongo getGongo(Integer gongoSn) {
        return gongoMapper.selectGongoByGongoSn(gongoSn);
    }
}

package kt.aivle.input.service;


import kt.aivle.base.BaseMsg;
import kt.aivle.base.BaseResModel;
import kt.aivle.input.mapper.InputMapper;
import kt.aivle.input.model.DTO.InputRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class InputService {

    @Autowired
    private InputMapper inputMapper;

    /**
     * 테스트 리스트
     *
     * @return
     */
    @Transactional
    public BaseResModel regInput(InputRequestDTO inputRequestDTO) {
        BaseResModel result = new BaseResModel();

        int cnt = inputMapper.regInput(inputRequestDTO);
        if (cnt == 0) {
            result.setResultMsg(BaseMsg.FAILED.getValue());
            result.setResultCode(BaseMsg.FAILED.getCode());
        }
        return result;
    }
}
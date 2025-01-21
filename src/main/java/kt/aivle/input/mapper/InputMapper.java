package kt.aivle.input.mapper;


import kt.aivle.base.config.mapper.DATA_SOURCE;
import kt.aivle.input.model.DTO.InputRequestDTO;
import kt.aivle.rslt_list.model.JutaekInfo;
import kt.aivle.rslt_list.model.JutaekListRequest;

import java.util.List;

@DATA_SOURCE
public interface InputMapper {

    /**
     * input에 삽입
     *
     * @return
     */
    public int regInput(InputRequestDTO inputRequestDTO);

}
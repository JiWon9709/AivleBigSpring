package kt.aivle.member.mapper;

import kt.aivle.base.config.mapper.DATA_SOURCE;
import kt.aivle.member.model.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@DATA_SOURCE // 커스텀 어노테이션, 일단 쓰고 나중에 이해
public interface UserMapper {
    void insertUser(User user);
    User findByEmail(String email);

}

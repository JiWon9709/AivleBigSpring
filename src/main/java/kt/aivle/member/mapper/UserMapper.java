package kt.aivle.member.mapper;

import kt.aivle.base.config.mapper.DATA_SOURCE;
import kt.aivle.member.model.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@DATA_SOURCE
public interface UserMapper {
    void insertUser(User user);
    User findByEmail(String email);

}

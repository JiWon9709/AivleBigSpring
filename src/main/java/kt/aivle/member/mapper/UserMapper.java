package kt.aivle.member.mapper;

import kt.aivle.base.config.mapper.DATA_SOURCE;
import kt.aivle.member.model.SignupRequest;
import kt.aivle.member.model.UserAuth;
import kt.aivle.member.model.UserResponse;

import org.apache.ibatis.annotations.Param;

@DATA_SOURCE // 커스텀 어노테이션, 일단 쓰고 나중에 이해
public interface UserMapper {
    void insertUser(SignupRequest signupRequest);
    UserAuth findByEmailForAuth(String email); // 로그인 검증용
    UserResponse findByEmail(String email); // 일반 정보 조회용
    int updatePassword(@Param("email") String email, @Param("password") String password);

}

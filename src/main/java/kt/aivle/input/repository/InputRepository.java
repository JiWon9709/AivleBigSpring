package kt.aivle.input.repository;

import kt.aivle.input.model.entity.UserInput;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InputRepository extends JpaRepository<UserInput, Long> {
}
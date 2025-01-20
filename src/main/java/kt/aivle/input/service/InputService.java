package kt.aivle.input.service;

import kt.aivle.input.model.DTO.InputRequestDTO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class InputService {

    private final JdbcTemplate jdbcTemplate;

    public InputService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void saveInput(InputRequestDTO inputRequestDTO) {
        String sql = "INSERT INTO user_input (user_sn, gongo_sn, input_type, input_rank, input_score) " +
                "VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql,
                inputRequestDTO.getUserSn(),
                inputRequestDTO.getGongoSn(),
                inputRequestDTO.getInputType(),
                inputRequestDTO.getInputRank(),
                inputRequestDTO.getInputScore()
        );
    }
}


package kt.aivle.input.service;

import kt.aivle.input.model.DTO.InputRequestDTO;
import kt.aivle.input.model.entity.UserInput;
import kt.aivle.input.repository.InputRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InputService {

    private final InputRepository inputRepository;

    public void saveInput(InputRequestDTO inputRequestDTO) {

        UserInput userInput = new UserInput();
        userInput.setUserSn(inputRequestDTO.getUserSn());
        userInput.setGongoSn(inputRequestDTO.getGongoSn());
        userInput.setInputType(inputRequestDTO.getInputType());
        userInput.setInputRank(inputRequestDTO.getInputRank());
        userInput.setInputScore(inputRequestDTO.getInputScore());
        inputRepository.save(userInput);
    }
}



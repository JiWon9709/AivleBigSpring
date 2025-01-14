package kt.aivle;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableEncryptableProperties
public class AivleApplication {

	public static void main(String[] args) {

		SpringApplication.run(AivleApplication.class, args);

	}

}

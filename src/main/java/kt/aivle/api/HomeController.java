package kt.aivle.api;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import springfox.documentation.annotations.ApiIgnore;

@Controller
@ApiIgnore
@Profile({"local", "stg", "dev"})

public class HomeController {

  @RequestMapping(value = {"/", "/api-doc"})
  public String index() {
    return "redirect:swagger-ui.html";
  }


}

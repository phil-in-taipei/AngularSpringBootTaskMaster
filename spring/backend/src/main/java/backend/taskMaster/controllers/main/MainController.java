package backend.taskMaster.controllers.main;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class MainController {

    @GetMapping(value = {"/"})
    public String index() {
        return "index";
    }

    //@GetMapping(value = {"/{regex:\\w+}", "/**/{regex:\\w+}"})
   //@GetMapping(value = {"/{regex:[\\w-]+}", "/**/{regex:[\\w-]+}"})
   @GetMapping(value = "/{path:[^\\.]*}")
    public String forward404() {
    //   return "forward:/index";
     return "forward:/";
    }
}

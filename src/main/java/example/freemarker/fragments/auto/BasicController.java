package example.freemarker.fragments.auto;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller("AutoBasicController")
@RequestMapping("/auto")
public class BasicController {

    @GetMapping("/")
    public String getPage(Model model) {
        return "auto/basic_page";
    }

    @GetMapping("/fragment")
    public String getFragment(Model model) {
        return "auto/basic_page :: ArticleBlock";
    }

    @GetMapping("/badfragment")
    public String failOnNonExistentFragment(Model model) {
        return "auto/basic_page :: BadFragment";
    }

}

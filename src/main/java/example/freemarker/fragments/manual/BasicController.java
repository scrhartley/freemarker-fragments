package example.freemarker.fragments.manual;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller("ManualBasicController")
@RequestMapping("/manual")
public class BasicController {

    @GetMapping
    public String getPage(Model model) {
        return "manual/basic_page";
    }

    @GetMapping("/fragment")
    public String getFragment(Model model) {
        model.addAttribute("FRAGMENT", "article");
        return "manual/basic_page";
    }

    @GetMapping("/badfragment")
    public String failOnNonExistentFragment(Model model) {
        model.addAttribute("FRAGMENT", "badfragment");
        return "manual/basic_page";
    }

}

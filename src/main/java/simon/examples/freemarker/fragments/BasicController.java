package simon.examples.freemarker.fragments;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class BasicController {

    @GetMapping("/")
    public String getPage(Model model) {
        return "basic_page";
    }

    @GetMapping("/fragment")
    public String getFragment(Model model) {
        model.addAttribute("FRAGMENT", "article");
        return "basic_page";
    }

    @GetMapping("/badfragment")
    public String failOnNonExistentFragment(Model model) {
        model.addAttribute("FRAGMENT", "badfragment");
        return "basic_page";
    }

}

package example.freemarker.fragments.auto;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import example.freemarker.fragments.Person;

@Controller("AutoTableController")
@RequestMapping("/auto/table")
public class TableController {

    @GetMapping
    public String getPage(Model model) {
        model.addAttribute("people", List.of(
                new Person(1, "John Paul", "jp@gmail.com"),
                new Person(2, "Paul George", "pg@gmail.com"),
                new Person(3, "George John", "gj@gmail.com")
        ));
        return "auto/table";
    }

    @GetMapping("/row")
    public String getRow(Model model) {
        model.addAttribute("person",
                new Person(3, "George John", "gj@gmail.com") );
        return "auto/table :: Row";
    }

}


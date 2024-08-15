package example.freemarker.fragments;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import freemarker.template.Template;
import freemarker.template.TemplateException;

public class FullAutoTest extends FreeMarkerTest {

    private static final FragmentTemplate FRAGMENT_TEMPLATE_BUILDER = new FragmentTemplate.FullyAutomatic();

    @Test
    public void testAutoFragment() throws TemplateException, IOException {
        Template template = getTemplate("/templates/autoFragment.ftlh");
        String expected = """
                first line
                macro 2
                last line""";
        assertEquals(expected, process(template));

        Template fragment = fragmentTemplate("Macro1", template);
        assertEquals("macro 1", process(fragment));
    }

    @Test
    public void testAutoFragmentInheritImports() throws TemplateException, IOException {
        Template template = getTemplate("/templates/autoFragmentImport.ftlh");
        String expected = """
                first line
                imported macro 2
                last line""";
        assertEquals(expected, process(template));

        // Fragment doesn't need to also import the library macros
        Template fragment = fragmentTemplate("LocalMacro1", template);
        expected = """
                local macro 1 first line
                imported macro 2
                local macro 1 last line""";
        assertEquals(expected, process(fragment));

        // Use an imported macro as a fragment
        fragment = fragmentTemplate("lib.LibMacro2", template);
        assertEquals("imported macro 2", process(fragment));
    }

    @Test
    public void testAutoFragmentNestedImports() throws TemplateException, IOException {
        Template template = getTemplate("/templates/autoFragmentImportNesting.ftlh");
        String expected = """
                first line
                macro calling nested first line
                imported macro 1
                macro calling nested last line
                last line""";
        assertEquals(expected, process(template));

        Template fragment = fragmentTemplate("LocalMacro1", template);
        expected = """
                local macro 1 first line
                macro calling nested first line
                imported macro 1
                macro calling nested last line
                local macro 1 last line""";
        assertEquals(expected, process(fragment));
    }


    private static Template fragmentTemplate(String macroName, Template baseTemplate) throws IOException {
        String fragmentViewName = baseTemplate.getName() + " :: " + macroName;
        return FRAGMENT_TEMPLATE_BUILDER.build(macroName, fragmentViewName, baseTemplate);
    }

}

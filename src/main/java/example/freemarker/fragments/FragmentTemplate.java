package example.freemarker.fragments;

import java.io.IOException;
import java.io.StringReader;
import java.util.Collection;
import java.util.regex.Pattern;

import freemarker.core.LibraryLoad;
import freemarker.core.Macro;
import freemarker.template.Template;

abstract class FragmentTemplate {
    private static final Pattern ESCAPE_CHARS = Pattern.compile("[-:#]");


    abstract Template build(String macroName, String viewName, Template baseTemplate) throws IOException;


    // This implementation only writes the chosen macro's output to the page,
    // but all template content is still evaluated. To use this strategy, even the top level content
    // should be a macro in order to avoid still having to provide the full template's data when using a fragment.
    // You can then either:
    // a) Always explicitly set a fragment identifier in the view name for templates with fragments.
    // b) Configure a default macro name and use that convention in ALL your templates.
    //    Perhaps this could be enhanced to verify the default macro exists in the base template
    //    when a fragment identifier isn't specified (falling back to the full template) and hence allow
    //    non-fragment templates to not be forced to have top level content be in a macro.
    static class SemiAutomatic extends FragmentTemplate {
        @Override
        public Template build(String macroName, String viewName, Template baseTemplate) throws IOException {
            final String namespace = "$__auto_invoke__$";
            String templateText =
                    "<#import \"/" + baseTemplate.getName() + "\" as " + namespace + ">" +
                            "<@" + namespace + "." + escape(macroName) + " />";
            return newTemplate(templateText, viewName, baseTemplate);
        }
    }

    // This implementation will only execute the chosen macro, even if the base template has top level content
    // that's not in a macro. One downside of this implementation is that it uses deprecated FreeMarker methods.
    // Another is that since template-level include directives are not evaluated, macros in descendant templates
    // may be unavailable to the fragment. Instead, either: prefer imports over includes, use include directives
    // inside the fragment macro or make sure the required macros are defined in the template you are calling.
    static class FullyAutomatic extends FragmentTemplate {
        private static final boolean INCLUDE_TEMPLATE_IMPORTS_IN_FRAGMENT = true;
        private static final boolean FORCE_LAZY_IMPORTS_IN_FRAGMENT = true;

        @Override
        @SuppressWarnings("deprecation")
        public Template build(String macroName, String viewName, Template baseTemplate) throws IOException {
            String templateText = "<@" + escape(macroName) + " />";
            if (INCLUDE_TEMPLATE_IMPORTS_IN_FRAGMENT) {
                StringBuilder builder = extractImports(baseTemplate);
                if (builder != null) {
                    builder.append(templateText);
                    templateText = builder.toString();
                }
            }
            Template fragmentTemplate = newTemplate(templateText, viewName, baseTemplate);
            if (INCLUDE_TEMPLATE_IMPORTS_IN_FRAGMENT && FORCE_LAZY_IMPORTS_IN_FRAGMENT) {
                // If we're including the base template's imports, then it becomes
                // more desirable to not have to pay the cost for them unless necessarily.
                fragmentTemplate.setLazyImports(true);
            }
            @SuppressWarnings("unchecked")
            Collection<Macro> macros = (Collection<Macro>) baseTemplate.getMacros().values();
            for (Macro macro : macros) {
                fragmentTemplate.addMacro(macro);
            }
            return fragmentTemplate;
        }

        @SuppressWarnings("deprecation")
        private static StringBuilder extractImports(Template template) {
            @SuppressWarnings("unchecked")
            Collection<LibraryLoad> imports = (Collection<LibraryLoad>) template.getImports();
            if (!imports.isEmpty()) {
                StringBuilder builder = new StringBuilder();
                for (LibraryLoad ll : imports) {
                    builder.append(ll.getCanonicalForm());
                }
                return builder;
            } else {
                return null;
            }
        }
    }


    private static Template newTemplate(
            String templateText, String viewName, Template baseTemplate) throws IOException {
        return new Template(
                viewName, null,
                new StringReader(templateText),
                baseTemplate.getConfiguration(),
                baseTemplate.getParserConfiguration(),
                baseTemplate.getEncoding());
    }

    private static String escape(String macroName) {
        return ESCAPE_CHARS.matcher(macroName).replaceAll("\\\\$0");
    }

}

package example.freemarker.fragments;

import java.io.IOException;
import java.io.StringReader;
import java.util.Collection;
import java.util.Locale;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.boot.autoconfigure.freemarker.FreeMarkerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.AbstractUrlBasedView;
import org.springframework.web.servlet.view.freemarker.FreeMarkerView;
import org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver;

import freemarker.core.LibraryLoad;
import freemarker.core.Macro;
import freemarker.template.Template;

@Configuration
public class FreeMarkerAutoFragmentConfig {
    private static final String VIEW_FRAGMENT_SEPARATOR = " :: "; // Same as used by Thymeleaf templating

    private static final boolean TRANSLATE_MACRO_NAMES_ENABLED = false;
    private static final FragmentTemplate FRAGMENT_TEMPLATE_BUILDER = new FragmentTemplate.FullyAutomatic();
    private static final String DEFAULT_MACRO = null;

    @Bean
    FreeMarkerViewResolver freeMarkerViewResolver(FreeMarkerProperties properties) {
        FreeMarkerViewResolver resolver = new CustomFreeMarkerViewResolver();
        properties.applyToMvcViewResolver(resolver);
        return resolver;
    }


    static class CustomFreeMarkerView extends FreeMarkerView {
        private static final Pattern ESCAPE_CHARS = Pattern.compile("[-.:#]");
        String fragmentId;
        String fragmentViewName;

        @Override
        protected Template getTemplate(String name, Locale locale) throws IOException {
            Template template = super.getTemplate(name, locale);
            String fragment = (fragmentId != null || DEFAULT_MACRO == null) ? fragmentId : DEFAULT_MACRO;
            if (fragment != null) {
                template = FRAGMENT_TEMPLATE_BUILDER.build(toMacroName(fragment), fragmentViewName, template);
            }
            return template;
        }

        private static String toMacroName(String fragmentId) {
            if (TRANSLATE_MACRO_NAMES_ENABLED) { // e.g. "my-fragment" to "MyFragment"
                fragmentId = Stream.of(fragmentId.split("[-_]"))
                        .map(s -> Character.toUpperCase(s.charAt(0)) + s.substring(1))
                        .collect(Collectors.joining());
            }
            return ESCAPE_CHARS.matcher(fragmentId).replaceAll("\\\\$0");
        }
    }


    static class CustomFreeMarkerViewResolver extends FreeMarkerViewResolver {
        @Override
        protected View loadView(String viewName, Locale locale) throws Exception {
            String fragmentId = null;
            String originalViewName = viewName;
            int index = viewName.indexOf(VIEW_FRAGMENT_SEPARATOR);
            if (index != -1) {
                fragmentId = viewName.substring(index + VIEW_FRAGMENT_SEPARATOR.length());
                viewName = viewName.substring(0, index);
            }

            View view = super.loadView(viewName, locale);
            if (view instanceof CustomFreeMarkerView) {
                ((CustomFreeMarkerView) view).fragmentId = fragmentId;
                ((CustomFreeMarkerView) view).fragmentViewName = originalViewName;
            }
            return view;
        }

        @Override
        protected Class<?> requiredViewClass() {
            return CustomFreeMarkerView.class;
        }

        @Override
        protected AbstractUrlBasedView instantiateView() {
            return getViewClass() == CustomFreeMarkerView.class
                    ? new CustomFreeMarkerView() : super.instantiateView();
        }
    }


    abstract static class FragmentTemplate {

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
                        "<@" + namespace + "." + macroName + " />";
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

            @Override @SuppressWarnings("deprecation")
            public Template build(String macroName, String viewName, Template baseTemplate) throws IOException {
                String templateText = "<@" + macroName + " />";
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

    }

}

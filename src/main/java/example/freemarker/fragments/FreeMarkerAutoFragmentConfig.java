package example.freemarker.fragments;

import java.io.IOException;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.boot.autoconfigure.freemarker.FreeMarkerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.AbstractUrlBasedView;
import org.springframework.web.servlet.view.freemarker.FreeMarkerView;
import org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver;

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
        String fragmentId;
        String fragmentViewName;

        @Override
        protected Template getTemplate(String name, Locale locale) throws IOException {
            Template template = super.getTemplate(name, locale);
            String fragment = (fragmentId != null || DEFAULT_MACRO == null) ? fragmentId : DEFAULT_MACRO;
            if (fragment != null) {
                template = FRAGMENT_TEMPLATE_BUILDER.build(transformMacroName(fragment), fragmentViewName, template);
            }
            return template;
        }

        private static String transformMacroName(String fragmentId) {
            if (TRANSLATE_MACRO_NAMES_ENABLED) { // e.g. "my-fragment" to "MyFragment"
                // Will also capitalize when delimiter not found.
                fragmentId = Stream.of(fragmentId.split("[-_]"))
                        .map(s -> Character.toUpperCase(s.charAt(0)) + s.substring(1))
                        .collect(Collectors.joining());
            }
            return fragmentId;
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

}

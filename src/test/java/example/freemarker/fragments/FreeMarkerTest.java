package example.freemarker.fragments;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

import freemarker.cache.ClassTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapperBuilder;
import freemarker.template.Template;
import freemarker.template.TemplateDateModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import freemarker.template.Version;

abstract class FreeMarkerTest {
    private static final Configuration CONFIG = newConfiguration();


    protected static String process(Template template) throws TemplateException, IOException {
        StringWriter writer = new StringWriter();
        template.process(null, writer);
        return writer.toString().trim();
    }


    protected static Template getTemplate(String name) throws IOException {
        return CONFIG.getTemplate(name);
    }

    private static Configuration newConfiguration() {
        Version version = Configuration.VERSION_2_3_32;

        Configuration cfg = new Configuration(version);
        cfg.setTemplateLoader(new ClassTemplateLoader(FreeMarkerTest.class, "/"));

        cfg.setDefaultEncoding("UTF-8");
        cfg.setLogTemplateExceptions(false);
        cfg.setWrapUncheckedExceptions(true);
        cfg.setFallbackOnNullLoopVariable(false);
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);

        DefaultObjectWrapperBuilder builder = new DefaultObjectWrapperBuilder(version);
        builder.setForceLegacyNonListCollections(false);
        builder.setDefaultDateType(TemplateDateModel.DATETIME);
        cfg.setObjectWrapper(builder.build());

        return cfg;
    }

}

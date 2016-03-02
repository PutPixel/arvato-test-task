package com.github.putpixel.arvato.templates;

import java.nio.file.Files;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

@Component
public class TemplateProcessor {

    @Autowired
    private ResourceLoader resourceLoader;

    public String generateFromTemplate(String name, Map<String, Object> params) {
        String template = readResource(name);
        Set<Entry<String, Object>> entrySet = params.entrySet();
        for (Entry<String, Object> entry : entrySet) {
            template = template.replaceAll("@@" + entry.getKey() + "@@", String.valueOf(entry.getValue()));
        }
        return template;
    }

    private String readResource(String name) {
        try {
            Resource resource = resourceLoader.getResource("classpath:" + name);
            return new String(Files.readAllBytes(resource.getFile().toPath()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}

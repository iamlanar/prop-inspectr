package com.lanar.inspektr;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import static com.lanar.inspektr.HtmlTags.*;

public class HtmlReport implements Renderable {
    private final Map<String, Properties> props;
    private final Arguments cfg;

    public HtmlReport(Map<String, Properties> props, Arguments cfg) {
        this.props = props;
        this.cfg = cfg;
    }

    @Override
    public void render() {
        var template = loadTemplate();
        var replacement = renderHead(props.keySet()) + renderBody(props);
        template = template.replaceAll("PLACEHOLDER", replacement);
        var path = cfg.getTo();
        System.out.printf("Writing report to %s%n", path);
        File report = new File(path);
        try {
            Files.writeString(Path.of(report.getPath()), template);
            System.out.println("Done");
        } catch (IOException e) {
            throw new RuntimeException("Failed to write report");
        }
    }

    private String loadTemplate() {
        var resource = Main.class.getClassLoader().getResource("index.html");
        try {
            var path = Path.of(resource.toURI());
            return Files.readString(path);
        } catch (URISyntaxException e) {
            throw new RuntimeException("Failed to load template index.html");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String renderHead(Set<String> keys) {
        var tHead = new StringBuilder(TR_O);
        tHead.append(TH_O).append("Property / Env").append(TH_C);
        for (var key : keys) {
            tHead.append(TH_O);
            tHead.append(key);
            tHead.append(TH_C);
        }
        tHead.append(TR_C);
        return tHead.toString();
    }

    private String renderBody(Map<String, Properties> props) {
        var tBody = new StringBuilder();
        var propNames = getPropertyNames(props);
        for (var name : propNames) {
            tBody.append(TR_O);
            tBody.append(TD_O).append(name).append(TD_C);
            for (var prop : props.values()) {
                tBody.append(TD_O);

                var value = prop.get(name);
                if (value != null) {
                    tBody.append(value);
                } else {
                    tBody.append(" ");
                }

                tBody.append(TD_C);
            }
            tBody.append(TR_C);
        }

        return tBody.toString();
    }

    private Set<String> getPropertyNames(Map<String, Properties> props) {
        var names = new LinkedHashSet<String>();
        for (var prop : props.values()) {
            for (Object key : prop.keySet()) {
                names.add(key.toString());
            }
        }
        return names;
    }
}

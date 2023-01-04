package com.lanar.inspektr;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static com.lanar.inspektr.HtmlTags.TD_C;
import static com.lanar.inspektr.HtmlTags.TD_O;
import static com.lanar.inspektr.HtmlTags.TH_C;
import static com.lanar.inspektr.HtmlTags.TH_O;
import static com.lanar.inspektr.HtmlTags.TR_0_W_CLASS;
import static com.lanar.inspektr.HtmlTags.TR_C;
import static com.lanar.inspektr.HtmlTags.TR_O;


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
        replacement = replacement.replace("\\{", "\\\\{").replace("}", "\\\\}");
        template = template.replace("PLACEHOLDER", replacement);
        var path = cfg.getTo();
        System.out.printf("Writing report to %s%n", path);
        File report = new File(path);
        try {
            Files.writeString(Path.of(report.getPath()), template);
            System.out.println("Done");
        } catch (IOException e) {
            throw new InspectrException("Failed to write report", e);
        }
    }

    private String loadTemplate() {
        try {
            var loader = ClassLoader.getSystemClassLoader();
            try(var is = loader.getResourceAsStream("index.html")) {
                if (is == null) {
                    throw new InspectrException("Failed to load template index.html");
                }
                try(var isr = new InputStreamReader(is);
                    var reader = new BufferedReader(isr)) {
                    return reader.lines().collect(Collectors.joining(System.lineSeparator()));
                }
            }
        } catch (IOException e) {
            throw new InspectrException("Failed to load template index.html", e);
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
            System.out.println(name);
            var tag = hasEqualValues(name) ? TR_0_W_CLASS : TR_O;
            tBody.append(tag);
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

    private boolean hasEqualValues(String name) {
        var value = props.values().stream().findFirst().get().get(name);
        for (var env : props.values()) {
            String nextValue = env.get(name).toString();
            if (!Objects.equals(value,nextValue)) {
                return false;
            }
            value = nextValue;
        }
        return true;
    }

    private Set<String> getPropertyNames(Map<String, Properties> props) {
        var names = new TreeSet<String>();
        for (var prop : props.values()) {
            for (Object key : prop.keySet()) {
                names.add(key.toString());
            }
        }
        return names;
    }
}

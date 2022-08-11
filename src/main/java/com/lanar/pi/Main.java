package com.lanar.pi;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static com.lanar.pi.HtmlTags.*;

public class Main {

    public static void main(String[] args) {
        var props = readFiles("E:\\code\\prop-inspector\\files");
        render(props);
    }

    private static void render(Map<String, Properties> props) {
        var template = loadTemplate();
        var replacement = renderHead(props.keySet()) + renderBody(props);
        template = template.replaceAll("PLACEHOLDER", replacement);
        var path = Paths.get("").toAbsolutePath() + "-report.html";
        System.out.printf("Writing report to %s%n", path);
        File report = new File(path);
        try {
            Files.writeString(Path.of(report.getPath()), template);
            System.out.println("Done");
        } catch (IOException e) {
            throw new RuntimeException("Failed to write report");
        }
    }

    private static String loadTemplate() {
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

    private static String renderHead(Set<String> keys) {
        StringBuilder tHead = new StringBuilder(TR_O);
        tHead.append(TH_O).append("Property / Env").append(TH_C);
        for (var key : keys) {
            tHead.append(TH_O);
            tHead.append(key);
            tHead.append(TH_C);
        }
        tHead.append(TR_C);
        return tHead.toString();
    }

    private static String renderBody(Map<String, Properties> props) {
        StringBuilder tBody = new StringBuilder();
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

    private static Set<String> getPropertyNames(Map<String, Properties> props) {
        var names = new LinkedHashSet<String>();
        for (var prop : props.values()) {
            for (Object key : prop.keySet()) {
                names.add(key.toString());
            }
        }
        return names;
    }

    private static Map<String, Properties> readFiles(String path) {
        var dir = new File(path);
        if (dir.listFiles() == null) {
            throw new RuntimeException(String.format("Cannot open directory %s", path));
        }

        var parsed = new HashMap<String, Properties>();
        for (File file : dir.listFiles()) {
            parsed.put(cleanFileName(file.getName()), readFile(file));
        }

        return parsed;
    }

    private static String cleanFileName(String fileName) {
        return fileName.split("\\.")[0].toUpperCase();
    }

    private static Properties readFile(File file) {
        try(var stream = new FileInputStream(file)) {
            var props = new Properties();
            props.load(stream);
            return props;
        } catch (Exception e) {
            throw new RuntimeException("Error loading file", e);
        }
    }
}

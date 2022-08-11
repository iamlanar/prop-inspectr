package com.lanar.inspektr;

import java.io.File;
import java.io.FileInputStream;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

public class FileReader {

    public static Map<String, Properties> readFiles(String path) {
        System.out.printf("Reading files from %s%n", path);
        var dir = new File(path);
        if (dir.listFiles() == null) {
            throw new RuntimeException(String.format("Cannot open directory %s", path));
        }

        var parsed = new TreeMap<String, Properties>();
        for (File file : dir.listFiles()) {
            parsed.put(cleanFileName(file.getName()), readFile(file));
        }

        return parsed;
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

    private static String cleanFileName(String fileName) {
        return fileName.split("\\.")[0].toUpperCase();
    }
}

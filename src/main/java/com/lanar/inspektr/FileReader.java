package com.lanar.inspektr;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

public class FileReader {
    private FileReader() {}

    public static Map<String, Properties> readFiles(String path) {
        System.out.printf("Reading files from %s%n", path);
        var dir = new File(path);
        var files = dir.listFiles(new PropertiesFileNameFilter());
        if (files == null) {
            throw new InspectrException(String.format("Cannot open directory %s", path));
        }

        var parsed = new TreeMap<String, Properties>();
        for (var file : files) {
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
            throw new InspectrException("Error loading file", e);
        }
    }

    private static String cleanFileName(String fileName) {
        return fileName.split("\\.")[0].toUpperCase();
    }

    private static class PropertiesFileNameFilter implements FilenameFilter {
        @Override
        public boolean accept(File dir, String name) {
            return name.toLowerCase().endsWith(".properties");
        }
    }
}

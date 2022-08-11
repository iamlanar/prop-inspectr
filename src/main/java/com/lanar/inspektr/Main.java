package com.lanar.inspektr;

public class Main {

    public static void main(String[] args) {
        var cfg = Arguments.fromArgs(args);
        var props = FileReader.readFiles(cfg.getFrom());
        new HtmlReport(props, cfg).render();
    }

}

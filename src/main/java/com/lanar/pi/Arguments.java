package com.lanar.pi;

public class Arguments {
    public static final String FROM_ARG = "--from";
    public static final String TO_ARG = "--to";

    private String from;
    private String to;

    public static Arguments fromArgs(String[] args) {
        if (args.length > 4) {
            throw new RuntimeException("Too many arguments");
        }

        if (args.length % 2 != 0) {
            throw new RuntimeException("Not enough arguments");
        }

        var arguments = new Arguments();
        for (int i = 0; i < args.length; i+=2) {
            if (FROM_ARG.equals(args[i])) {
                arguments.setFrom(args[i+1]);
            }
            if (TO_ARG.equals(args[i])) {
                arguments.setTo(args[i+1]);
            }
        }
        return arguments;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getFrom() {
        return from;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getTo() {
        return to;
    }
}

package com.example.ftpclient.LogAdapter;

import java.util.Date;

public class LogItem {
    private final String time;
    private final String content;

    public LogItem(String content) {
        this.time = new Date().toString();
        this.content = content;
    }

    public String getTime() {
        return time;
    }

    public String getContent() {
        return content;
    }
}
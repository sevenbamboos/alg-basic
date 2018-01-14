package com.samwang.demo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LogRepo {

    private List<String> logs;

    private LogRepo() { logs = new ArrayList<>(1000); }

    public static LogRepo global() { return new LogRepo(); }

    public LogRepo log(String s) { logs.add(s); return this; }

    public List<String> logs() { return Collections.unmodifiableList(logs); }
}

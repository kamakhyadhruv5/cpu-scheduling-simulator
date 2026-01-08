package com.cpu.backend.model;
public class GanttEntry {

    public String pid;
    public int start;
    public int end;

    public GanttEntry(String pid, int start, int end) {
        this.pid = pid;
        this.start = start;
        this.end = end;
    }
}

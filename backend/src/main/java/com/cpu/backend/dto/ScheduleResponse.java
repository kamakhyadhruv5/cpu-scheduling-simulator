package com.cpu.backend.dto; // Ensure this matches your folder exactly

import java.util.List;
import com.cpu.backend.model.Process;
import com.cpu.backend.model.GanttEntry;

public class ScheduleResponse {
    public List<Process> processes;
    public List<GanttEntry> gantt;
    public double avgWT; // Added semicolon
    public double avgTAT; // Added semicolon
    public double avgRT; // Added semicolon
}
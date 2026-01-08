package com.cpu.backend.algorithms;

import java.util.List;
import com.cpu.backend.model.Process;
import com.cpu.backend.model.GanttEntry;

public class SchedulingResult {
    public List<Process> processes;   // Process list with CT, WT, TAT, RT
    public List<GanttEntry> gantt;    // Gantt chart
    public double avgWT;
    public double avgTAT;
    public double avgRT;
}

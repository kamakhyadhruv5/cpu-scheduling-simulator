package com.cpu.backend.algorithms;

import com.cpu.backend.model.Process;
import com.cpu.backend.model.GanttEntry;

import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;

public class FCFS implements Scheduler {

    @Override
    public SchedulingResult schedule(List<Process> processes) {

        // Sort by arrival time
        processes.sort(Comparator.comparingInt(p -> p.arrivalTime));

        List<GanttEntry> gantt = new ArrayList<>();

        int time = 0;
        double wt = 0;
        double tat = 0;
        double rt = 0;

        for (Process p : processes) {

            // CPU idle
            if (time < p.arrivalTime) {
                gantt.add(new GanttEntry("IDLE", time, p.arrivalTime));
                time = p.arrivalTime;
            }

            // Response time
            p.responseTime = time - p.arrivalTime;

            // Waiting time (FCFS = non-preemptive)
            p.waitingTime = p.responseTime;

            // Execution
            gantt.add(new GanttEntry(p.pid, time, time + p.burstTime));
            time += p.burstTime;

            // Completion & Turnaround
            p.completionTime = time;
            p.turnaroundTime = p.completionTime - p.arrivalTime;

            // Totals
            wt += p.waitingTime;
            tat += p.turnaroundTime;
            rt += p.responseTime;
        }

        // Build result
        SchedulingResult res = new SchedulingResult();
        res.processes = processes;
        res.gantt = gantt;
        res.avgWT = wt / processes.size();
        res.avgTAT = tat / processes.size();
        res.avgRT = rt / processes.size();

        return res;
    }
}

package com.cpu.backend.algorithms;

import com.cpu.backend.model.Process;
import com.cpu.backend.model.GanttEntry;

import java.util.List;
import java.util.ArrayList;


public class SJF implements Scheduler {

    @Override
    public SchedulingResult schedule(List<Process> processes) {
        List<GanttEntry> gantt = new ArrayList<>();
        int n = processes.size();
        int completed = 0, time = 0;
        boolean[] isCompleted = new boolean[n];
        double wt = 0, tat = 0, rt = 0;

        while (completed < n) {
            int idx = -1;
            int minBT = Integer.MAX_VALUE;

            for (int i = 0; i < n; i++) {
                Process p = processes.get(i);
                if (p.arrivalTime <= time && !isCompleted[i] && p.burstTime < minBT) {
                    minBT = p.burstTime;
                    idx = i;
                }
            }

            if (idx == -1) {
                // CPU idle
                int nextArrival = Integer.MAX_VALUE;
                for (int i = 0; i < n; i++) {
                    if (!isCompleted[i] && processes.get(i).arrivalTime < nextArrival) {
                        nextArrival = processes.get(i).arrivalTime;
                    }
                }
                gantt.add(new GanttEntry("IDLE", time, nextArrival));
                time = nextArrival;
                continue;
            }

            Process p = processes.get(idx);
            p.responseTime = time - p.arrivalTime;
            p.waitingTime = time - p.arrivalTime;
            gantt.add(new GanttEntry(p.pid, time, time + p.burstTime));
            time += p.burstTime;
            p.completionTime = time;
            p.turnaroundTime = p.completionTime - p.arrivalTime;

            wt += p.waitingTime;
            tat += p.turnaroundTime;
            rt += p.responseTime;

            isCompleted[idx] = true;
            completed++;
        }

        SchedulingResult res = new SchedulingResult();
        res.processes = processes;
        res.gantt = gantt;
        res.avgWT = wt / n;
        res.avgTAT = tat / n;
        res.avgRT = rt / n;

        return res;
    }
}

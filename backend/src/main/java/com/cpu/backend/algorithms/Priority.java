package com.cpu.backend.algorithms;

import com.cpu.backend.model.Process;
import com.cpu.backend.model.GanttEntry;

import java.util.List;
import java.util.ArrayList;


public class Priority implements Scheduler {

    private boolean preemptive;
    public Priority() {
        this.preemptive = false; // Non-preemptive by default
    }

    public Priority(boolean preemptive) {
        this.preemptive = preemptive;
    }

    @Override
    public SchedulingResult schedule(List<Process> processes) {
        List<GanttEntry> gantt = new ArrayList<>();
        int n = processes.size();
        int completed = 0, time = 0;
        double wt = 0, tat = 0, rt = 0;
        boolean[] isCompleted = new boolean[n];

        while (completed < n) {
            int idx = -1;
            int minPr = Integer.MAX_VALUE;

            for (int i = 0; i < n; i++) {
                Process p = processes.get(i);
                if (p.arrivalTime <= time && !isCompleted[i] && p.remainingTime > 0 && p.priority < minPr) {
                    minPr = p.priority;
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
            if (!p.started) {
                p.responseTime = time - p.arrivalTime;
                p.started = true;
            }

            if (preemptive) {
                gantt.add(new GanttEntry(p.pid, time, time + 1));
                time++;
                p.remainingTime--;
                if (p.remainingTime == 0) {
                    p.completionTime = time;
                    p.turnaroundTime = p.completionTime - p.arrivalTime;
                    p.waitingTime = p.turnaroundTime - p.burstTime;

                    wt += p.waitingTime;
                    tat += p.turnaroundTime;
                    rt += p.responseTime;

                    isCompleted[idx] = true;
                    completed++;
                }
            } else {
                gantt.add(new GanttEntry(p.pid, time, time + p.burstTime));
                time += p.burstTime;
                p.completionTime = time;
                p.turnaroundTime = p.completionTime - p.arrivalTime;
                p.waitingTime = p.turnaroundTime - p.burstTime;

                wt += p.waitingTime;
                tat += p.turnaroundTime;
                rt += p.responseTime;

                isCompleted[idx] = true;
                completed++;
            }
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

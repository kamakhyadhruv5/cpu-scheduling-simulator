package com.cpu.backend.algorithms;

import com.cpu.backend.model.Process;
import com.cpu.backend.model.GanttEntry;

import java.util.List;
import java.util.ArrayList;

import java.util.Queue;
import java.util.LinkedList;


public class RoundRobin implements Scheduler {

    private int quantum;

    public RoundRobin(int quantum) {
        this.quantum = quantum;
    }

    @Override
    public SchedulingResult schedule(List<Process> processes) {
        List<GanttEntry> gantt = new ArrayList<>();
        Queue<Process> queue = new LinkedList<>();
        int time = 0, completed = 0;
        int n = processes.size();
        boolean[] inQueue = new boolean[n];
        double wt = 0, tat = 0, rt = 0;

        while (completed < n) {
            for (int i = 0; i < n; i++) {
                Process p = processes.get(i);
                if (p.arrivalTime <= time && !inQueue[i] && p.remainingTime > 0) {
                    queue.add(p);
                    inQueue[i] = true;
                }
            }

            if (queue.isEmpty()) {
                int nextArrival = Integer.MAX_VALUE;
                for (int i = 0; i < n; i++) {
                    if (processes.get(i).remainingTime > 0 && processes.get(i).arrivalTime < nextArrival)
                        nextArrival = processes.get(i).arrivalTime;
                }
                gantt.add(new GanttEntry("IDLE", time, nextArrival));
                time = nextArrival;
                continue;
            }

            Process p = queue.poll();
            if (!p.started) {
                p.responseTime = time - p.arrivalTime;
                p.started = true;
            }

            int exec = Math.min(quantum, p.remainingTime);
            gantt.add(new GanttEntry(p.pid, time, time + exec));
            time += exec;
            p.remainingTime -= exec;

            for (int i = 0; i < n; i++) {
                Process tmp = processes.get(i);
                if (tmp.arrivalTime <= time && !inQueue[i] && tmp.remainingTime > 0) {
                    queue.add(tmp);
                    inQueue[i] = true;
                }
            }

            if (p.remainingTime > 0) {
                queue.add(p); // re-add to end of queue
            } else {
                p.completionTime = time;
                p.turnaroundTime = p.completionTime - p.arrivalTime;
                p.waitingTime = p.turnaroundTime - p.burstTime;

                wt += p.waitingTime;
                tat += p.turnaroundTime;
                rt += p.responseTime;

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

package com.cpu.backend.model;

public class Process {

    // ===== INPUT VALUES (Given by user) =====
    public String pid;
    public int arrivalTime;
    public int burstTime;
    public int priority;

    // ===== CALCULATED VALUES (Output) =====
    public int completionTime;
    public int waitingTime;
    public int turnaroundTime;
    public int responseTime;

    // ===== INTERNAL VALUES (Used by algorithms) =====
    public int remainingTime;
    public boolean started = false;

    public Process(String pid, int at, int bt, int pr) {
        this.pid = pid;
        this.arrivalTime = at;
        this.burstTime = bt;
        this.priority = pr;
        this.remainingTime = bt;
    }
}

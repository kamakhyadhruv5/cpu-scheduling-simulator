package com.cpu.backend.controller;

import com.cpu.backend.dto.*;
import com.cpu.backend.algorithms.*;
import com.cpu.backend.model.Process;

import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@CrossOrigin(origins = "*") // VERY IMPORTANT FOR FRONTEND
@RequestMapping("/api")
public class SchedulerController {

    @PostMapping("/schedule")
    public ScheduleResponse schedule(@RequestBody ScheduleRequest request) {

        // 1️⃣ Convert DTO → Model
        List<Process> processList = new ArrayList<>();
        for (ProcessDTO dto : request.processes) {
            processList.add(new Process(
                    dto.pid,
                    dto.arrivalTime,
                    dto.burstTime,
                    dto.priority
            ));
        }

        // 2️⃣ Choose Scheduler Algorithm
        Scheduler scheduler;

        switch (request.algorithm) {
            case "SJF":
                scheduler = new SJF();
                break;
            case "SRTF":
                scheduler = new SRTF();
                break;
            case "PRIORITY":
                scheduler = new Priority();
                break;
            case "RR":
                scheduler = new RoundRobin(request.quantum);
                break;
            default:
                scheduler = new FCFS();
        }

        // 3️⃣ Run Scheduling
        SchedulingResult result = scheduler.schedule(processList);

        // 4️⃣ Map Algorithm Result → API Response
        ScheduleResponse response = new ScheduleResponse();
        response.processes = result.processes;
        response.gantt = result.gantt;
        response.avgWT= result.avgWT;
        response.avgTAT = result.avgTAT;
        response.avgRT = result.avgRT;

        return response;
    }
}

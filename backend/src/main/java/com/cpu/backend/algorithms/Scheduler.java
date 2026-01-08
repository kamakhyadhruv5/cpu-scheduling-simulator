package com.cpu.backend.algorithms;

import java.util.List;
import com.cpu.backend.model.Process;

public interface Scheduler {
    SchedulingResult schedule(List<Process> processes);
}

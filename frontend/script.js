let selectedAlgo = "FCFS";

/* ================= Clear Output ================= */
function clearOutput() {
  document.getElementById("ganttChart").innerHTML = "";
  document.getElementById("ganttTimeline").innerHTML = "";
  document.getElementById("metrics").innerHTML = "";
  document.querySelector("#resultTable tbody").innerHTML = "";
}

/* ================= Algorithm Buttons ================= */
document.querySelectorAll(".algo").forEach(btn => {
  btn.onclick = () => {
    // Active button handling
    document.querySelectorAll(".algo").forEach(b => b.classList.remove("active"));
    btn.classList.add("active");

    selectedAlgo = btn.dataset.algo;

    // Clear previous output
    clearOutput();

    const priorityCol = document.querySelector(".priority-col");
    const priorityNote = document.getElementById("priorityNote");
    const isPriority = selectedAlgo === "PRIORITY";

    // Priority column handling
    priorityCol.style.display = isPriority ? "table-cell" : "none";
    priorityCol.textContent = isPriority
      ? "Priority (Higher = More)"
      : "Priority";

    document.querySelectorAll(".priority-data").forEach(td => {
      td.style.display = isPriority ? "table-cell" : "none";
    });

    // Priority info note
    if (priorityNote) {
      priorityNote.style.display = isPriority ? "block" : "none";
    }

    // RR Quantum box
    document.getElementById("quantumBox").classList.toggle(
      "show",
      selectedAlgo === "RR"
    );
  };
});


/* ================= Add Process ================= */
function addProcess() {
  const tbody = document.querySelector("#processTable tbody");
  const index = tbody.rows.length + 1;

  const displayStyle = selectedAlgo === "PRIORITY" ? "table-cell" : "none";

  const row = document.createElement("tr");
  row.innerHTML = `
    <td><input value="P${index}"></td>
    <td><input type="number" placeholder="0"></td>
    <td><input type="number" placeholder="0"></td>
    <td class="priority-data" style="display:${displayStyle}">
      <input type="number" placeholder="0">
    </td>
    <td><button onclick="this.closest('tr').remove()">❌</button></td>
  `;
  
  tbody.appendChild(row);
  renumberPIDs();
}

/* ================= Run Simulation ================= */
function runSimulation() {
  const rows = document.querySelectorAll("#processTable tbody tr");
  let processes = [];

  rows.forEach(r => {
    const cells = r.querySelectorAll("input");
    processes.push({
      pid: cells[0].value,
      arrivalTime: +cells[1].value,
      burstTime: +cells[2].value,
      priority: selectedAlgo === "PRIORITY" ? +cells[3].value : 0
    });
  });

  const payload = {
    algorithm: selectedAlgo,
    quantum: selectedAlgo === "RR"
      ? +document.getElementById("quantum").value
      : 0,
    processes
  };

  fetch("https://cpu-scheduling-simulator-m8kk.onrender.com/api/schedule", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(payload)
  })
    .then(res => res.json())
    .then(data => {
      renderGantt(data.gantt);
      showMetrics(data.avgWT, data.avgTAT, data.avgRT);
      showProcessMetrics(data.processes);
    })
    .catch(err => console.error(err));
}

/* ================= Gantt Chart ================= */
function renderGantt(gantt) {
  const chart = document.getElementById("ganttChart");
  const timeline = document.getElementById("ganttTimeline");

  chart.innerHTML = "";
  timeline.innerHTML = "";

  gantt.forEach((g, index) => {
    const width = (g.end - g.start) * 40;

    const time = document.createElement("div");
    time.style.width = width + "px";
    time.textContent = g.start;
    timeline.appendChild(time);

    const block = document.createElement("div");
    block.className = "gantt-block";
    block.style.width = width + "px";
    block.textContent = g.pid;
    chart.appendChild(block);

    if (index === gantt.length - 1) {
      const end = document.createElement("div");
      end.textContent = g.end;
      timeline.appendChild(end);
    }
  });
}

/* ================= Metrics ================= */
function showMetrics(wt, tat, rt) {
  document.getElementById("metrics").innerHTML = `
    <p><b>Average Waiting Time:</b> ${wt.toFixed(2)}</p>
    <p><b>Average Turnaround Time:</b> ${tat.toFixed(2)}</p>
    <p><b>Average Response Time:</b> ${rt.toFixed(2)}</p>
  `;
}

/* ================= Process Metrics ================= */
function showProcessMetrics(processes) {
  const tbody = document.querySelector("#resultTable tbody");
  tbody.innerHTML = "";

  processes.forEach(p => {
    const row = document.createElement("tr");
    row.innerHTML = `
      <td>${p.pid}</td>
      <td>${p.waitingTime}</td>
      <td>${p.turnaroundTime}</td>
      <td>${p.responseTime}</td>
    `;
    tbody.appendChild(row);
  });
}
if (selectedAlgo === "PRIORITY") {
  document.querySelector(".priority-col").textContent =
    "Priority (Higher = More)";
}
function renumberPIDs() {
  const rows = document.querySelectorAll("#processTable tbody tr");
  rows.forEach((row, index) => {
    row.querySelector("td input").value = `P${index + 1}`;
  });
}

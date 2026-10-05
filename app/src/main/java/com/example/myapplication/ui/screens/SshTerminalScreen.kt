package com.example.myapplication.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.model.VpsInstance

@Composable
fun SshTerminalScreen(
    instance: VpsInstance,
    onBack: () -> Unit
) {
    var commandInput by remember { mutableStateOf("") }
    val terminalOutput = remember {
        mutableStateListOf(
            "Welcome to Ubuntu 24.04 LTS (GNU/Linux 6.8.0-31-generic x86_64)",
            " * Documentation: https://help.babalcloud.com",
            " * Management:  https://console.babalcloud.com/vps/ktm01",
            "",
            "[OK] Babal KVM-Agent v3.1.2 operational. Cloud-init finished in 2.148s.",
            "",
            "root@prod-api-ktm01:~# docker ps -a",
            "CONTAINER ID   IMAGE                   COMMAND                  CREATED      STATUS",
            "7c12f08a8b19   babal/api-gateway:v2    \"/entrypoint.sh run\"     7 weeks ago  Up 42 hours",
            "3d99e0a294fc   redis:7.2-alpine        \"docker-entrypoint.s...\" 7 weeks ago  Up 42 hours",
            "81ea0283c411   babal/queue-worker:v1   \"python manage.py run\"   7 weeks ago  Up 42 hours",
            "",
            "root@prod-api-ktm01:~# "
        )
    }

    val runCommand: (String) -> Unit = { cmd ->
        terminalOutput.add("root@prod-api-ktm01:~# $cmd")
        when (cmd) {
            "docker ps -a" -> {
                terminalOutput.add("CONTAINER ID   IMAGE                   COMMAND                  CREATED      STATUS")
                terminalOutput.add("7c12f08a8b19   babal/api-gateway:v2    \"/entrypoint.sh run\"     7 weeks ago  Up 42 hours")
                terminalOutput.add("3d99e0a294fc   redis:7.2-alpine        \"docker-entrypoint.s...\" 7 weeks ago  Up 42 hours")
                terminalOutput.add("81ea0283c411   babal/queue-worker:v1   \"python manage.py run\"   7 weeks ago  Up 42 hours")
            }
            "systemctl status nginx" -> {
                terminalOutput.add("● nginx.service - A high performance web server and a reverse proxy server")
                terminalOutput.add("     Loaded: loaded (/lib/systemd/system/nginx.service; enabled)")
                terminalOutput.add("     Active: active (running) since Sun 2024-09-08 12:00:10 NPT; 48 days ago")
                terminalOutput.add("   Main PID: 989 (nginx)")
                terminalOutput.add("      Tasks: 5 (limit: 9482)")
                terminalOutput.add("     Memory: 28.4M")
            }
            "top summary" -> {
                terminalOutput.add("Tasks: 164 total, 1 running, 163 sleeping, 0 stopped, 0 zombie")
                terminalOutput.add("%Cpu(s): 3.8 us,  1.2 sy,  0.0 ni, 94.8 id,  0.1 wa,  0.0 hi,  0.1 si")
                terminalOutput.add("MiB Mem :   8000.0 total,   2800.0 free,   4160.0 used,   1040.0 buff/cache")
            }
            "free -m -h" -> {
                terminalOutput.add("               total        used        free      shared  buff/cache   available")
                terminalOutput.add("Mem:           7.8Gi       4.1Gi       2.7Gi       128Mi       1.0Gi       3.4Gi")
                terminalOutput.add("Swap:          4.0Gi       180Mi       3.8Gi")
            }
            "journalctl babal-agent" -> {
                terminalOutput.add("Sep 08 12:00:10 prod-api-ktm01 systemd[1]: Started Babal Agent daemon v3.1.2.")
                terminalOutput.add("Sep 08 12:00:11 prod-api-ktm01 babal-agent[912]: [INFO] Telemetry mesh connected to KTM-1 edge.")
            }
            "ufw status" -> {
                terminalOutput.add("Status: active")
                terminalOutput.add("To                         Action      From")
                terminalOutput.add("--                         ------      ----")
                terminalOutput.add("22/tcp                     ALLOW       202.70.76.12")
                terminalOutput.add("80/tcp                     ALLOW       Anywhere")
                terminalOutput.add("443/tcp                    ALLOW       Anywhere")
            }
            "git pull && compose restart" -> {
                terminalOutput.add("Already up to date.")
                terminalOutput.add("Restarting api-gateway, redis, queue-worker... [OK]")
            }
            "certbot renew --dry-run" -> {
                terminalOutput.add("Saving debug log to /var/log/letsencrypt/letsencrypt.log")
                terminalOutput.add("Simulating renewal of existing certificates for prod-api.babal.cloud")
                terminalOutput.add("Congratulations, all simulated renewals succeeded! [OK]")
            }
            "redis-cli FLUSHDB" -> {
                terminalOutput.add("OK")
            }
            else -> {
                terminalOutput.add("bash: $cmd: command not found. Type 'help' for available commands.")
            }
        }
        terminalOutput.add("root@prod-api-ktm01:~# ")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0B0F19))
            .padding(16.dp)
    ) {
        // Connection Header Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(onClick = onBack, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Text("root@${instance.name}", color = Color(0xFF34D399), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Text("•", color = Color(0xFF9CA3AF))
                Text(instance.publicIp, color = Color(0xFF38BDF8), fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                TerminalBadge("WSS TLS 1.3 (4ms RTT)", Color(0xFF8B5CF6))
                TerminalBadge("Session #1 • PTY 138x42", Color(0xFFF59E0B))
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // One-Tap Quick Macros Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("QUICK MACROS:", color = Color(0xFF9CA3AF), fontSize = 11.sp, fontWeight = FontWeight.Bold)
            listOf("docker ps -a", "systemctl status nginx", "top summary", "free -m -h", "journalctl babal-agent", "ufw status").forEach { macro ->
                Surface(
                    modifier = Modifier.clickable { runCommand(macro) },
                    color = Color(0xFF1E293B),
                    shape = RoundedCornerShape(6.dp),
                    border = BorderStroke(1.dp, Color(0xFF334155))
                ) {
                    Text(
                        text = macro,
                        color = Color(0xFF38BDF8),
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Main Mac-styled Terminal & Sidebar Layout
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Mac-styled Terminal Window Box
            Surface(
                modifier = Modifier
                    .weight(2f)
                    .height(480.dp),
                color = Color(0xFF0F172A),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color(0xFF334155))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    // Mac Traffic Lights Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(Color(0xFFEF4444)))
                            Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(Color(0xFFF59E0B)))
                            Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(Color(0xFF10B981)))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("bash — 138x42 — babal-cli v2.4 (KTM-1)", color = Color(0xFF9CA3AF), fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                        }
                        Text("PTY /dev/pts/3", color = Color(0xFF64748B), fontSize = 11.sp)
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider(color = Color(0xFF1E293B))
                    Spacer(modifier = Modifier.height(8.dp))

                    // Terminal Log Lines with Syntax Highlighting
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        items(terminalOutput) { line ->
                            val textColor = when {
                                line.startsWith("root@") -> Color(0xFF34D399)
                                line.startsWith("[OK]") -> Color(0xFF38BDF8)
                                line.contains("active (running)") || line.contains("OK") -> Color(0xFF34D399)
                                line.startsWith("CONTAINER ID") -> Color(0xFFFBBF24)
                                else -> Color(0xFFE2E8F0)
                            }
                            Text(
                                text = line,
                                color = textColor,
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Command Input Field
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF1E293B), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("root@prod-api-ktm01:~# ", color = Color(0xFF34D399), fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                        OutlinedTextField(
                            value = commandInput,
                            onValueChange = { commandInput = it },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            singleLine = true
                        )
                        Button(
                            onClick = {
                                if (commandInput.isNotBlank()) {
                                    val cmd = commandInput
                                    commandInput = ""
                                    runCommand(cmd)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEA580C)),
                            shape = RoundedCornerShape(4.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Text("Run", fontSize = 11.sp, color = Color.White)
                        }
                    }
                }
            }

            // Right Sidebar: Listening Ports, DevOps Runbooks & Telemetry
            Column(
                modifier = Modifier
                    .weight(1f)
                    .height(480.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Listening Ports Card
                Surface(
                    color = Color(0xFF111827),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color(0xFF1F2937))
                ) {
                    Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Listening Ports", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Surface(color = Color(0xFF10B981).copy(alpha = 0.2f), shape = RoundedCornerShape(4.dp)) {
                                Text("3 Active", color = Color(0xFF34D399), fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                            }
                        }
                        ListeningPortRow("Port 22", "SSH (OpenSSH 9.6p1)", "0.0.0.0:*")
                        ListeningPortRow("Port 80", "HTTP (Nginx reverse)", ":::80")
                        ListeningPortRow("Port 443", "HTTPS (TLS v1.3 Edge)", ":::443")
                    }
                }

                // DevOps Runbooks
                Surface(
                    color = Color(0xFF111827),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color(0xFF1F2937))
                ) {
                    Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("DevOps Runbooks", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        RunbookItem("Deploy Git Webhook", "git pull && compose restart") {
                            runCommand("git pull && compose restart")
                        }
                        RunbookItem("Renew Certbot SSL", "certbot renew --dry-run") {
                            runCommand("certbot renew --dry-run")
                        }
                        RunbookItem("Flush Redis Cache", "redis-cli FLUSHDB") {
                            runCommand("redis-cli FLUSHDB")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TerminalBadge(text: String, color: Color) {
    Surface(
        color = color.copy(alpha = 0.2f),
        shape = RoundedCornerShape(6.dp),
        border = BorderStroke(1.dp, color.copy(alpha = 0.5f))
    ) {
        Text(
            text = text,
            color = color,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun ListeningPortRow(port: String, service: String, bind: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF1F2937), RoundedCornerShape(4.dp))
            .padding(6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(Color(0xFF10B981)))
            Column {
                Text(port, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Text(service, color = Color(0xFF9CA3AF), fontSize = 9.sp)
            }
        }
        Text(bind, color = Color(0xFF38BDF8), fontSize = 9.sp, fontFamily = FontFamily.Monospace)
    }
}

@Composable
fun RunbookItem(title: String, subtitle: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .background(Color(0xFF1F2937), RoundedCornerShape(6.dp))
            .padding(6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(title, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Text(subtitle, color = Color(0xFF9CA3AF), fontSize = 9.sp, fontFamily = FontFamily.Monospace)
        }
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color(0xFF9CA3AF), modifier = Modifier.size(16.dp))
    }
}

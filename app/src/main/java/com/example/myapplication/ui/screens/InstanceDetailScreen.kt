package com.example.myapplication.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.model.FirewallRule
import com.example.myapplication.model.MockData
import com.example.myapplication.model.VpsInstance

@Composable
fun EnvCard(
    modifier: Modifier = Modifier,
    title: String,
    badge: String,
    badgeColor: Color,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier,
        color = Color(0xFF111827),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color(0xFF1F2937))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(title, color = Color(0xFF9CA3AF), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Surface(
                    color = badgeColor.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = badge,
                        color = badgeColor,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            content()
        }
    }
}

@Composable
fun TelemetryBox(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    badge: String,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier,
        color = Color(0xFF111827),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color(0xFF1F2937))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(title, color = Color(0xFF9CA3AF), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Surface(
                    color = Color(0xFFEA580C).copy(alpha = 0.2f),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = badge,
                        color = Color(0xFFFDBA74),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(value, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            content()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InstanceDetailScreen(
    instance: VpsInstance,
    onBack: () -> Unit,
    onOpenTerminal: () -> Unit
) {
    var showAddFirewallDialog by remember { mutableStateOf(false) }
    val firewallRulesList = remember { mutableStateListOf(*MockData.firewallRules.toTypedArray()) }
    var newPort by remember { mutableStateOf("") }
    var newRuleName by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0B0F19))
            .padding(16.dp)
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(onClick = onBack, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Text(
                    text = "CONSOLE > COMPUTE > VPS INSTANCES > ",
                    color = Color(0xFF9CA3AF),
                    fontSize = 12.sp
                )
                Text(
                    text = instance.name,
                    color = Color(0xFF38BDF8),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = onOpenTerminal,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEA580C)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(36.dp)
                ) {
                    Icon(Icons.Default.Terminal, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Web SSH Terminal", fontSize = 12.sp, color = Color.White)
                }
                OutlinedButton(
                    onClick = { /* Snapshot */ },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFF374151)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(36.dp)
                ) {
                    Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Take Snapshot", fontSize = 12.sp)
                }
                OutlinedButton(
                    onClick = { /* Reboot */ },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFF374151)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(36.dp)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Reboot", fontSize = 12.sp)
                }
                OutlinedButton(
                    onClick = { /* Power Off */ },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFEF4444)),
                    border = BorderStroke(1.dp, Color(0xFFEF4444).copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(36.dp)
                ) {
                    Icon(Icons.Default.PowerSettingsNew, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color(0xFFEF4444))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Power Off", fontSize = 12.sp, color = Color(0xFFEF4444))
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Instance Title & Status bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = instance.name,
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Surface(
                color = Color(0xFF1F2937),
                shape = RoundedCornerShape(6.dp)
            ) {
                Text(
                    text = instance.code,
                    color = Color(0xFF9CA3AF),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
            Surface(
                color = Color(0xFF10B981).copy(alpha = 0.2f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF10B981))
                    )
                    Text(instance.status, color = Color(0xFF34D399), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Surface(
                color = Color(0xFF1E293B),
                shape = RoundedCornerShape(6.dp)
            ) {
                Text(
                    text = "KVM HYPERVISOR V9.1    📍 Kathmandu-01 DC (Rack E-14)",
                    color = Color(0xFF38BDF8),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Hardware & Environment Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            EnvCard(
                modifier = Modifier.weight(1f),
                title = "NETWORKING & IPS",
                badge = "10 Gbps Uplink",
                badgeColor = Color(0xFF38BDF8)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Public IPv4: ${instance.publicIp}", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Text("Private VPC: ${instance.privateIp}", color = Color(0xFF9CA3AF), fontSize = 11.sp)
                    Text("Gateway: 103.112.54.1", color = Color(0xFF9CA3AF), fontSize = 11.sp)
                }
            }

            EnvCard(
                modifier = Modifier.weight(1f),
                title = "COMPUTE HARDWARE",
                badge = "Standard Compute",
                badgeColor = Color(0xFFF59E0B)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("AMD EPYC™ 9654 Gen 4 @ 3.7 GHz", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Text("4 vCPU Core   •   8 GB DDR5 ECC 5600", color = Color(0xFF9CA3AF), fontSize = 11.sp)
                    Text("160 GB NVMe PCIe 4.0 SAN", color = Color(0xFF9CA3AF), fontSize = 11.sp)
                }
            }

            EnvCard(
                modifier = Modifier.weight(1f),
                title = "OS & ENVIRONMENT",
                badge = "Up to date",
                badgeColor = Color(0xFF10B981)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Ubuntu 24.04 LTS x86_64", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Text("Kernel: 6.8.0-31-generic", color = Color(0xFF9CA3AF), fontSize = 11.sp)
                    Text("Docker v26.1   •   cloud-init v24.1", color = Color(0xFF9CA3AF), fontSize = 11.sp)
                }
            }

            EnvCard(
                modifier = Modifier.weight(1f),
                title = "SLA & PROTECTION",
                badge = "99.98% SLA",
                badgeColor = Color(0xFF8B5CF6)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Backup: Daily @ 02:00 NPT (14 Retained)", color = Color.White, fontSize = 11.sp)
                    Text("DDoS Mitigation: Always On", color = Color(0xFF10B981), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Text("Failover Node: ktm01-no...", color = Color(0xFF9CA3AF), fontSize = 11.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Real-Time Telemetry & Performance Gauges section
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFF10B981)))
                Text("Real-time Telemetry & Performance Gauges", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf("1h", "6h", "24h", "7d", "30d").forEach { time ->
                    Surface(
                        color = if (time == "24h") Color(0xFFEA580C) else Color(0xFF1F2937),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = time,
                            color = if (time == "24h") Color.White else Color(0xFF9CA3AF),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 4 Telemetry Gauges / Cards Grid
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TelemetryBox(modifier = Modifier.weight(1f), title = "CPU UTILIZATION", value = "${instance.cpuUsagePercent}%", badge = "Normal") {
                Column {
                    Text("Baseline: 38% • Peak: 78% • Steal: 0.1%", color = Color(0xFF9CA3AF), fontSize = 10.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { instance.cpuUsagePercent / 100f },
                        modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                        color = Color(0xFFEA580C),
                        trackColor = Color(0xFF374151)
                    )
                }
            }

            TelemetryBox(modifier = Modifier.weight(1f), title = "MEMORY ALLOCATION", value = "${instance.ramUsageGb} / ${instance.ramTotalGb} GB", badge = "65% Active") {
                Column {
                    Text("Swap: 180 MB / 4.00 GB", color = Color(0xFF9CA3AF), fontSize = 10.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { (instance.ramUsageGb / instance.ramTotalGb).toFloat() },
                        modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                        color = Color(0xFF3B82F6),
                        trackColor = Color(0xFF374151)
                    )
                }
            }

            TelemetryBox(modifier = Modifier.weight(1f), title = "STORAGE & I/O OPERATIONS", value = "${instance.storageUsageGb} / ${instance.storageTotalGb} GB", badge = "58% Utilized") {
                Column {
                    Text("Read: 12,400 IOPS • Write: 8,200 IOPS", color = Color(0xFF9CA3AF), fontSize = 10.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { (instance.storageUsageGb / instance.storageTotalGb).toFloat() },
                        modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                        color = Color(0xFF8B5CF6),
                        trackColor = Color(0xFF374151)
                    )
                }
            }

            TelemetryBox(modifier = Modifier.weight(1f), title = "NETWORK BANDWIDTH", value = "48.2 Mbps In / 112.4 Mbps Out", badge = "Direct IXP") {
                Column {
                    Text("Monthly Tier: 1.82 TB of 5.00 TB", color = Color(0xFF9CA3AF), fontSize = 10.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { 0.36f },
                        modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                        color = Color(0xFF10B981),
                        trackColor = Color(0xFF374151)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Firewall Rules & Syslog Stream section
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Surface(
                modifier = Modifier.weight(1f),
                color = Color(0xFF111827),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color(0xFF1F2937))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Default.Security, contentDescription = null, tint = Color(0xFFEA580C), modifier = Modifier.size(16.dp))
                            Text("Firewall Rules & Policies", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                        Surface(color = Color(0xFF10B981).copy(alpha = 0.2f), shape = RoundedCornerShape(4.dp)) {
                            Text("Active: ${firewallRulesList.size} Inbound", color = Color(0xFF34D399), fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    LazyColumn(
                        modifier = Modifier.height(150.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(firewallRulesList) { rule ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFF1F2937), RoundedCornerShape(6.dp))
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Surface(color = Color(0xFF374151), shape = RoundedCornerShape(4.dp)) {
                                        Text("${rule.port} / ${rule.protocol}", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                    }
                                    Column {
                                        Text(rule.name, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                        Text(rule.source, color = Color(0xFF9CA3AF), fontSize = 10.sp)
                                    }
                                }
                                Surface(
                                    color = if (rule.status == "ALLOWED") Color(0xFF10B981).copy(alpha = 0.2f) else Color(0xFF3B82F6).copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = rule.status,
                                        color = if (rule.status == "ALLOWED") Color(0xFF34D399) else Color(0xFF93C5FD),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedButton(
                        onClick = { showAddFirewallDialog = true },
                        modifier = Modifier.fillMaxWidth().height(36.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                        border = BorderStroke(1.dp, Color(0xFF374151)),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Add Firewall Inbound Rule", fontSize = 11.sp)
                    }
                }
            }

            Surface(
                modifier = Modifier.weight(1f),
                color = Color(0xFF111827),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color(0xFF1F2937))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFFEF4444)))
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFFF59E0B)))
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFF10B981)))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Live Event & Syslog Stream", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                        Surface(color = Color(0xFF10B981).copy(alpha = 0.2f), shape = RoundedCornerShape(4.dp)) {
                            Text("TAIL STREAM", color = Color(0xFF34D399), fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    LazyColumn(
                        modifier = Modifier.height(190.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(MockData.syslogEntries) { log ->
                            Text(
                                text = "[${log.timestamp}] ${log.source}: ${log.message}",
                                color = if (log.level == "success") Color(0xFF34D399) else Color(0xFF9CA3AF),
                                fontSize = 11.sp,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
    }

    if (showAddFirewallDialog) {
        AlertDialog(
            onDismissRequest = { showAddFirewallDialog = false },
            title = { Text("Add Custom Firewall Inbound Rule", color = Color.White) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = newPort,
                        onValueChange = { newPort = it },
                        label = { Text("Port / Protocol (e.g. 8080 / TCP)") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                    OutlinedTextField(
                        value = newRuleName,
                        onValueChange = { newRuleName = it },
                        label = { Text("Rule Name / Description") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newPort.isNotBlank() && newRuleName.isNotBlank()) {
                            firewallRulesList.add(
                                FirewallRule(
                                    port = newPort,
                                    protocol = "TCP",
                                    name = newRuleName,
                                    source = "Src: 0.0.0.0/0 (Global Any)",
                                    status = "ALLOWED"
                                )
                            )
                            newPort = ""
                            newRuleName = ""
                            showAddFirewallDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEA580C))
                ) {
                    Text("Add Rule", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddFirewallDialog = false }) {
                    Text("Cancel", color = Color(0xFF9CA3AF))
                }
            },
            containerColor = Color(0xFF111827)
        )
    }
}

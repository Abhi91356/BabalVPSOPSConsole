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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Router
import androidx.compose.material.icons.filled.Search
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
import com.example.myapplication.model.MockData
import com.example.myapplication.model.VpsInstance

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onInstanceClick: (VpsInstance) -> Unit,
    onDeployClick: () -> Unit,
    onTerminalClick: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedRegion by remember { mutableStateOf("All Locations (20)") }
    var selectedStatus by remember { mutableStateOf("All") }
    var pingTested by remember { mutableStateOf(false) }

    val filteredInstances = MockData.instances.filter { instance ->
        val matchesSearch = instance.name.contains(searchQuery, ignoreCase = true) ||
                instance.publicIp.contains(searchQuery, ignoreCase = true) ||
                instance.code.contains(searchQuery, ignoreCase = true)
        val matchesRegion = when (selectedRegion) {
            "Kathmandu KTM-1 (14)" -> instance.region == "Kathmandu KTM-1"
            "Singapore SG-1 (6)" -> instance.region == "Singapore SG-1"
            else -> true
        }
        val matchesStatus = when (selectedStatus) {
            "Running" -> instance.status == "Running"
            "Stopped" -> instance.status == "Stopped"
            else -> true
        }
        matchesSearch && matchesRegion && matchesStatus
    }

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
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "CONSOLE > COMPUTE > ",
                    color = Color(0xFF9CA3AF),
                    fontSize = 13.dp.value.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "VPS INSTANCES",
                    color = Color(0xFFF9FAFB),
                    fontSize = 13.dp.value.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = { /* Refresh */ },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFF9FAFB)),
                    border = BorderStroke(1.dp, Color(0xFF374151)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Refresh Metrics", fontSize = 12.sp)
                }

                Button(
                    onClick = onDeployClick,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEA580C)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Deploy New Instance", fontSize = 12.sp, color = Color.White)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Title and Mesh Sync Pill
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Cloud VPS & Compute Fleet",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Surface(
                    color = Color(0xFF1E293B),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, Color(0xFF38BDF8).copy(alpha = 0.4f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF38BDF8))
                        )
                        Text(
                            text = "Mesh Synchronized",
                            color = Color(0xFF38BDF8),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 4 Real-time Metric Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            MetricCard(
                modifier = Modifier.weight(1f),
                title = "ACTIVE COMPUTE",
                value = "18 Running",
                badgeText = "99.98% SLA",
                badgeColor = Color(0xFF10B981),
                subtitle = "● 2 Stopped   ● 0 Failed"
            )
            MetricCard(
                modifier = Modifier.weight(1f),
                title = "TOTAL VCPU ALLOCATED",
                value = "64 / 96 Cores",
                badgeText = "66% Load",
                badgeColor = Color(0xFFF59E0B),
                subtitle = "Reserved: 64 Core   32 Available",
                progress = 0.66f
            )
            MetricCard(
                modifier = Modifier.weight(1f),
                title = "RAM ALLOCATED",
                value = "128 / 192 GB",
                badgeText = "DDR5 ECC",
                badgeColor = Color(0xFF3B82F6),
                subtitle = "● Active: 84 GB   ● Cache: 44 GB"
            )
            MetricCard(
                modifier = Modifier.weight(1f),
                title = "NVME TIER-1 STORAGE",
                value = "3.2 / 6.0 TB",
                badgeText = "48,200 IOPS",
                badgeColor = Color(0xFF8B5CF6),
                subtitle = "Throughput: 1.8 GB/s   2.8 TB Free"
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Search & Filters Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Filter by hostname, IP address...", color = Color(0xFF6B7280), fontSize = 13.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF9CA3AF)) },
                    modifier = Modifier
                        .width(300.dp)
                        .height(50.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF111827),
                        unfocusedContainerColor = Color(0xFF111827),
                        focusedBorderColor = Color(0xFF374151),
                        unfocusedBorderColor = Color(0xFF374151),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    singleLine = true
                )

                listOf("All Locations (20)", "Kathmandu KTM-1 (14)", "Singapore SG-1 (6)").forEach { region ->
                    val isSelected = selectedRegion == region
                    Surface(
                        modifier = Modifier.clickable { selectedRegion = region },
                        color = if (isSelected) Color(0xFFEA580C).copy(alpha = 0.2f) else Color(0xFF111827),
                        shape = RoundedCornerShape(20.dp),
                        border = BorderStroke(1.dp, if (isSelected) Color(0xFFEA580C) else Color(0xFF374151))
                    ) {
                        Text(
                            text = region,
                            color = if (isSelected) Color(0xFFFDBA74) else Color(0xFF9CA3AF),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Instance Table / Cards List
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color(0xFF111827),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, Color(0xFF1F2937))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("INSTANCE HOST & ID", color = Color(0xFF6B7280), fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(2f))
                    Text("STATE", color = Color(0xFF6B7280), fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                    Text("NETWORKING / IP", color = Color(0xFF6B7280), fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(2f))
                    Text("REGION & DATACENTER", color = Color(0xFF6B7280), fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(2f))
                    Text("OS & KERNEL", color = Color(0xFF6B7280), fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(2f))
                    Text("ACTIONS", color = Color(0xFF6B7280), fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                }

                HorizontalDivider(color = Color(0xFF1F2937))

                LazyColumn(
                    modifier = Modifier.height(280.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredInstances) { instance ->
                        InstanceRowItem(
                            instance = instance,
                            onClick = { onInstanceClick(instance) },
                            onTerminalClick = onTerminalClick
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Bottom Cards
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
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFF10B981).copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Router, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(16.dp))
                        }
                        Text("Low-latency Kathmandu Edge Active", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF10B981))
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        "Local IX Peering active with sub-5ms round-trip latency across Nepal ISPs (WorldLink, Vianet, NTC, Ncell).",
                        color = Color(0xFF9CA3AF),
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(
                            onClick = { /* Topology */ },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                            border = BorderStroke(1.dp, Color(0xFF374151)),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Text("View Peering Topology", fontSize = 11.sp)
                        }
                        Button(
                            onClick = { pingTested = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEA580C)),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Text(if (pingTested) "Ping: 3.2 ms" else "Test Latency Ping", fontSize = 11.sp, color = Color.White)
                        }
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
                        Text("VPC Security Shield", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Icon(Icons.Default.Security, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(18.dp))
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("DDoS mitigation & automated rate-limiting enabled at Nepal edge boundary.", color = Color(0xFF9CA3AF), fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("DDoS Mitigation:", color = Color(0xFF9CA3AF), fontSize = 11.sp)
                        Text("🛡️ Armored (L3/L4/L7)", color = Color(0xFF10B981), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Blocked Ingress (24h):", color = Color(0xFF9CA3AF), fontSize = 11.sp)
                        Text("14,290 invalid hits", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Default Firewall:", color = Color(0xFF9CA3AF), fontSize = 11.sp)
                        Text("strict-ktm-default", color = Color(0xFFFDBA74), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun MetricCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    badgeText: String,
    badgeColor: Color,
    subtitle: String,
    progress: Float? = null
) {
    Surface(
        modifier = modifier,
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
                Text(title, color = Color(0xFF9CA3AF), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Surface(
                    color = badgeColor.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = badgeText,
                        color = badgeColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(value, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            if (progress != null) {
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = Color(0xFFEA580C),
                    trackColor = Color(0xFF374151)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(subtitle, color = Color(0xFF6B7280), fontSize = 11.sp)
        }
    }
}

@Composable
fun InstanceRowItem(
    instance: VpsInstance,
    onClick: () -> Unit,
    onTerminalClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .background(Color(0xFF111827), RoundedCornerShape(8.dp))
            .padding(vertical = 10.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(modifier = Modifier.weight(2f), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(if (instance.status == "Running") Color(0xFF1F2937) else Color(0xFF1F1F1F)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Dns,
                    contentDescription = null,
                    tint = if (instance.status == "Running") Color(0xFF38BDF8) else Color(0xFF6B7280),
                    modifier = Modifier.size(18.dp)
                )
            }
            Column {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(instance.name, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Surface(
                        color = Color(0xFF374151),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(instance.role, color = Color(0xFFE5E7EB), fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                    }
                }
                Text(instance.code, color = Color(0xFF6B7280), fontSize = 11.sp)
            }
        }

        Box(modifier = Modifier.weight(1f)) {
            Surface(
                color = if (instance.status == "Running") Color(0xFF10B981).copy(alpha = 0.15f) else Color(0xFF6B7280).copy(alpha = 0.15f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(if (instance.status == "Running") Color(0xFF10B981) else Color(0xFF6B7280))
                    )
                    Text(
                        text = instance.status,
                        color = if (instance.status == "Running") Color(0xFF34D399) else Color(0xFF9CA3AF),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        Column(modifier = Modifier.weight(2f)) {
            Text(instance.publicIp, color = Color(0xFF38BDF8), fontSize = 12.sp, fontWeight = FontWeight.Medium)
            Text("priv: ${instance.privateIp}", color = Color(0xFF6B7280), fontSize = 11.sp)
        }

        Column(modifier = Modifier.weight(2f)) {
            Text("🇳🇵 Babal KTM-1", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            Text("Kathmandu DC", color = Color(0xFF6B7280), fontSize = 11.sp)
        }

        Column(modifier = Modifier.weight(2f)) {
            Text(instance.osKernel, color = Color(0xFFE5E7EB), fontSize = 12.sp, maxLines = 1)
        }

        Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                onClick = onTerminalClick,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(Icons.Default.Terminal, contentDescription = "Terminal", tint = Color(0xFFEA580C), modifier = Modifier.size(16.dp))
            }
            IconButton(
                onClick = { /* Power toggle */ },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(Icons.Default.PowerSettingsNew, contentDescription = "Power", tint = if (instance.status == "Running") Color(0xFF10B981) else Color(0xFFEF4444), modifier = Modifier.size(16.dp))
            }
        }
    }
}

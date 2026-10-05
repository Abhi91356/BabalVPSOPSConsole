package com.example.myapplication.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.theme.MyApplicationTheme

// --- Design Token System ---
val ConsoleOrange = Color(0xFFC85108)
val ConsoleBackground = Color(0xFFF4F6FB)
val ConsoleSurface = Color(0xFFFFFFFF)
val ConsoleBorder = Color(0xFFE2E8F0)
val ConsoleSuccess = Color(0xFF10B981)
val ConsoleSuccessBg = Color(0xFFECFDF5)
val ConsoleInactive = Color(0xFF94A3B8)
val ConsolePrimaryText = Color(0xFF0F172A)
val ConsoleSecondaryText = Color(0xFF64748B)

// --- Domain Data Models ---
data class VpsInstanceNode(
    val id: String,
    val hostname: String,
    val roleTag: String,
    val status: String, // "Running" or "Stopped"
    val publicIp: String,
    val privateIp: String,
    val datacenter: String,
    val osKernel: String,
    val cpuUsage: Float,
    val memoryUsage: Float,
    val region: String
)

data class OpsMetricItem(
    val title: String,
    val value: String,
    val subValue: String,
    val badge: String? = null,
    val loadProgress: Float? = null,
    val isSuccess: Boolean = false
)

// --- Parent Composable Viewport ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BabalOpsConsoleScreen(
    onDeployClick: () -> Unit = {},
    onInstanceClick: (VpsInstanceNode) -> Unit = {},
    onTerminalClick: (VpsInstanceNode) -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedRegionFilter by remember { mutableStateOf("All Locations (20)") }

    val sampleInstances = listOf(
        VpsInstanceNode(
            id = "vps-849204",
            hostname = "prod-api-ktm01",
            roleTag = "API",
            status = "Running",
            publicIp = "103.112.54.21",
            privateIp = "10.0.1.42",
            datacenter = "Babal KTM-1, Kathmandu DC",
            osKernel = "Ubuntu 24.04 LTS",
            cpuUsage = 0.42f,
            memoryUsage = 0.65f,
            region = "Kathmandu KTM-1"
        ),
        VpsInstanceNode(
            id = "vps-992102",
            hostname = "db-postgres-cluster",
            roleTag = "PRIMARY",
            status = "Running",
            publicIp = "103.112.54.89",
            privateIp = "10.0.1.18",
            datacenter = "Babal KTM-1, Kathmandu DC",
            osKernel = "Debian 12 Bookworm",
            cpuUsage = 0.68f,
            memoryUsage = 0.82f,
            region = "Kathmandu KTM-1"
        ),
        VpsInstanceNode(
            id = "vps-302194",
            hostname = "babal-web-edge",
            roleTag = "CDN",
            status = "Running",
            publicIp = "103.112.55.104",
            privateIp = "10.2.0.8",
            datacenter = "Singapore SG-1, Equinix",
            osKernel = "Ubuntu 22.04 LTS",
            cpuUsage = 0.29f,
            memoryUsage = 0.44f,
            region = "Singapore SG-1"
        ),
        VpsInstanceNode(
            id = "vps-110294",
            hostname = "dev-sandbox-nepal",
            roleTag = "DEV",
            status = "Stopped",
            publicIp = "103.112.54.12",
            privateIp = "10.0.1.99",
            datacenter = "Babal KTM-1, Kathmandu DC",
            osKernel = "AlmaLinux 9",
            cpuUsage = 0.0f,
            memoryUsage = 0.0f,
            region = "Kathmandu KTM-1"
        )
    )

    val filteredInstances = sampleInstances.filter { instance ->
        val matchesSearch = instance.hostname.contains(searchQuery, ignoreCase = true) ||
                instance.publicIp.contains(searchQuery, ignoreCase = true) ||
                instance.osKernel.contains(searchQuery, ignoreCase = true)
        val matchesRegion = when (selectedRegionFilter) {
            "Kathmandu KTM-1 (14)" -> instance.region == "Kathmandu KTM-1"
            "Singapore SG-1 (6)" -> instance.region == "Singapore SG-1"
            else -> true
        }
        matchesSearch && matchesRegion
    }

    Scaffold(
        topBar = { OpsConsoleTopHeaderBar() },
        containerColor = ConsoleBackground
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(modifier = Modifier.height(4.dp)) }

            // Section B: Page Command Header
            item {
                OpsFleetCommandHeader(onDeployClick = onDeployClick)
            }

            // Section C: High-Density Telemetry Metrics Grid
            item {
                OpsMetricsSection()
            }

            // Section D: Search Bar & Region Filter Carousel
            item {
                OpsSearchFilterSection(
                    searchQuery = searchQuery,
                    onSearchChange = { searchQuery = it },
                    selectedFilter = selectedRegionFilter,
                    onFilterChange = { selectedRegionFilter = it }
                )
            }

            // Section E: VPS Fleet Instance Cards
            items(filteredInstances, key = { it.id }) { instance ->
                OpsInstanceRowItem(
                    instance = instance,
                    onClick = { onInstanceClick(instance) },
                    onTerminalClick = { onTerminalClick(instance) }
                )
            }

            // Section F: Edge Telemetry Banner
            item {
                OpsTelemetryBannerCard()
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }
}

// --- Section A: Top Navigation & Header ---
@Composable
fun OpsConsoleTopHeaderBar() {
    Surface(
        color = ConsoleSurface,
        shadowElevation = 1.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Babal Cloud",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = ConsolePrimaryText
                )
                Text(
                    text = "VPS OPS CONSOLE",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = ConsoleOrange
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Surface(
                    color = ConsoleBackground,
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, ConsoleBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(ConsoleSuccess)
                        )
                        Text(
                            text = "Region: Nepal (KTM-1)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = ConsolePrimaryText
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(ConsoleBackground),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = "Suman Shrestha - Root Administrator",
                        tint = ConsolePrimaryText,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

// --- Section B: Page Command Header ---
@Composable
fun OpsFleetCommandHeader(onDeployClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Cloud VPS & Compute Fleet",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = ConsolePrimaryText
            )
            Spacer(modifier = Modifier.height(2.dp))
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Box(
                    modifier = Modifier
                        .size(7.dp)
                        .clip(CircleShape)
                        .background(ConsoleSuccess)
                )
                Text(
                    text = "Mesh Synchronized",
                    fontSize = 12.sp,
                    color = ConsoleSecondaryText
                )
            }
        }

        Button(
            onClick = onDeployClick,
            colors = ButtonDefaults.buttonColors(containerColor = ConsoleOrange),
            shape = RoundedCornerShape(10.dp),
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = "Deploy Instance", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
    }
}

// --- Section C: High-Density Telemetry Metrics Grid ---
@Composable
fun OpsMetricsSection() {
    val metrics = listOf(
        OpsMetricItem("ACTIVE COMPUTE", "18 Running", "99.98% SLA (2 Stopped)", isSuccess = true),
        OpsMetricItem("TOTAL VCPU ALLOCATED", "64 / 96 Cores", "66% Load", loadProgress = 0.66f),
        OpsMetricItem("RAM ALLOCATED", "128 / 192 GB", "Active: 84 GB | Cache: 44 GB"),
        OpsMetricItem("NVME TIER-1 STORAGE", "3.2 / 6.0 TB", "48,200 avg IOPS")
    )

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OpsMetricCard(modifier = Modifier.weight(1f), item = metrics[0])
            OpsMetricCard(modifier = Modifier.weight(1f), item = metrics[1])
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OpsMetricCard(modifier = Modifier.weight(1f), item = metrics[2])
            OpsMetricCard(modifier = Modifier.weight(1f), item = metrics[3])
        }
    }
}

@Composable
fun OpsMetricCard(modifier: Modifier = Modifier, item: OpsMetricItem) {
    Surface(
        modifier = modifier,
        color = ConsoleSurface,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, ConsoleBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = item.title,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = ConsoleSecondaryText
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = item.value,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = if (item.isSuccess) ConsoleSuccess else ConsolePrimaryText
            )
            if (item.loadProgress != null) {
                Spacer(modifier = Modifier.height(6.dp))
                LinearProgressIndicator(
                    progress = { item.loadProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(5.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = ConsoleOrange,
                    trackColor = ConsoleBackground
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = item.subValue,
                fontSize = 11.sp,
                color = ConsoleSecondaryText
            )
        }
    }
}

// --- Section D: Search Bar & Region Filter Carousel ---
@Composable
fun OpsSearchFilterSection(
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    selectedFilter: String,
    onFilterChange: (String) -> Unit
) {
    val filterOptions = listOf("All Locations (20)", "Kathmandu KTM-1 (14)", "Singapore SG-1 (6)")

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Filter by hostname, IP address, or OS...", fontSize = 13.sp, color = ConsoleSecondaryText) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = ConsoleSecondaryText) },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = ConsoleSurface,
                unfocusedContainerColor = ConsoleSurface,
                focusedBorderColor = ConsoleOrange,
                unfocusedBorderColor = ConsoleBorder,
                focusedTextColor = ConsolePrimaryText,
                unfocusedTextColor = ConsolePrimaryText
            )
        )

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(filterOptions, key = { it }) { option ->
                val isSelected = selectedFilter == option
                Surface(
                    modifier = Modifier.clickable { onFilterChange(option) },
                    color = if (isSelected) ConsoleOrange else ConsoleSurface,
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, if (isSelected) ConsoleOrange else ConsoleBorder)
                ) {
                    Text(
                        text = option,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (isSelected) Color.White else ConsolePrimaryText,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                    )
                }
            }
        }
    }
}

// --- Section E: VPS Fleet Instance Cards ---
@Composable
fun OpsInstanceRowItem(
    instance: VpsInstanceNode,
    onClick: () -> Unit,
    onTerminalClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        color = ConsoleSurface,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, ConsoleBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = instance.hostname,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = ConsolePrimaryText
                    )
                    Surface(
                        color = ConsoleBackground,
                        shape = RoundedCornerShape(4.dp),
                        border = BorderStroke(1.dp, ConsoleBorder)
                    ) {
                        Text(
                            text = instance.roleTag,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = ConsolePrimaryText,
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                        )
                    }
                }

                val isRunning = instance.status == "Running"
                Surface(
                    color = if (isRunning) ConsoleSuccessBg else ConsoleBackground,
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
                                .background(if (isRunning) ConsoleSuccess else ConsoleInactive)
                        )
                        Text(
                            text = instance.status,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isRunning) ConsoleSuccess else ConsoleSecondaryText
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = Color(0xFFF1F5F9))
            Spacer(modifier = Modifier.height(10.dp))

            // Details Grid (3 columns)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "IP ADDRESS", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = ConsoleSecondaryText)
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(text = instance.publicIp, fontSize = 11.sp, fontWeight = FontWeight.Medium, color = ConsoleOrange)
                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy IP", tint = ConsoleSecondaryText, modifier = Modifier.size(10.dp))
                    }
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "DATACENTER", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = ConsoleSecondaryText)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(text = instance.datacenter, fontSize = 11.sp, color = ConsolePrimaryText, maxLines = 1)
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "OS & KERNEL", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = ConsoleSecondaryText)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(text = instance.osKernel, fontSize = 11.sp, color = ConsolePrimaryText, maxLines = 1)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Footer Micro Progress Bars & Action
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("CPU Usage", fontSize = 9.sp, color = ConsoleSecondaryText)
                            Text("${(instance.cpuUsage * 100).toInt()}%", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = ConsolePrimaryText)
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        LinearProgressIndicator(
                            progress = { instance.cpuUsage },
                            modifier = Modifier.fillMaxWidth().height(3.dp).clip(RoundedCornerShape(2.dp)),
                            color = ConsoleOrange,
                            trackColor = ConsoleBackground
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("RAM Usage", fontSize = 9.sp, color = ConsoleSecondaryText)
                            Text("${(instance.memoryUsage * 100).toInt()}%", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = ConsolePrimaryText)
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        LinearProgressIndicator(
                            progress = { instance.memoryUsage },
                            modifier = Modifier.fillMaxWidth().height(3.dp).clip(RoundedCornerShape(2.dp)),
                            color = Color(0xFF3B82F6),
                            trackColor = ConsoleBackground
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                IconButton(
                    onClick = onTerminalClick,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(Icons.Default.Terminal, contentDescription = "SSH Terminal", tint = ConsoleOrange, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

// --- Section F: Edge Telemetry Banner ---
@Composable
fun OpsTelemetryBannerCard() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = ConsoleSurface,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, ConsoleBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(ConsoleSuccess)
                )
                Text(
                    text = "Low-latency Kathmandu Edge Active",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = ConsolePrimaryText
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Local IX Peering active with sub-5ms round-trip latency across Nepal ISPs (WorldLink, Vianet, NTC, Ncell).",
                fontSize = 11.sp,
                color = ConsoleSecondaryText
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = { /* Topology */ },
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, ConsoleBorder),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text("View Peering Topology", fontSize = 11.sp, color = ConsolePrimaryText)
                }
                OutlinedButton(
                    onClick = { /* Ping */ },
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, ConsoleOrange),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = ConsoleOrange),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text("Test Latency Ping", fontSize = 11.sp)
                }
            }
        }
    }
}

// --- Android Studio Visual Validation Preview ---
@Preview(showBackground = true, backgroundColor = 0xFFF4F6FB, showSystemUi = true)
@Composable
fun BabalCloudOpsConsolePreview() {
    MyApplicationTheme {
        BabalOpsConsoleScreen()
    }
}

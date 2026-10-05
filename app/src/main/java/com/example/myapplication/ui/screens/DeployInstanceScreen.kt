package com.example.myapplication.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Dns
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
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeployInstanceScreen(
    onBack: () -> Unit,
    onInstanceDeployed: (VpsInstance) -> Unit
) {
    var selectedRegion by remember { mutableStateOf("Kathmandu (KTM-1)") }
    var selectedOs by remember { mutableStateOf("Ubuntu 24.04 LTS") }
    var selectedTier by remember { mutableStateOf("Pro 4") }
    var authMode by remember { mutableStateOf("SSH Key") }
    var instanceHostname by remember { mutableStateOf("prod-api-ktm02") }
    var resourceTags by remember { mutableStateOf("env:production, project:api-gate") }
    var backupEnabled by remember { mutableStateOf(true) }
    var ddosEnabled by remember { mutableStateOf(true) }
    var staticIpEnabled by remember { mutableStateOf(true) }

    val tierPrice = when (selectedTier) {
        "Standard 2" -> 1200.0
        "Pro 4" -> 2400.0
        "Compute 8" -> 4600.0
        "Memory 16" -> 8200.0
        else -> 2400.0
    }
    val backupPrice = if (backupEnabled) 250.0 else 0.0
    val vatTax = (tierPrice + backupPrice) * 0.13
    val totalDue = tierPrice + backupPrice + vatTax

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
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Text("CONSOLE > COMPUTE > ", color = Color(0xFF9CA3AF), fontSize = 12.sp)
                Text("Deploy New High-Performance Cloud VPS", color = Color(0xFF38BDF8), fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }

            Surface(color = Color(0xFF10B981).copy(alpha = 0.2f), shape = RoundedCornerShape(6.dp)) {
                Text("V4 API • NVMe Gen4 Pools Live", color = Color(0xFF34D399), fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Left Wizard Form Section (Steps 1 to 5)
            LazyColumn(
                modifier = Modifier
                    .weight(2f)
                    .height(520.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Step 1 — Region Selection
                item {
                    WizardSectionCard(step = "1", title = "Select Datacenter Region") {
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            RegionCard("🇳🇵 Kathmandu", "KTM-1 Primary Edge", "Fastest (4ms latency)", selectedRegion == "Kathmandu (KTM-1)") {
                                selectedRegion = "Kathmandu (KTM-1)"
                            }
                            RegionCard("🇸🇬 Singapore", "SG-1 Equinix", "Global Tier-1 (48ms)", selectedRegion == "Singapore (SG-1)") {
                                selectedRegion = "Singapore (SG-1)"
                            }
                            RegionCard("🇮🇳 Mumbai", "BOM-1 Ctrls DC", "Sub-continent hub", selectedRegion == "Mumbai (BOM-1)") {
                                selectedRegion = "Mumbai (BOM-1)"
                            }
                        }
                    }
                }

                // Step 2 — OS Distribution
                item {
                    WizardSectionCard(step = "2", title = "Choose Operating System & Distribution") {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OsCard("Ubuntu 24.04", "Free", selectedOs == "Ubuntu 24.04 LTS") { selectedOs = "Ubuntu 24.04 LTS" }
                            OsCard("Debian 12", "Free", selectedOs == "Debian 12") { selectedOs = "Debian 12" }
                            OsCard("AlmaLinux 9", "Free", selectedOs == "AlmaLinux 9.3") { selectedOs = "AlmaLinux 9.3" }
                            OsCard("Rocky Linux", "Free", selectedOs == "Rocky Linux 9.4") { selectedOs = "Rocky Linux 9.4" }
                            OsCard("Windows 2022", "+NPR 800", selectedOs == "Windows Server 2022") { selectedOs = "Windows Server 2022" }
                        }
                    }
                }

                // Step 3 — Hardware Specs
                item {
                    WizardSectionCard(step = "3", title = "Choose Instance Tier & Hardware Specs") {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                TierCard("Standard 2", "NPR 1,200/mo", "2 vCPU • 4 GB • 80 GB NVMe", selectedTier == "Standard 2") { selectedTier = "Standard 2" }
                                TierCard("Pro 4", "NPR 2,400/mo", "4 vCPU • 8 GB • 160 GB NVMe", selectedTier == "Pro 4") { selectedTier = "Pro 4" }
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                TierCard("Compute 8", "NPR 4,600/mo", "8 vCPU • 16 GB • 320 GB NVMe", selectedTier == "Compute 8") { selectedTier = "Compute 8" }
                                TierCard("Memory 16", "NPR 8,200/mo", "8 vCPU • 32 GB • 480 GB NVMe", selectedTier == "Memory 16") { selectedTier = "Memory 16" }
                            }
                        }
                    }
                }

                // Step 4 — Authentication & Hostname
                item {
                    WizardSectionCard(step = "4", title = "Authentication & Security") {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                AuthTab("🔑 SSH Key", authMode == "SSH Key") { authMode = "SSH Key" }
                                AuthTab("🔒 Root Password", authMode == "Root Password") { authMode = "Root Password" }
                            }
                            OutlinedTextField(
                                value = instanceHostname,
                                onValueChange = { instanceHostname = it },
                                label = { Text("Instance Hostname") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                )
                            )
                            OutlinedTextField(
                                value = resourceTags,
                                onValueChange = { resourceTags = it },
                                label = { Text("Resource Tags (Comma separated)") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                )
                            )
                        }
                    }
                }

                // Step 5 — Add-ons & Networking
                item {
                    WizardSectionCard(step = "5", title = "Optional Add-ons & Networking") {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            AddonToggle("Automated Daily Backups (+NPR 250/mo)", "Retained for 7 rolling days", backupEnabled) { backupEnabled = it }
                            AddonToggle("Advanced 100Gbps+ DDoS Protection (Included Free)", "Layer 3/4 & Layer 7 scrubbing", ddosEnabled) { ddosEnabled = it }
                            AddonToggle("Dedicated Static IPv4 & /64 IPv6 Subnet (Included Free)", "Assigned from clean 103.14.x.x block", staticIpEnabled) { staticIpEnabled = it }
                        }
                    }
                }
            }

            // Right Live Order Summary Sidebar
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .height(520.dp),
                color = Color(0xFF111827),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color(0xFF1F2937))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Order Summary", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            Surface(color = Color(0xFF1E293B), shape = RoundedCornerShape(4.dp)) {
                                Text(selectedRegion, color = Color(0xFF38BDF8), fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                            }
                        }
                        HorizontalDivider(color = Color(0xFF1F2937))

                        SummaryRow("Instance: $selectedTier", "NPR ${tierPrice.toInt()}.00")
                        SummaryRow("OS: $selectedOs", "Free")
                        SummaryRow("Storage: Gen4 NVMe SAN", "Included")
                        SummaryRow("Automated Daily Backups", if (backupEnabled) "NPR 250.00" else "Off")
                        SummaryRow("100Gbps DDoS Protection", "Free")
                        SummaryRow("Dedicated Static IPv4", "Free")

                        HorizontalDivider(color = Color(0xFF1F2937))
                        SummaryRow("Value Added Tax (13%)", "NPR ${String.format(Locale.US, "%.2f", vatTax)}")

                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Total Due Today", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Text("NPR ${String.format(Locale.US, "%.2f", totalDue)}", color = Color(0xFFEA580C), fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Button(
                        onClick = {
                            val newInstance = VpsInstance(
                                id = "vps-${(100000..999999).random()}",
                                name = instanceHostname,
                                code = "vps-${(100000..999999).random()}",
                                role = "API",
                                status = "Running",
                                publicIp = "103.112.54.${(10..99).random()}",
                                privateIp = "10.0.1.${(10..99).random()}",
                                datacenter = if (selectedRegion.contains("Kathmandu")) "Babal KTM-1 (Kathmandu DC)" else "Singapore SG-1",
                                osKernel = "$selectedOs (6.8.0-generic)",
                                cpuUsagePercent = 12,
                                ramUsageGb = 1.2,
                                ramTotalGb = if (selectedTier.contains("2")) 4.0 else if (selectedTier.contains("4")) 8.0 else 16.0,
                                storageUsageGb = 24.0,
                                storageTotalGb = 160.0,
                                region = if (selectedRegion.contains("Kathmandu")) "Kathmandu KTM-1" else "Singapore SG-1"
                            )
                            onInstanceDeployed(newInstance)
                        },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEA580C)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.CloudUpload, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Deploy Instance Now (35s)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun WizardSectionCard(step: String, title: String, content: @Composable () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xFF111827),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color(0xFF1F2937))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(
                    modifier = Modifier.size(24.dp).clip(CircleShape).background(Color(0xFFEA580C)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(step, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Text(title, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
fun RegionCard(title: String, subtitle: String, latency: String, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.width(135.dp).clickable(onClick = onClick),
        color = if (isSelected) Color(0xFFEA580C).copy(alpha = 0.15f) else Color(0xFF1F2937),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, if (isSelected) Color(0xFFEA580C) else Color(0xFF374151))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(title, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Text(subtitle, color = Color(0xFF9CA3AF), fontSize = 10.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(latency, color = Color(0xFF10B981), fontSize = 9.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun OsCard(name: String, badge: String, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.width(95.dp).clickable(onClick = onClick),
        color = if (isSelected) Color(0xFFEA580C).copy(alpha = 0.15f) else Color(0xFF1F2937),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, if (isSelected) Color(0xFFEA580C) else Color(0xFF374151))
    ) {
        Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.Dns, contentDescription = null, tint = if (isSelected) Color(0xFFEA580C) else Color(0xFF9CA3AF), modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(name, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold, maxLines = 1)
            Text(badge, color = Color(0xFF10B981), fontSize = 8.sp)
        }
    }
}

@Composable
fun TierCard(name: String, price: String, specs: String, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.width(200.dp).clickable(onClick = onClick),
        color = if (isSelected) Color(0xFFEA580C).copy(alpha = 0.15f) else Color(0xFF1F2937),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, if (isSelected) Color(0xFFEA580C) else Color(0xFF374151))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(name, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                if (isSelected) Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFFEA580C), modifier = Modifier.size(16.dp))
            }
            Text(price, color = Color(0xFF38BDF8), fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Text(specs, color = Color(0xFF9CA3AF), fontSize = 10.sp)
        }
    }
}

@Composable
fun AuthTab(title: String, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.clickable(onClick = onClick),
        color = if (isSelected) Color(0xFFEA580C).copy(alpha = 0.2f) else Color(0xFF1F2937),
        shape = RoundedCornerShape(6.dp),
        border = BorderStroke(1.dp, if (isSelected) Color(0xFFEA580C) else Color(0xFF374151))
    ) {
        Text(
            text = title,
            color = if (isSelected) Color(0xFFFDBA74) else Color(0xFF9CA3AF),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        )
    }
}

@Composable
fun AddonToggle(title: String, subtitle: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF1F2937), RoundedCornerShape(6.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(title, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Text(subtitle, color = Color(0xFF9CA3AF), fontSize = 9.sp)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Color(0xFFEA580C))
        )
    }
}

@Composable
fun SummaryRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = Color(0xFF9CA3AF), fontSize = 12.sp)
        Text(value, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Medium)
    }
}

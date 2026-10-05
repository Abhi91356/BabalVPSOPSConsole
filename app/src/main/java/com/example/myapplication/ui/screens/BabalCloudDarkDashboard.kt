package com.example.myapplication.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

// --- Visual Design System Tokens ---
val MobileDarkBg = Color(0xFF0B0F17)
val MobileDarkSurface = Color(0xFF131B2E)
val MobileDarkBorder = Color(0xFF1E293B)
val MobilePrimaryOrange = Color(0xFFE05A10)
val MobileTextWhite = Color(0xFFF8FAFC)
val MobileTextMuted = Color(0xFF94A3B8)
val MobileStatusGreen = Color(0xFF10B981)
val MobileStatusGreenBg = Color(0xFF064E3B)

// --- Navigation Routes ---
enum class MobileNavScreen(val title: String, val icon: ImageVector) {
    COMPUTE("Compute / VPS Instances", Icons.Default.Computer),
    STORAGE("Storage & Volumes", Icons.Default.Storage),
    NETWORKING("Networking & VPC", Icons.Default.Router)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BabalMobileDarkCloudApp() {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var currentScreen by remember { mutableStateOf(MobileNavScreen.COMPUTE) }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = MobileDarkSurface,
                modifier = Modifier.width(280.dp)
            ) {
                MobileDrawerHeader()
                HorizontalDivider(color = MobileDarkBorder, modifier = Modifier.padding(vertical = 8.dp))
                
                MobileNavScreen.entries.forEach { screen ->
                    NavigationDrawerItem(
                        icon = { Icon(screen.icon, contentDescription = null, tint = if (currentScreen == screen) Color.White else MobileTextMuted) },
                        label = { 
                            Text(
                                text = screen.title, 
                                color = if (currentScreen == screen) Color.White else MobileTextMuted,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            ) 
                        },
                        selected = currentScreen == screen,
                        onClick = {
                            currentScreen = screen
                            scope.launch { drawerState.close() }
                        },
                        colors = NavigationDrawerItemDefaults.colors(
                            selectedContainerColor = MobilePrimaryOrange,
                            unselectedContainerColor = Color.Transparent
                        ),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp)
                    )
                }

                Spacer(modifier = Modifier.weight(1f))
                MobileQuotaProgressCard()
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = currentScreen.title,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MobileTextWhite,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu", tint = MobileTextWhite)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = MobileDarkBg)
                )
            },
            containerColor = MobileDarkBg
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                when (currentScreen) {
                    MobileNavScreen.COMPUTE -> ComputeFleetScreen()
                    MobileNavScreen.STORAGE -> MobileStorageVolumesScreen()
                    MobileNavScreen.NETWORKING -> MobileNetworkingVpcScreen()
                }
            }
        }
    }
}

// --- View 1: Compute / VPS Fleet View ---
@Composable
fun ComputeFleetScreen() {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { Spacer(modifier = Modifier.height(4.dp)) }

        // Header with title, mesh sync badge, and deploy button
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Cloud VPS Fleet", 
                        fontSize = 18.sp, 
                        fontWeight = FontWeight.Bold, 
                        color = MobileTextWhite,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(modifier = Modifier.size(7.dp).clip(CircleShape).background(MobileStatusGreen))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Mesh Synchronized", 
                            fontSize = 11.sp, 
                            color = MobileTextMuted,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                Button(
                    onClick = { },
                    colors = ButtonDefaults.buttonColors(containerColor = MobilePrimaryOrange),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Deploy", fontSize = 12.sp)
                }
            }
        }

        // 2x2 Telemetry Metrics Grid
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    MobileMetricCard(Modifier.weight(1f), "ACTIVE COMPUTE", "18 Running", "99.98% SLA", isGreen = true)
                    MobileMetricCard(Modifier.weight(1f), "TOTAL vCPU", "64 / 96 Cores", "66% Load", progress = 0.66f)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    MobileMetricCard(Modifier.weight(1f), "RAM ALLOCATED", "128 / 192 GB", "Active: 84 GB")
                    MobileMetricCard(Modifier.weight(1f), "NVMe STORAGE", "3.2 / 6.0 TB", "48,200 IOPS")
                }
            }
        }

        // Section Title
        item {
            Text("Instances Overview", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = MobileTextWhite)
        }

        // Full-Width Stacked Instance Cards
        items(getMobileSampleInstances()) { vps ->
            MobileVpsInstanceCard(vps)
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}

// --- View 2: Storage & Volumes View ---
@Composable
fun MobileStorageVolumesScreen() {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item { Spacer(modifier = Modifier.height(4.dp)) }
        item {
            Text("NVMe Gen4 Storage Pools", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MobileTextWhite)
        }

        items(getMobileSampleStoragePools()) { pool ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MobileDarkSurface),
                border = BorderStroke(1.dp, MobileDarkBorder)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = pool.name, 
                            fontWeight = FontWeight.Bold, 
                            color = MobileTextWhite, 
                            fontSize = 14.sp,
                            modifier = Modifier.weight(1f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(color = MobileStatusGreenBg, shape = RoundedCornerShape(6.dp)) {
                            Text(pool.status, color = MobileStatusGreen, fontSize = 10.sp, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            Text("CAPACITY", fontSize = 9.sp, color = MobileTextMuted)
                            Text(pool.capacity, fontSize = 12.sp, color = MobileTextWhite, fontWeight = FontWeight.Medium)
                        }
                        Column {
                            Text("ATTACHED TO", fontSize = 9.sp, color = MobileTextMuted)
                            Text(pool.attachedInstance, fontSize = 12.sp, color = MobileTextWhite, fontWeight = FontWeight.Medium)
                        }
                        Column {
                            Text("IOPS", fontSize = 9.sp, color = MobileTextMuted)
                            Text(pool.iops, fontSize = 12.sp, color = MobilePrimaryOrange, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}

// --- View 3: Networking & VPC View ---
@Composable
fun MobileNetworkingVpcScreen() {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item { Spacer(modifier = Modifier.height(4.dp)) }
        item {
            Text("VPC Subnets & Peering", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MobileTextWhite)
        }

        items(getMobileSampleSubnets()) { subnet ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MobileDarkSurface),
                border = BorderStroke(1.dp, MobileDarkBorder)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = subnet.name, 
                            fontWeight = FontWeight.Bold, 
                            color = MobileTextWhite, 
                            fontSize = 13.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(subnet.cidr, fontSize = 11.sp, color = MobileStatusGreen, fontWeight = FontWeight.Medium)
                        Text(
                            text = subnet.region, 
                            fontSize = 10.sp, 
                            color = MobileTextMuted,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(color = MobileStatusGreenBg, shape = RoundedCornerShape(6.dp)) {
                        Text("Active", color = MobileStatusGreen, fontSize = 10.sp, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                    }
                }
            }
        }
        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}

// --- Reusable Atomic Components ---
@Composable
fun MobileMetricCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    subText: String,
    isGreen: Boolean = false,
    progress: Float? = null
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MobileDarkSurface),
        border = BorderStroke(1.dp, MobileDarkBorder),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = title, 
                fontSize = 9.sp, 
                color = MobileTextMuted, 
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value, 
                fontSize = 14.sp, 
                fontWeight = FontWeight.Bold, 
                color = if (isGreen) MobileStatusGreen else MobileTextWhite,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (progress != null) {
                Spacer(modifier = Modifier.height(6.dp))
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
                    color = MobilePrimaryOrange,
                    trackColor = MobileDarkBg,
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subText, 
                fontSize = 10.sp, 
                color = MobileTextMuted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun MobileVpsInstanceCard(vps: MobileVpsInstanceData) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MobileDarkSurface),
        border = BorderStroke(1.dp, MobileDarkBorder),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = vps.name, 
                    fontWeight = FontWeight.Bold, 
                    color = MobileTextWhite, 
                    fontSize = 14.sp,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.width(8.dp))
                Surface(
                    color = if (vps.isRunning) MobileStatusGreenBg else MobileDarkBg,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = if (vps.isRunning) "Running" else "Stopped",
                        color = if (vps.isRunning) MobileStatusGreen else MobileTextMuted,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = MobileDarkBorder)
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("IP ADDRESS", fontSize = 9.sp, color = MobileTextMuted)
                    Text(vps.ip, fontSize = 11.sp, color = MobileTextWhite, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("REGION", fontSize = 9.sp, color = MobileTextMuted)
                    Text(vps.region, fontSize = 11.sp, color = MobileTextWhite, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("OS", fontSize = 9.sp, color = MobileTextMuted)
                    Text(vps.os, fontSize = 11.sp, color = MobileTextWhite, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }
        }
    }
}

@Composable
fun MobileDrawerHeader() {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Babal Cloud", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MobileTextWhite)
        Text("VPS OPS CONSOLE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MobilePrimaryOrange)
    }
}

@Composable
fun MobileQuotaProgressCard() {
    Card(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = MobileDarkBg),
        border = BorderStroke(1.dp, MobileDarkBorder)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Tier Quota", fontSize = 11.sp, color = MobileTextMuted)
                Text("82% Used", fontSize = 11.sp, color = MobilePrimaryOrange, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(6.dp))
            LinearProgressIndicator(
                progress = { 0.82f },
                modifier = Modifier.fillMaxWidth().height(5.dp).clip(RoundedCornerShape(3.dp)),
                color = MobilePrimaryOrange,
                trackColor = MobileDarkBorder
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text("8 of 10 VPS active in KTM-1", fontSize = 10.sp, color = MobileTextMuted)
        }
    }
}

// --- Data Models & Sample Data ---
data class MobileVpsInstanceData(val name: String, val isRunning: Boolean, val ip: String, val region: String, val os: String)
data class MobileStoragePoolData(val name: String, val capacity: String, val attachedInstance: String, val iops: String, val status: String)
data class MobileSubnetData(val name: String, val cidr: String, val region: String)

fun getMobileSampleInstances() = listOf(
    MobileVpsInstanceData("prod-api-ktm01", true, "103.112.54.21", "Kathmandu DC", "Ubuntu 24.04"),
    MobileVpsInstanceData("db-postgres-cluster", true, "103.112.54.89", "Kathmandu DC", "Debian 12"),
    MobileVpsInstanceData("babal-web-edge", true, "103.112.55.104", "Singapore SG-1", "Ubuntu 22.04"),
    MobileVpsInstanceData("dev-sandbox-nepal", false, "103.112.54.12", "Kathmandu DC", "AlmaLinux 9")
)

fun getMobileSampleStoragePools() = listOf(
    MobileStoragePoolData("vda-root-nvme-160", "160 GB", "prod-api-ktm01", "24,000", "In-Use"),
    MobileStoragePoolData("vol-postgres-data", "320 GB", "db-postgres-cluster", "48,200", "In-Use"),
    MobileStoragePoolData("vol-cdn-cache-01", "80 GB", "babal-web-edge", "18,000", "In-Use")
)

fun getMobileSampleSubnets() = listOf(
    MobileSubnetData("ktm-core-vpc-sub1", "10.0.1.0/24", "Kathmandu KTM-1"),
    MobileSubnetData("ktm-db-isolated-sub", "10.0.2.0/24", "Kathmandu KTM-1"),
    MobileSubnetData("sg-edge-transit-sub", "10.2.0.0/16", "Singapore SG-1")
)

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MobileAppPreview() {
    BabalMobileDarkCloudApp()
}

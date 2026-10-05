package com.example.myapplication.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// --- Design Tokens ---
val BabalNavy = Color(0xFF0B0F17)
val BabalSlate = Color(0xFF131B2E)
val BabalBorder = Color(0xFF1E293B)
val BabalOrange = Color(0xFFE05A10)
val BabalWhite = Color(0xFFF9FAFB)
val BabalMuted = Color(0xFF94A3B8)
val BabalGreen = Color(0xFF10B981)
val BabalGreenBg = Color(0xFF064E3B)
val BabalRed = Color(0xFFEF4444)

// --- Domain Models & State ---
sealed interface BabalConsoleState {
    object Loading : BabalConsoleState
    data class Success(
        val servers: List<BabalServer>,
        val cpuLoad: Float,
        val ramUsage: Float,
        val audits: List<BabalAudit>,
        val isOffline: Boolean = false
    ) : BabalConsoleState
    data class Error(val message: String) : BabalConsoleState
}

data class BabalServer(
    val id: String,
    val name: String,
    val role: String,
    val status: String,
    val ip: String,
    val region: String,
    val os: String,
    val cpuPercent: Float,
    val ramGb: Float
)

data class BabalAudit(
    val id: String,
    val timestamp: String,
    val user: String,
    val action: String,
    val ip: String,
    val status: String
)

// --- Simulated WebSocket & Repository Engine ---
class BabalOpsRepository {
    private val _state = MutableStateFlow<BabalConsoleState>(BabalConsoleState.Loading)
    val state: StateFlow<BabalConsoleState> = _state.asStateFlow()

    init {
        fetchInitialData()
        startTelemetryStream()
    }

    private fun fetchInitialData() {
        CoroutineScope(Dispatchers.IO).launch {
            delay(600)
            _state.value = BabalConsoleState.Success(
                servers = listOf(
                    BabalServer("vps-01", "prod-api-ktm01", "API", "Running", "103.112.54.21", "Kathmandu KTM-1", "Ubuntu 24.04 LTS", 0.42f, 5.2f),
                    BabalServer("vps-02", "db-postgres-cluster", "PRIMARY", "Running", "103.112.54.89", "Kathmandu KTM-1", "Debian 12", 0.68f, 14.5f),
                    BabalServer("vps-03", "babal-web-edge", "CDN", "Running", "103.112.55.104", "Singapore SG-1", "Ubuntu 22.04", 0.29f, 2.1f),
                    BabalServer("vps-04", "dev-sandbox-nepal", "DEV", "Stopped", "103.112.54.12", "Kathmandu KTM-1", "AlmaLinux 9", 0.0f, 0.0f)
                ),
                cpuLoad = 42.5f,
                ramUsage = 64.8f,
                audits = listOf(
                    BabalAudit("1", "14:24:02", "USR-001 (Suman S.)", "REBOOT_INSTANCE", "103.112.54.21", "SUCCESS"),
                    BabalAudit("2", "13:10:15", "USR-002 (DevOps)", "DEPLOY_SERVER", "103.112.54.89", "SUCCESS"),
                    BabalAudit("3", "11:05:42", "SYS-001 (AutoScaler)", "RESIZE_VOLUME", "10.0.1.42", "SUCCESS")
                )
            )
        }
    }

    private fun startTelemetryStream() {
        CoroutineScope(Dispatchers.IO).launch {
            while (true) {
                delay(2000)
                val current = _state.value
                if (current is BabalConsoleState.Success) {
                    val fluctuatedCpu = (35..65).random().toFloat()
                    _state.value = current.copy(cpuLoad = fluctuatedCpu)
                }
            }
        }
    }

    fun refresh() {
        fetchInitialData()
    }
}

// --- Navigation Routes ---
enum class BabalRoute(val title: String, val icon: ImageVector) {
    COMPUTE("Compute Fleet", Icons.Default.Computer),
    TERMINAL("Web SSH Terminal", Icons.Default.Terminal),
    ANALYTICS("Telemetry Charts", Icons.AutoMirrored.Filled.ShowChart),
    AUDIT("Audit Logs", Icons.Default.History)
}

// --- Root Application Composable ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BabalEnterpriseOpsApp() {
    val repository = remember { BabalOpsRepository() }
    val state by repository.state.collectAsState()
    var currentRoute by remember { mutableStateOf(BabalRoute.COMPUTE) }
    var selectedServer by remember { mutableStateOf<BabalServer?>(null) }
    var showDeployWizard by remember { mutableStateOf(false) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val haptic = LocalHapticFeedback.current

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = BabalSlate,
                modifier = Modifier.width(280.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Babal Cloud", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = BabalWhite)
                    Text("VPS OPS CONSOLE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BabalOrange)
                }
                HorizontalDivider(color = BabalBorder, modifier = Modifier.padding(vertical = 8.dp))

                BabalRoute.entries.forEach { route ->
                    NavigationDrawerItem(
                        icon = { Icon(route.icon, contentDescription = null, tint = if (currentRoute == route) Color.White else BabalMuted) },
                        label = { Text(route.title, color = if (currentRoute == route) Color.White else BabalMuted) },
                        selected = currentRoute == route,
                        onClick = {
                            currentRoute = route
                            scope.launch { drawerState.close() }
                        },
                        colors = NavigationDrawerItemDefaults.colors(selectedContainerColor = BabalOrange)
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                // Bottom Quota Card
                Card(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    colors = CardDefaults.cardColors(containerColor = BabalNavy),
                    border = BorderStroke(1.dp, BabalBorder)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Tier Quota", fontSize = 11.sp, color = BabalMuted)
                            Text("82% Used", fontSize = 11.sp, color = BabalOrange, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        LinearProgressIndicator(
                            progress = { 0.82f },
                            modifier = Modifier.fillMaxWidth().height(5.dp).clip(RoundedCornerShape(3.dp)),
                            color = BabalOrange,
                            trackColor = BabalBorder
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("8 of 10 VPS active in KTM-1", fontSize = 10.sp, color = BabalMuted)
                    }
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopHeaderBar(
                    onMenuClick = { scope.launch { drawerState.open() } },
                    onRefreshClick = { repository.refresh() }
                )
            },
            containerColor = BabalNavy
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                when (val st = state) {
                    is BabalConsoleState.Loading -> BabalSkeletonLoader()
                    is BabalConsoleState.Error -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(st.message, color = BabalRed)
                    }
                    is BabalConsoleState.Success -> {
                        when (currentRoute) {
                            BabalRoute.COMPUTE -> ComputeFleetView(
                                servers = st.servers,
                                onDeployClick = { showDeployWizard = true },
                                onServerClick = { server -> selectedServer = server },
                                onTerminalClick = { currentRoute = BabalRoute.TERMINAL }
                            )
                            BabalRoute.TERMINAL -> SshTerminalView()
                            BabalRoute.ANALYTICS -> TelemetryAnalyticsView()
                            BabalRoute.AUDIT -> AuditLogsView()
                        }

                        if (selectedServer != null) {
                            ServerDetailActionSheet(
                                server = selectedServer!!,
                                onDismiss = { selectedServer = null },
                                onOpenTerminal = { currentRoute = BabalRoute.TERMINAL; selectedServer = null },
                                haptic = haptic
                            )
                        }

                        if (showDeployWizard) {
                            DeployServerProvisioningWizard(
                                onDismiss = { showDeployWizard = false },
                                onDeployed = {
                                    showDeployWizard = false
                                    repository.refresh()
                                },
                                haptic = haptic
                            )
                        }
                    }
                }
            }
        }
    }
}

// --- Section A: Top Navigation & Header (`TopHeaderBar.kt`) ---
@Composable
fun TopHeaderBar(
    onMenuClick: () -> Unit,
    onRefreshClick: () -> Unit
) {
    Surface(
        color = BabalSlate,
        shadowElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(onClick = onMenuClick, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.Default.Menu, contentDescription = "Navigation Menu", tint = BabalWhite)
                }
                Column {
                    Text(
                        text = "Babal Cloud",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = BabalWhite
                    )
                    Text(
                        text = "VPS OPS CONSOLE",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = BabalOrange
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(onClick = onRefreshClick, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Refresh, contentDescription = "Refresh", tint = BabalWhite)
                }

                // Region Badge Pill
                Surface(
                    color = BabalNavy,
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, BabalBorder)
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
                                .background(BabalGreen)
                        )
                        Text(
                            text = "Region: Nepal (KTM-1)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = BabalWhite
                        )
                    }
                }

                // Admin Profile Avatar
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(BabalNavy)
                        .border(1.dp, BabalBorder, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = "Suman Shrestha - Root Administrator",
                        tint = BabalWhite,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

// --- Feature 1: Compute Fleet Overview (`/compute`) ---
@Composable
fun ComputeFleetView(
    servers: List<BabalServer>,
    onDeployClick: () -> Unit,
    onServerClick: (BabalServer) -> Unit,
    onTerminalClick: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item { Spacer(modifier = Modifier.height(4.dp)) }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Cloud VPS & Compute Fleet", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = BabalWhite)
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Box(modifier = Modifier.size(7.dp).clip(CircleShape).background(BabalGreen))
                        Text("KTM-1 DC: 99.98% Uptime", fontSize = 11.sp, color = BabalMuted)
                    }
                }
                Button(
                    onClick = onDeployClick,
                    colors = ButtonDefaults.buttonColors(containerColor = BabalOrange),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Deploy Instance", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // 2x2 Telemetry Metrics Grid
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    BabalMetricCard(Modifier.weight(1f), "ACTIVE COMPUTE", "18 Running", "99.98% SLA", isGreen = true)
                    BabalMetricCard(Modifier.weight(1f), "TOTAL vCPU", "64 / 96 Cores", "66% Load", progress = 0.66f)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    BabalMetricCard(Modifier.weight(1f), "RAM ALLOCATED", "128 / 192 GB", "Active: 84 GB")
                    BabalMetricCard(Modifier.weight(1f), "NVMe STORAGE", "3.2 / 6.0 TB", "48,200 IOPS")
                }
            }
        }

        item {
            Text("Server Nodes Overview", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = BabalWhite)
        }

        items(servers, key = { it.id }) { server ->
            BabalServerCard(
                server = server,
                onClick = { onServerClick(server) },
                onTerminalClick = onTerminalClick
            )
        }

        item { Spacer(modifier = Modifier.height(20.dp)) }
    }
}

@Composable
fun BabalMetricCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    subValue: String,
    isGreen: Boolean = false,
    progress: Float? = null
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = BabalSlate),
        border = BorderStroke(1.dp, BabalBorder),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(title, fontSize = 9.sp, color = BabalMuted, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = if (isGreen) BabalGreen else BabalWhite)
            if (progress != null) {
                Spacer(modifier = Modifier.height(6.dp))
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
                    color = BabalOrange,
                    trackColor = BabalNavy
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(subValue, fontSize = 10.sp, color = BabalMuted)
        }
    }
}

@Composable
fun BabalServerCard(
    server: BabalServer,
    onClick: () -> Unit,
    onTerminalClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = BabalSlate),
        border = BorderStroke(1.dp, BabalBorder),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(server.name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = BabalWhite)
                    Surface(color = BabalNavy, shape = RoundedCornerShape(4.dp), border = BorderStroke(1.dp, BabalBorder)) {
                        Text(server.role, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = BabalWhite, modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp))
                    }
                }
                Surface(color = if (server.status == "Running") BabalGreenBg else BabalNavy, shape = RoundedCornerShape(12.dp)) {
                    Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(if (server.status == "Running") BabalGreen else BabalMuted))
                        Text(server.status, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = if (server.status == "Running") BabalGreen else BabalMuted)
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = BabalBorder)
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("IP ADDRESS", fontSize = 9.sp, color = BabalMuted)
                    Text(server.ip, fontSize = 11.sp, color = BabalOrange, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("REGION", fontSize = 9.sp, color = BabalMuted)
                    Text(server.region, fontSize = 11.sp, color = BabalWhite, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
                IconButton(onClick = onTerminalClick, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Terminal, contentDescription = "Terminal", tint = BabalOrange, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

// --- Feature 2: Interactive Web SSH Terminal & Syslog View (`/terminal`) ---
@Composable
fun SshTerminalView() {
    var commandInput by remember { mutableStateOf("") }
    val logs = remember {
        mutableStateListOf(
            "[14:22:04] [systemd] Started Babal High Performance API Stack daemon.",
            "[14:22:10] [docker] Container api-service-worker transitioned to HEALTHY.",
            "[14:22:15] [kernel] eth0: NIC physical link up, speed 10000 Mbps, full-duplex.",
            "[14:23:45] [sshd] Accepted publickey for root from 202.70.76.12 port 51240."
        )
    }

    Column(
        modifier = Modifier.fillMaxSize().background(Color(0xFF060910)).padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("root@prod-api-ktm01:~#", fontSize = 13.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, color = BabalGreen)
            Text("WSS TLS 1.3", fontSize = 11.sp, color = BabalOrange)
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
            listOf("docker ps -a", "systemctl status nginx", "top", "free -m -h", "ufw status").forEach { macro ->
                Surface(
                    modifier = Modifier.clickable {
                        logs.add("root@prod-api-ktm01:~# $macro")
                        logs.add("[OK] Executed $macro successfully.")
                    },
                    color = BabalSlate,
                    shape = RoundedCornerShape(4.dp),
                    border = BorderStroke(1.dp, BabalBorder)
                ) {
                    Text(macro, fontSize = 9.sp, color = BabalOrange, fontFamily = FontFamily.Monospace, modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Surface(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            color = Color(0xFF090D16),
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(1.dp, BabalBorder)
        ) {
            LazyColumn(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                items(logs) { log ->
                    Text(
                        text = log,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        color = if (log.contains("[OK]") || log.contains("systemd")) BabalGreen else BabalMuted
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = commandInput,
            onValueChange = { commandInput = it },
            placeholder = { Text("Enter terminal command...", fontSize = 12.sp, color = BabalMuted) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = BabalSlate,
                unfocusedContainerColor = BabalSlate,
                focusedBorderColor = BabalOrange,
                unfocusedBorderColor = BabalBorder,
                focusedTextColor = BabalWhite,
                unfocusedTextColor = BabalWhite
            )
        )
    }
}

// --- Feature 3: Single Instance Detail & Lifecycle Operations (`/instance/[id]`) ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServerDetailActionSheet(
    server: BabalServer,
    onDismiss: () -> Unit,
    onOpenTerminal: () -> Unit,
    haptic: HapticFeedback
) {
    var showConfirmDialog by remember { mutableStateOf(false) }
    var actionType by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = BabalSlate
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text(server.name, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = BabalWhite)
                    Text("ID: ${server.id} • ${server.region}", fontSize = 11.sp, color = BabalMuted)
                }
                Button(
                    onClick = onOpenTerminal,
                    colors = ButtonDefaults.buttonColors(containerColor = BabalOrange),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.Terminal, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Terminal", fontSize = 11.sp)
                }
            }

            HorizontalDivider(color = BabalBorder)

            Text("Lifecycle & Power Controls", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = BabalWhite)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = { actionType = "Reboot"; showConfirmDialog = true; haptic.performHapticFeedback(HapticFeedbackType.LongPress) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = BabalNavy),
                    border = BorderStroke(1.dp, BabalBorder)
                ) {
                    Text("Reboot", fontSize = 11.sp, color = BabalWhite)
                }
                Button(
                    onClick = { actionType = "Shutdown"; showConfirmDialog = true; haptic.performHapticFeedback(HapticFeedbackType.LongPress) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = BabalNavy),
                    border = BorderStroke(1.dp, BabalRed)
                ) {
                    Text("Shutdown", fontSize = 11.sp, color = BabalRed)
                }
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = { actionType = "Emergency Kill"; showConfirmDialog = true; haptic.performHapticFeedback(HapticFeedbackType.LongPress) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = BabalNavy),
                    border = BorderStroke(1.dp, BabalRed)
                ) {
                    Text("Emergency Kill", fontSize = 11.sp, color = BabalRed)
                }
                Button(
                    onClick = { actionType = "Take Snapshot"; showConfirmDialog = true; haptic.performHapticFeedback(HapticFeedbackType.LongPress) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = BabalNavy),
                    border = BorderStroke(1.dp, BabalBorder)
                ) {
                    Text("Snapshot", fontSize = 11.sp, color = BabalWhite)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }

    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text("Confirm $actionType", color = BabalWhite) },
            text = { Text("Are you sure you want to execute $actionType on ${server.name}?", color = BabalMuted) },
            confirmButton = {
                Button(onClick = { showConfirmDialog = false; onDismiss() }, colors = ButtonDefaults.buttonColors(containerColor = BabalRed)) {
                    Text("Proceed", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) {
                    Text("Cancel", color = BabalMuted)
                }
            },
            containerColor = BabalSlate
        )
    }
}

// --- Telemetry Analytics View (`/analytics`) ---
@Composable
fun TelemetryAnalyticsView() {
    Column(modifier = Modifier.fillMaxSize().background(BabalNavy).padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text("Fleet Telemetry & Analytics", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = BabalWhite)
        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            item { AnalyticsCard("CPU Utilization Trend", "42% Normal (Peak: 78%)", 0.42f, BabalOrange) }
            item { AnalyticsCard("Memory Allocation", "5.20 / 8.00 GB (65%)", 0.65f, Color(0xFF3B82F6)) }
            item { AnalyticsCard("NVMe Storage I/O", "12,400 Read IOPS • 8,200 Write IOPS", 0.58f, Color(0xFF8B5CF6)) }
            item { AnalyticsCard("Network Bandwidth", "48.2 Mbps In / 112.4 Mbps Out", 0.36f, BabalGreen) }
        }
    }
}

@Composable
fun AnalyticsCard(title: String, subtitle: String, progress: Float, accentColor: Color) {
    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = BabalSlate), border = BorderStroke(1.dp, BabalBorder)) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = BabalWhite)
            Spacer(modifier = Modifier.height(4.dp))
            Text(subtitle, fontSize = 11.sp, color = BabalMuted)
            Spacer(modifier = Modifier.height(10.dp))
            LinearProgressIndicator(progress = { progress }, modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)), color = accentColor, trackColor = BabalNavy)
        }
    }
}

// --- Feature 4: Deploy New Server Provisioning Wizard (`/deploy`) ---
@Composable
fun DeployServerProvisioningWizard(onDismiss: () -> Unit, onDeployed: () -> Unit, haptic: HapticFeedback) {
    var step by remember { mutableStateOf(1) }
    var selectedRegion by remember { mutableStateOf("Kathmandu KTM-1 (4ms)") }
    var selectedOs by remember { mutableStateOf("Ubuntu 24.04 LTS") }
    var selectedTier by remember { mutableStateOf("Pro 4 (NPR 2,400/mo)") }
    var hostname by remember { mutableStateOf("prod-api-ktm02") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Deploy New Cloud VPS (Step $step / 4)", color = BabalWhite, fontSize = 16.sp) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                when (step) {
                    1 -> {
                        Text("Select Datacenter Region", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = BabalWhite)
                        listOf("Kathmandu KTM-1 (4ms)", "Singapore SG-1 (48ms)", "Mumbai BOM-1 (32ms)").forEach { reg ->
                            Row(modifier = Modifier.fillMaxWidth().clickable { selectedRegion = reg }.background(if (selectedRegion == reg) BabalNavy else Color.Transparent, RoundedCornerShape(6.dp)).padding(8.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(reg, fontSize = 12.sp, color = BabalWhite)
                                RadioButton(selected = selectedRegion == reg, onClick = { selectedRegion = reg })
                            }
                        }
                    }
                    2 -> {
                        Text("Choose Operating System", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = BabalWhite)
                        listOf("Ubuntu 24.04 LTS", "Debian 12", "AlmaLinux 9", "Rocky Linux 9", "Windows Server 2022").forEach { os ->
                            Row(modifier = Modifier.fillMaxWidth().clickable { selectedOs = os }.background(if (selectedOs == os) BabalNavy else Color.Transparent, RoundedCornerShape(6.dp)).padding(8.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(os, fontSize = 12.sp, color = BabalWhite)
                                RadioButton(selected = selectedOs == os, onClick = { selectedOs = os })
                            }
                        }
                    }
                    3 -> {
                        Text("Hardware Tier & Specs", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = BabalWhite)
                        listOf("Standard 2 (NPR 1,200/mo)", "Pro 4 (NPR 2,400/mo)", "Compute 8 (NPR 4,600/mo)", "Memory 16 (NPR 8,200/mo)").forEach { tier ->
                            Row(modifier = Modifier.fillMaxWidth().clickable { selectedTier = tier }.background(if (selectedTier == tier) BabalNavy else Color.Transparent, RoundedCornerShape(6.dp)).padding(8.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(tier, fontSize = 12.sp, color = BabalWhite)
                                RadioButton(selected = selectedTier == tier, onClick = { selectedTier = tier })
                            }
                        }
                    }
                    4 -> {
                        Text("Authentication & Hostname", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = BabalWhite)
                        OutlinedTextField(value = hostname, onValueChange = { hostname = it }, label = { Text("Instance Hostname") }, singleLine = true, colors = OutlinedTextFieldDefaults.colors(focusedTextColor = BabalWhite, unfocusedTextColor = BabalWhite))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Total Due Today: NPR 2,994.50 (incl. 13% VAT)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = BabalOrange)
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = { if (step < 4) step++ else { haptic.performHapticFeedback(HapticFeedbackType.LongPress); onDeployed() } }, colors = ButtonDefaults.buttonColors(containerColor = BabalOrange)) {
                Text(if (step < 4) "Next" else "Deploy Instance Now", color = Color.White)
            }
        },
        dismissButton = {
            if (step > 1) {
                TextButton(onClick = { step-- }) { Text("Back", color = BabalMuted) }
            }
        },
        containerColor = BabalSlate
    )
}

// --- Feature 5: Alerts, Audit Logs & Notifications (`/alerts`) ---
@Composable
fun AuditLogsView() {
    val audits = listOf(
        BabalAudit("1", "14:24:02", "USR-001 (Suman S.)", "REBOOT_INSTANCE", "103.112.54.21", "SUCCESS"),
        BabalAudit("2", "13:10:15", "USR-002 (DevOps)", "DEPLOY_SERVER", "103.112.54.89", "SUCCESS"),
        BabalAudit("3", "11:05:42", "SYS-001 (AutoScaler)", "RESIZE_VOLUME", "10.0.1.42", "SUCCESS")
    )

    Column(modifier = Modifier.fillMaxSize().background(BabalNavy).padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text("Audit Logs & Activity Stream", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = BabalWhite)
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(audits) { audit ->
                Card(colors = CardDefaults.cardColors(containerColor = BabalSlate), border = BorderStroke(1.dp, BabalBorder)) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(audit.action, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = BabalOrange)
                            Surface(color = BabalGreenBg, shape = RoundedCornerShape(4.dp)) {
                                Text(audit.status, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = BabalGreen, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                            }
                        }
                        Text("User: ${audit.user}", fontSize = 11.sp, color = BabalWhite)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("IP: ${audit.ip}", fontSize = 10.sp, color = BabalMuted)
                            Text(audit.timestamp, fontSize = 10.sp, color = BabalMuted)
                        }
                    }
                }
            }
        }
    }
}

// --- Skeleton Loader ---
@Composable
fun BabalSkeletonLoader() {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Box(modifier = Modifier.fillMaxWidth().height(50.dp).clip(RoundedCornerShape(8.dp)).background(BabalSlate))
        Box(modifier = Modifier.fillMaxWidth().height(120.dp).clip(RoundedCornerShape(12.dp)).background(BabalSlate))
        Box(modifier = Modifier.fillMaxWidth().height(200.dp).clip(RoundedCornerShape(12.dp)).background(BabalSlate))
    }
}

package com.example.myapplication.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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

// --- Enterprise Design System ---
val EntBg = Color(0xFF090D16)
val EntSurface = Color(0xFF111827)
val EntBorder = Color(0xFF1F2937)
val EntOrange = Color(0xFFE05A10)
val EntWhite = Color(0xFFF9FAFB)
val EntMuted = Color(0xFF9CA3AF)
val EntGreen = Color(0xFF10B981)
val EntGreenBg = Color(0xFF064E3B)
val EntRed = Color(0xFFEF4444)

// --- Domain Models ---
sealed interface ConsoleUiState {
    object Loading : ConsoleUiState
    data class Success(
        val servers: List<EnterpriseServer>,
        val cpuLoad: Float,
        val ramUsage: Float,
        val alerts: List<SystemAlert>
    ) : ConsoleUiState
    data class Error(val message: String) : ConsoleUiState
}

data class EnterpriseServer(
    val id: String,
    val name: String,
    val role: String,
    val status: String, // "Running" or "Stopped"
    val ip: String,
    val region: String,
    val datacenter: String = "$region, Kathmandu DC",
    val vcpus: Int,
    val ramGb: Int,
    val storageGb: Int,
    val cpuPercent: Float,
    val ramPercent: Float
)

data class SystemAlert(
    val id: String,
    val title: String,
    val time: String,
    val severity: String // INFO, WARN, CRITICAL
)

// --- ViewModel State Holder ---
class EnterpriseConsoleViewModel {
    private val _uiState = MutableStateFlow<ConsoleUiState>(ConsoleUiState.Loading)
    val uiState: StateFlow<ConsoleUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        _uiState.value = ConsoleUiState.Loading
        CoroutineScope(Dispatchers.IO).launch {
            delay(500)
            _uiState.value = ConsoleUiState.Success(
                servers = listOf(
                    EnterpriseServer("vps-01", "prod-api-ktm01", "API", "Running", "103.112.54.21", "Kathmandu KTM-1", "Babal KTM-1, Kathmandu DC", 4, 8, 160, 0.42f, 0.65f),
                    EnterpriseServer("vps-02", "db-postgres-cluster", "PRIMARY", "Running", "103.112.54.89", "Kathmandu KTM-1", "Babal KTM-1, Kathmandu DC", 8, 16, 320, 0.68f, 0.82f),
                    EnterpriseServer("vps-03", "babal-web-edge", "CDN", "Running", "103.112.55.104", "Singapore SG-1", "Singapore SG-1, Equinix", 2, 4, 80, 0.29f, 0.44f),
                    EnterpriseServer("vps-04", "dev-sandbox-nepal", "DEV", "Stopped", "103.112.54.12", "Kathmandu KTM-1", "Babal KTM-1, Kathmandu DC", 2, 4, 80, 0.0f, 0.0f)
                ),
                cpuLoad = 42.5f,
                ramUsage = 64.8f,
                alerts = listOf(
                    SystemAlert("1", "DDoS mitigation active on KTM-1 edge boundary", "2 mins ago", "INFO"),
                    SystemAlert("2", "Automated NVMe snapshot successfully completed", "15 mins ago", "INFO"),
                    SystemAlert("3", "High memory warning on db-postgres-cluster (82%)", "1 hour ago", "WARN")
                )
            )
        }
    }
}

// --- Main Enterprise Console App Composable ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnterpriseConsoleApp() {
    val viewModel = remember { EnterpriseConsoleViewModel() }
    val state by viewModel.uiState.collectAsState()
    var selectedServer by remember { mutableStateOf<EnterpriseServer?>(null) }
    var showDeployWizard by remember { mutableStateOf(false) }
    var showTerminal by remember { mutableStateOf(false) }
    var showAlertCenter by remember { mutableStateOf(false) }
    val haptic = LocalHapticFeedback.current

    Box(modifier = Modifier.fillMaxSize().background(EntBg)) {
        when (val st = state) {
            is ConsoleUiState.Loading -> {
                EnterpriseSkeletonLoader()
            }
            is ConsoleUiState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(st.message, color = EntRed)
                }
            }
            is ConsoleUiState.Success -> {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Box(modifier = Modifier.size(24.dp).clip(RoundedCornerShape(6.dp)).background(EntOrange), contentAlignment = Alignment.Center) {
                                        Icon(Icons.Default.Cloud, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                    }
                                    Text("Babal Cloud Ops", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = EntWhite)
                                }
                            },
                            actions = {
                                IconButton(onClick = { showAlertCenter = true }) {
                                    Badge(containerColor = EntOrange) {
                                        Icon(Icons.Default.Notifications, contentDescription = "Alerts", tint = EntWhite)
                                    }
                                }
                            },
                            colors = TopAppBarDefaults.topAppBarColors(containerColor = EntSurface)
                        )
                    },
                    containerColor = EntBg
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        EnterpriseDashboardView(
                            servers = st.servers,
                            onDeployClick = { showDeployWizard = true },
                            onServerClick = { server -> selectedServer = server },
                            onTerminalClick = { server ->
                                selectedServer = server
                                showTerminal = true
                            }
                        )
                    }
                }

                // Server Detail Action Sheet Bottom Sheet
                if (selectedServer != null && !showTerminal) {
                    ServerDetailBottomSheet(
                        server = selectedServer!!,
                        onDismiss = { selectedServer = null },
                        onOpenTerminal = { showTerminal = true },
                        haptic = haptic
                    )
                }

                // Live Terminal Screen
                if (showTerminal && selectedServer != null) {
                    LiveTerminalScreen(
                        serverName = selectedServer!!.name,
                        onBack = {
                            showTerminal = false
                            selectedServer = null
                        }
                    )
                }

                // Deploy Server Wizard Dialog
                if (showDeployWizard) {
                    DeployServerWizard(
                        onDismiss = { showDeployWizard = false },
                        onDeployed = {
                            showDeployWizard = false
                            viewModel.loadData()
                        },
                        haptic = haptic
                    )
                }

                // Alert Center Drawer / Dialog
                if (showAlertCenter) {
                    AlertDialog(
                        onDismissRequest = { showAlertCenter = false },
                        title = { Text("System Health & Alert Center", color = EntWhite) },
                        text = {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                st.alerts.forEach { alert ->
                                    Card(
                                        colors = CardDefaults.cardColors(containerColor = EntBg),
                                        border = BorderStroke(1.dp, EntBorder)
                                    ) {
                                        Column(modifier = Modifier.padding(10.dp)) {
                                            Text(alert.title, color = EntWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(alert.time, color = EntMuted, fontSize = 10.sp)
                                        }
                                    }
                                }
                            }
                        },
                        confirmButton = {
                            TextButton(onClick = { showAlertCenter = false }) {
                                Text("Close", color = EntOrange)
                            }
                        },
                        containerColor = EntSurface
                    )
                }
            }
        }
    }
}

// --- Dashboard View ---
@Composable
fun EnterpriseDashboardView(
    servers: List<EnterpriseServer>,
    onDeployClick: () -> Unit,
    onServerClick: (EnterpriseServer) -> Unit,
    onTerminalClick: (EnterpriseServer) -> Unit
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
                    Text("Compute Fleet", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = EntWhite)
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Box(modifier = Modifier.size(7.dp).clip(CircleShape).background(EntGreen))
                        Text("Mesh Synchronized • Kathmandu Edge Active", fontSize = 11.sp, color = EntMuted)
                    }
                }
                Button(
                    onClick = onDeployClick,
                    colors = ButtonDefaults.buttonColors(containerColor = EntOrange),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Deploy VPS", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    EnterpriseMetricCard(Modifier.weight(1f), "ACTIVE COMPUTE", "18 Running", "99.98% SLA", isSuccess = true)
                    EnterpriseMetricCard(Modifier.weight(1f), "TOTAL vCPU", "64 / 96 Cores", "66% Load", progress = 0.66f)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    EnterpriseMetricCard(Modifier.weight(1f), "RAM ALLOCATED", "128 / 192 GB", "Active: 84 GB")
                    EnterpriseMetricCard(Modifier.weight(1f), "NVMe STORAGE", "3.2 / 6.0 TB", "48,200 IOPS")
                }
            }
        }

        item {
            Text("Server Nodes Overview", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = EntWhite)
        }

        items(servers, key = { it.id }) { server ->
            EnterpriseServerCard(
                server = server,
                onClick = { onServerClick(server) },
                onTerminalClick = { onTerminalClick(server) }
            )
        }

        item { Spacer(modifier = Modifier.height(20.dp)) }
    }
}

// --- Skeleton Loader ---
@Composable
fun EnterpriseSkeletonLoader() {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth().height(50.dp).clip(RoundedCornerShape(8.dp)).background(EntSurface))
        Box(modifier = Modifier.fillMaxWidth().height(120.dp).clip(RoundedCornerShape(12.dp)).background(EntSurface))
        Box(modifier = Modifier.fillMaxWidth().height(200.dp).clip(RoundedCornerShape(12.dp)).background(EntSurface))
    }
}

// --- Metric Card ---
@Composable
fun EnterpriseMetricCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    subValue: String,
    isSuccess: Boolean = false,
    progress: Float? = null
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = EntSurface),
        border = BorderStroke(1.dp, EntBorder),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(title, fontSize = 9.sp, color = EntMuted, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = if (isSuccess) EntGreen else EntWhite)
            if (progress != null) {
                Spacer(modifier = Modifier.height(6.dp))
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
                    color = EntOrange,
                    trackColor = EntBg
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(subValue, fontSize = 10.sp, color = EntMuted)
        }
    }
}

// --- Server Node Card ---
@Composable
fun EnterpriseServerCard(
    server: EnterpriseServer,
    onClick: () -> Unit,
    onTerminalClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = EntSurface),
        border = BorderStroke(1.dp, EntBorder),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(server.name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = EntWhite)
                    Surface(color = EntBg, shape = RoundedCornerShape(4.dp), border = BorderStroke(1.dp, EntBorder)) {
                        Text(server.role, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = EntWhite, modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp))
                    }
                }
                Surface(color = if (server.status == "Running") EntGreenBg else EntBg, shape = RoundedCornerShape(12.dp)) {
                    Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(if (server.status == "Running") EntGreen else EntMuted))
                        Text(server.status, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = if (server.status == "Running") EntGreen else EntMuted)
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = EntBorder)
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("IP ADDRESS", fontSize = 9.sp, color = EntMuted)
                    Text(server.ip, fontSize = 11.sp, color = EntOrange, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("REGION", fontSize = 9.sp, color = EntMuted)
                    Text(server.region, fontSize = 11.sp, color = EntWhite, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
                IconButton(onClick = onTerminalClick, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Terminal, contentDescription = "Terminal", tint = EntOrange, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

// --- 2. Live Syslog Terminal View ---
@Composable
fun LiveTerminalScreen(serverName: String, onBack: () -> Unit) {
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
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = EntWhite)
            }
            Text("root@$serverName:~#", fontSize = 13.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, color = EntGreen)
            Text("WSS TLS 1.3", fontSize = 11.sp, color = EntOrange)
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
            listOf("docker ps", "systemctl status", "top", "ufw status").forEach { macro ->
                Surface(
                    modifier = Modifier.clickable {
                        logs.add("root@$serverName:~# $macro")
                        logs.add("[OK] Executed $macro successfully.")
                    },
                    color = EntSurface,
                    shape = RoundedCornerShape(4.dp),
                    border = BorderStroke(1.dp, EntBorder)
                ) {
                    Text(macro, fontSize = 10.sp, color = EntOrange, fontFamily = FontFamily.Monospace, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Surface(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            color = Color(0xFF090D16),
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(1.dp, EntBorder)
        ) {
            LazyColumn(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                items(logs) { log ->
                    Text(
                        text = log,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        color = if (log.contains("[OK]") || log.contains("systemd")) EntGreen else EntMuted
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = commandInput,
            onValueChange = { commandInput = it },
            placeholder = { Text("Enter terminal command...", fontSize = 12.sp, color = EntMuted) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = EntSurface,
                unfocusedContainerColor = EntSurface,
                focusedBorderColor = EntOrange,
                unfocusedBorderColor = EntBorder,
                focusedTextColor = EntWhite,
                unfocusedTextColor = EntWhite
            )
        )
    }
}

// --- 3. Interactive Server Operations & Controls (ServerDetailBottomSheet) ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServerDetailBottomSheet(
    server: EnterpriseServer,
    onDismiss: () -> Unit,
    onOpenTerminal: () -> Unit,
    haptic: HapticFeedback
) {
    var vcpus by remember { mutableStateOf(server.vcpus.toFloat()) }
    var ramGb by remember { mutableStateOf(server.ramGb.toFloat()) }
    var sshPortEnabled by remember { mutableStateOf(true) }
    var httpPortEnabled by remember { mutableStateOf(true) }
    var dbPortEnabled by remember { mutableStateOf(false) }
    var showConfirmDialog by remember { mutableStateOf(false) }
    var actionType by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = EntSurface
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text(server.name, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = EntWhite)
                    Text("ID: ${server.id} • ${server.datacenter}", fontSize = 11.sp, color = EntMuted)
                }
                Button(
                    onClick = onOpenTerminal,
                    colors = ButtonDefaults.buttonColors(containerColor = EntOrange),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.Terminal, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Terminal", fontSize = 11.sp)
                }
            }

            HorizontalDivider(color = EntBorder)

            Text("Power Operations", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = EntWhite)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = { actionType = "Reboot"; showConfirmDialog = true; haptic.performHapticFeedback(HapticFeedbackType.LongPress) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = EntBg),
                    border = BorderStroke(1.dp, EntBorder)
                ) {
                    Text("Reboot", fontSize = 11.sp, color = EntWhite)
                }
                Button(
                    onClick = { actionType = "Shutdown"; showConfirmDialog = true; haptic.performHapticFeedback(HapticFeedbackType.LongPress) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = EntBg),
                    border = BorderStroke(1.dp, EntRed)
                ) {
                    Text("Shutdown", fontSize = 11.sp, color = EntRed)
                }
            }

            Text("Resource Scaling (vCPU: ${vcpus.toInt()} | RAM: ${ramGb.toInt()} GB)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = EntWhite)
            Slider(
                value = vcpus,
                onValueChange = { vcpus = it },
                valueRange = 2f..16f,
                steps = 6,
                colors = SliderDefaults.colors(thumbColor = EntOrange, activeTrackColor = EntOrange)
            )

            Text("Firewall Policy Manager", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = EntWhite)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("SSH (Port 22)", fontSize = 12.sp, color = EntWhite)
                Switch(checked = sshPortEnabled, onCheckedChange = { sshPortEnabled = it }, colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = EntOrange))
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("HTTP/HTTPS (Ports 80/443)", fontSize = 12.sp, color = EntWhite)
                Switch(checked = httpPortEnabled, onCheckedChange = { httpPortEnabled = it }, colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = EntOrange))
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("PostgreSQL (Port 5432 - VPC Only)", fontSize = 12.sp, color = EntWhite)
                Switch(checked = dbPortEnabled, onCheckedChange = { dbPortEnabled = it }, colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = EntOrange))
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }

    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text("Confirm $actionType", color = EntWhite) },
            text = { Text("Are you sure you want to $actionType instance ${server.name}?", color = EntMuted) },
            confirmButton = {
                Button(onClick = { showConfirmDialog = false; onDismiss() }, colors = ButtonDefaults.buttonColors(containerColor = EntRed)) {
                    Text("Proceed", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) {
                    Text("Cancel", color = EntMuted)
                }
            },
            containerColor = EntSurface
        )
    }
}

// --- 4. Interactive Provisioning Wizard ---
@Composable
fun DeployServerWizard(onDismiss: () -> Unit, onDeployed: () -> Unit, haptic: HapticFeedback) {
    var step by remember { mutableStateOf(1) }
    var selectedRegion by remember { mutableStateOf("Kathmandu KTM-1 (4ms)") }
    var selectedOs by remember { mutableStateOf("Ubuntu 24.04 LTS") }
    var selectedTier by remember { mutableStateOf("Pro 4 (NPR 2,400/mo)") }
    var hostname by remember { mutableStateOf("prod-api-ktm02") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Deploy New Cloud VPS (Step $step / 4)", color = EntWhite, fontSize = 16.sp) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                when (step) {
                    1 -> {
                        Text("Select Datacenter Region", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = EntWhite)
                        listOf("Kathmandu KTM-1 (4ms)", "Singapore SG-1 (48ms)", "Mumbai BOM-1 (32ms)").forEach { reg ->
                            Row(
                                modifier = Modifier.fillMaxWidth().clickable { selectedRegion = reg }.background(if (selectedRegion == reg) EntBg else Color.Transparent, RoundedCornerShape(6.dp)).padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(reg, fontSize = 12.sp, color = EntWhite)
                                RadioButton(selected = selectedRegion == reg, onClick = { selectedRegion = reg })
                            }
                        }
                    }
                    2 -> {
                        Text("Choose Operating System", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = EntWhite)
                        listOf("Ubuntu 24.04 LTS", "Debian 12", "AlmaLinux 9", "Rocky Linux 9", "Windows Server 2022").forEach { os ->
                            Row(
                                modifier = Modifier.fillMaxWidth().clickable { selectedOs = os }.background(if (selectedOs == os) EntBg else Color.Transparent, RoundedCornerShape(6.dp)).padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(os, fontSize = 12.sp, color = EntWhite)
                                RadioButton(selected = selectedOs == os, onClick = { selectedOs = os })
                            }
                        }
                    }
                    3 -> {
                        Text("Hardware Tier & Specs", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = EntWhite)
                        listOf("Standard 2 (NPR 1,200/mo)", "Pro 4 (NPR 2,400/mo)", "Compute 8 (NPR 4,600/mo)", "Memory 16 (NPR 8,200/mo)").forEach { tier ->
                            Row(
                                modifier = Modifier.fillMaxWidth().clickable { selectedTier = tier }.background(if (selectedTier == tier) EntBg else Color.Transparent, RoundedCornerShape(6.dp)).padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(tier, fontSize = 12.sp, color = EntWhite)
                                RadioButton(selected = selectedTier == tier, onClick = { selectedTier = tier })
                            }
                        }
                    }
                    4 -> {
                        Text("Authentication & Hostname", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = EntWhite)
                        OutlinedTextField(
                            value = hostname,
                            onValueChange = { hostname = it },
                            label = { Text("Instance Hostname") },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = EntWhite, unfocusedTextColor = EntWhite)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Total Due Today: NPR 2,994.50 (incl. 13% VAT)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = EntOrange)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (step < 4) {
                        step++
                    } else {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onDeployed()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = EntOrange)
            ) {
                Text(if (step < 4) "Next" else "Deploy Instance Now", color = Color.White)
            }
        },
        dismissButton = {
            if (step > 1) {
                TextButton(onClick = { step-- }) {
                    Text("Back", color = EntMuted)
                }
            }
        },
        containerColor = EntSurface
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun EnterpriseConsolePreview() {
    EnterpriseConsoleApp()
}

package com.example.myapplication.model

data class VpsInstance(
    val id: String,
    val name: String,
    val code: String,
    val role: String, // API, PRIMARY, CDN, K8S, DEV
    val status: String, // Running, Stopped
    val publicIp: String,
    val privateIp: String,
    val datacenter: String,
    val osKernel: String,
    val cpuUsagePercent: Int,
    val ramUsageGb: Double,
    val ramTotalGb: Double,
    val storageUsageGb: Double,
    val storageTotalGb: Double,
    val region: String // Kathmandu KTM-1, Singapore SG-1
)

data class FirewallRule(
    val port: String,
    val protocol: String,
    val name: String,
    val source: String,
    val status: String // ALLOWED, VPC ONLY
)

data class RestorePoint(
    val id: String,
    val name: String,
    val size: String,
    val timeAgo: String
)

data class LogEntry(
    val timestamp: String,
    val source: String,
    val message: String,
    val level: String // info, success, warning
)

data class StorageVolume(
    val id: String,
    val name: String,
    val sizeGb: Int,
    val type: String,
    val attachedTo: String,
    val iops: Int,
    val status: String
)

data class VpcSubnet(
    val id: String,
    val name: String,
    val cidr: String,
    val region: String,
    val status: String
)

object MockData {
    val instances = mutableListOf(
        VpsInstance(
            id = "vps-849204",
            name = "prod-api-ktm01",
            code = "vps-849204",
            role = "API",
            status = "Running",
            publicIp = "103.112.54.21",
            privateIp = "10.0.1.42",
            datacenter = "Babal KTM-1 (Kathmandu DC)",
            osKernel = "Ubuntu 24.04 LTS (6.8.0-generic)",
            cpuUsagePercent = 42,
            ramUsageGb = 5.2,
            ramTotalGb = 8.0,
            storageUsageGb = 94.2,
            storageTotalGb = 160.0,
            region = "Kathmandu KTM-1"
        ),
        VpsInstance(
            id = "vps-992102",
            name = "db-postgres-cluster",
            code = "vps-992102",
            role = "PRIMARY",
            status = "Running",
            publicIp = "103.112.54.89",
            privateIp = "10.0.1.18",
            datacenter = "Babal KTM-1 (Kathmandu DC)",
            osKernel = "Debian 12 Bookworm (x86_64)",
            cpuUsagePercent = 68,
            ramUsageGb = 14.5,
            ramTotalGb = 16.0,
            storageUsageGb = 210.0,
            storageTotalGb = 320.0,
            region = "Kathmandu KTM-1"
        ),
        VpsInstance(
            id = "vps-302194",
            name = "babal-web-edge",
            code = "vps-302194",
            role = "CDN",
            status = "Running",
            publicIp = "103.112.55.104",
            privateIp = "10.2.0.8",
            datacenter = "Singapore SG-1 (Equinix SG)",
            osKernel = "Ubuntu 22.04 LTS (5.15-generic)",
            cpuUsagePercent = 29,
            ramUsageGb = 2.1,
            ramTotalGb = 4.0,
            storageUsageGb = 45.0,
            storageTotalGb = 80.0,
            region = "Singapore SG-1"
        ),
        VpsInstance(
            id = "vps-482019",
            name = "staging-k8s-worker-01",
            code = "vps-482019",
            role = "K8S",
            status = "Running",
            publicIp = "103.112.54.112",
            privateIp = "10.0.1.75",
            datacenter = "Babal KTM-1 (Kathmandu DC)",
            osKernel = "Rocky Linux 9 (Blue Onyx)",
            cpuUsagePercent = 54,
            ramUsageGb = 11.2,
            ramTotalGb = 16.0,
            storageUsageGb = 180.0,
            storageTotalGb = 320.0,
            region = "Kathmandu KTM-1"
        ),
        VpsInstance(
            id = "vps-110294",
            name = "dev-sandbox-nepal",
            code = "vps-110294",
            role = "DEV",
            status = "Stopped",
            publicIp = "103.112.54.12",
            privateIp = "10.0.1.99",
            datacenter = "Babal KTM-1 (Kathmandu DC)",
            osKernel = "AlmaLinux 9 (x86_64)",
            cpuUsagePercent = 0,
            ramUsageGb = 0.0,
            ramTotalGb = 4.0,
            storageUsageGb = 12.0,
            storageTotalGb = 80.0,
            region = "Kathmandu KTM-1"
        )
    )

    val firewallRules = mutableListOf(
        FirewallRule("22", "TCP", "SSH Remote Management", "Src: 202.70.76.12/32 (HQ Gateway)", "ALLOWED"),
        FirewallRule("80", "TCP", "HTTP (Auto HTTPS Redirect)", "Src: 0.0.0.0/0 (Global Any)", "ALLOWED"),
        FirewallRule("443", "TCP", "HTTPS TLS Gateway", "Src: 0.0.0.0/0 (Global Any)", "ALLOWED"),
        FirewallRule("5432", "TCP", "PostgreSQL Database", "Src: 10.0.1.0/24 (VPC Subnet)", "VPC ONLY")
    )

    val restorePoints = listOf(
        RestorePoint("1", "auto-ktm01-snap-20250519-0200", "Size: 42.1 GB • Consistent NVMe Freeze", "11h ago"),
        RestorePoint("2", "auto-ktm01-snap-20250518-0200", "Size: 41.8 GB • Consistent NVMe Freeze", "1d 11h ago")
    )

    val syslogEntries = listOf(
        LogEntry("14:22:04", "systemd", "Started Babal High Performance API Stack daemon via systemd unit babal-api.service.", "success"),
        LogEntry("14:22:10", "docker", "Container api-service-worker transitioned to status: HEALTHY (port 8080/tcp -> 10.0.1.42:8080).", "success"),
        LogEntry("14:22:15", "kernel", "eth0: NIC physical link up, speed 10000 Mbps, full-duplex, rx_flow_control=off, mtu=9000", "info"),
        LogEntry("14:22:20", "nginx", "Worker processes 4 configured and active. SSL certificate validated (Let's Encrypt Wildcard, expires 84 days).", "success"),
        LogEntry("14:23:45", "[sshd]", "Accepted publickey for root from 202.70.76.12 port 51240 ssh2: ED25519 SHA256:d8kj9+Lqm3", "info"),
        LogEntry("14:24:02", "[cron]", "root) CMD (/opt/babal/telemetry-agent --push --interval=5s > /dev/null 2>&1)", "info"),
        LogEntry("14:24:18", "[monitor]", "KTM-1 internal gateway echo probe: latency=0.22ms, jitter=0.01ms, packet_loss=0.0%", "success")
    )

    val storageVolumes = mutableListOf(
        StorageVolume("vol-01", "vda-root-nvme-prod01", 160, "NVMe Gen4 SAN", "prod-api-ktm01", 24000, "In-Use"),
        StorageVolume("vol-02", "vol-postgres-data-db01", 320, "NVMe Tier-1 ECC", "db-postgres-cluster", 48200, "In-Use"),
        StorageVolume("vol-03", "vol-cdn-cache-edge01", 80, "NVMe High-IOPS", "babal-web-edge", 18000, "In-Use")
    )

    val vpcSubnets = mutableListOf(
        VpcSubnet("subnet-01", "ktm-core-vpc-sub1", "10.0.1.0/24", "Kathmandu KTM-1", "Active"),
        VpcSubnet("subnet-02", "ktm-db-isolated-sub", "10.0.2.0/24", "Kathmandu KTM-1", "Active"),
        VpcSubnet("subnet-03", "sg-edge-transit-sub", "10.2.0.0/16", "Singapore SG-1", "Active")
    )
}

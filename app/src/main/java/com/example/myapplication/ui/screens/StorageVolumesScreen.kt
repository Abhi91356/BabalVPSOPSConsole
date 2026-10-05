package com.example.myapplication.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.model.MockData

@Composable
fun StorageVolumesScreen() {
    val volumes = remember { MockData.storageVolumes }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0B0F19))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("STORAGE & VOLUMES", color = Color(0xFF9CA3AF), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Text("NVMe Gen4 Tier-1 Storage Pools", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            }
            Button(
                onClick = { /* Create Volume */ },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEA580C)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Create Volume", fontSize = 12.sp, color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color(0xFF111827),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, Color(0xFF1F2937))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("VOLUME NAME & ID", color = Color(0xFF6B7280), fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(2f))
                    Text("CAPACITY", color = Color(0xFF6B7280), fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                    Text("STORAGE TYPE", color = Color(0xFF6B7280), fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(2f))
                    Text("ATTACHED INSTANCE", color = Color(0xFF6B7280), fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(2f))
                    Text("IOPS CAPABILITY", color = Color(0xFF6B7280), fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                    Text("STATUS", color = Color(0xFF6B7280), fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                }

                HorizontalDivider(color = Color(0xFF1F2937))

                LazyColumn(
                    modifier = Modifier.height(350.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(volumes) { vol ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF111827), RoundedCornerShape(8.dp))
                                .padding(vertical = 12.dp, horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(modifier = Modifier.weight(2f), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Icon(Icons.Default.Storage, contentDescription = null, tint = Color(0xFF8B5CF6), modifier = Modifier.size(18.dp))
                                Column {
                                    Text(vol.name, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    Text(vol.id, color = Color(0xFF6B7280), fontSize = 10.sp)
                                }
                            }
                            Text("${vol.sizeGb} GB", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                            Text(vol.type, color = Color(0xFF38BDF8), fontSize = 12.sp, modifier = Modifier.weight(2f))
                            Text(vol.attachedTo, color = Color(0xFFE5E7EB), fontSize = 12.sp, modifier = Modifier.weight(2f))
                            Text("${vol.iops} IOPS", color = Color(0xFFA78BFA), fontSize = 12.sp, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
                            Surface(color = Color(0xFF10B981).copy(alpha = 0.2f), shape = RoundedCornerShape(4.dp), modifier = Modifier.weight(1f)) {
                                Text(vol.status, color = Color(0xFF34D399), fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

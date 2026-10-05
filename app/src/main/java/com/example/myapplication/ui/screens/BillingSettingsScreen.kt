package com.example.myapplication.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BillingSettingsScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0B0F19))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column {
            Text("ACCOUNT & GOVERNANCE", color = Color(0xFF9CA3AF), fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Text("Billing, Account Balance & Payment Methods", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        }

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
                        Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(20.dp))
                        Text("Available Balance", color = Color(0xFF9CA3AF), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("NPR 14,800.00", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Auto-recharge enabled via ConnectIPS", color = Color(0xFF6B7280), fontSize = 11.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { /* Add Funds */ },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEA580C)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Top Up Account", fontSize = 12.sp, color = Color.White)
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
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.CreditCard, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(20.dp))
                        Text("Linked Payment Gateways", color = Color(0xFF9CA3AF), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("• ConnectIPS Corporate Gateway (Primary)", color = Color.White, fontSize = 12.sp)
                    Text("• eSewa Merchant Wallet (Instant)", color = Color(0xFF9CA3AF), fontSize = 12.sp)
                    Text("• Khalti Digital Wallet", color = Color(0xFF9CA3AF), fontSize = 12.sp)
                }
            }
        }

        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color(0xFF111827),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, Color(0xFF1F2937))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.Receipt, contentDescription = null, tint = Color(0xFFF59E0B), modifier = Modifier.size(20.dp))
                    Text("Recent Invoices & Audit Logs", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
                HorizontalDivider(color = Color(0xFF1F2937))
                InvoiceRow("INV-2025-0519", "NPR 14,350.00", "Paid (ConnectIPS)", "2025-05-19")
                InvoiceRow("INV-2025-0419", "NPR 13,800.00", "Paid (ConnectIPS)", "2025-04-19")
                InvoiceRow("INV-2025-0319", "NPR 12,400.00", "Paid (ConnectIPS)", "2025-03-19")
            }
        }
    }
}

@Composable
fun InvoiceRow(invoiceNo: String, amount: String, status: String, date: String) {
    Row(
        modifier = Modifier.fillMaxWidth().background(Color(0xFF1F2937), RoundedCornerShape(6.dp)).padding(10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(invoiceNo, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Text(date, color = Color(0xFF9CA3AF), fontSize = 10.sp)
        }
        Text(amount, color = Color(0xFF38BDF8), fontSize = 12.sp, fontWeight = FontWeight.Medium)
        Surface(color = Color(0xFF10B981).copy(alpha = 0.2f), shape = RoundedCornerShape(4.dp)) {
            Text(status, color = Color(0xFF34D399), fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
        }
    }
}

package com.example.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.R
import com.example.data.PaymentStatus
import com.example.data.WorkRecord
import com.example.ui.theme.*

@Composable
fun HomeScreenReal(viewModel: MainViewModel, navController: NavController) {
    val todaysRecords by viewModel.recordsForToday.collectAsState()
    val allPending by viewModel.pendingRecords.collectAsState()

    val todaysEarnings = todaysRecords.sumOf { it.totalAmount }
    val totalPending = allPending.sumOf { it.pendingAmount }
    val totalHoursToday = todaysRecords.sumOf { it.runtimeMinutes } / 60f

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            StatCard(
                title = stringResource(R.string.home_todays_earnings),
                value = "₹$todaysEarnings",
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = stringResource(R.string.home_total_pending),
                value = "₹$totalPending",
                modifier = Modifier.weight(1f),
                isPending = true
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "${todaysRecords.size} records today · ${String.format("%.1f", totalHoursToday)} hours",
            style = MaterialTheme.typography.bodyLarge,
            color = TextMuted
        )
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(
            onClick = { navController.navigate(Screen.NewRecord.route) },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Saffron, contentColor = DarkBackground)
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(stringResource(R.string.home_new_record), fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(8.dp), contentPadding = PaddingValues(bottom = 80.dp)) {
            items(todaysRecords) { record ->
                RecordItemCard(record) {
                    navController.navigate(Screen.RecordDetail.createRoute(record.id))
                }
            }
        }
    }
}

@Composable
fun StatCard(title: String, value: String, isPending: Boolean = false, modifier: Modifier = Modifier) {
    Card(modifier = modifier, colors = CardDefaults.cardColors(containerColor = DarkSurface)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.bodyMedium, color = TextMuted)
            Spacer(modifier = Modifier.height(8.dp))
            Text(value, style = MaterialTheme.typography.titleLarge, color = if (isPending) DangerRed else SuccessGreen, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun RecordItemCard(record: WorkRecord, onClick: () -> Unit) {
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = DarkSurface)) {
        Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
            Column {
                Text(record.customerName, style = MaterialTheme.typography.titleMedium, color = TextPrimary, fontWeight = FontWeight.Bold)
                if (record.village.isNotEmpty()) {
                    Text(record.village, style = MaterialTheme.typography.bodyMedium, color = TextMuted)
                }
            }
            Column(horizontalAlignment = androidx.compose.ui.Alignment.End) {
                Text("₹${record.totalAmount}", style = MaterialTheme.typography.titleMedium, color = TextPrimary, fontWeight = FontWeight.Bold)
                
                val statusColor = when (record.paymentStatus) {
                    PaymentStatus.PAID -> SuccessGreen
                    PaymentStatus.PARTIAL -> WarningAmber
                    PaymentStatus.UNPAID -> DangerRed
                }
                Text(record.paymentStatus.name, style = MaterialTheme.typography.labelMedium, color = statusColor)
            }
        }
    }
}

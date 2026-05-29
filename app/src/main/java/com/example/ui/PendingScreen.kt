package com.example.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.R
import com.example.data.PaymentStatus
import com.example.ui.theme.*

@Composable
fun PendingScreenReal(viewModel: MainViewModel, navController: NavController) {
    val pendingRecords by viewModel.pendingRecords.collectAsState()

    var selectedTab by remember { mutableStateOf(0) }
    val unpaidRecords = pendingRecords.filter { it.paymentStatus == PaymentStatus.UNPAID }
    val partialRecords = pendingRecords.filter { it.paymentStatus == PaymentStatus.PARTIAL }
    
    val totalPending = pendingRecords.sumOf { it.pendingAmount }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        StatCard(
            title = stringResource(R.string.home_total_pending),
            value = "₹$totalPending",
            isPending = true,
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(16.dp))

        TabRow(selectedTabIndex = selectedTab, containerColor = DarkBackground, contentColor = Saffron) {
            Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("${stringResource(R.string.status_unpaid)} (${unpaidRecords.size})") }, selectedContentColor = Saffron, unselectedContentColor = TextMuted)
            Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("${stringResource(R.string.status_partial)} (${partialRecords.size})") }, selectedContentColor = Saffron, unselectedContentColor = TextMuted)
        }

        Spacer(modifier = Modifier.height(16.dp))

        val listToShow = if (selectedTab == 0) unpaidRecords else partialRecords

        LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(8.dp), contentPadding = PaddingValues(bottom = 80.dp)) {
            items(listToShow) { record ->
                Card(modifier = Modifier.fillMaxWidth(), onClick = { navController.navigate(Screen.RecordDetail.createRoute(record.id)) }, colors = CardDefaults.cardColors(containerColor = DarkSurface)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(record.customerName, style = MaterialTheme.typography.titleMedium, color = TextPrimary, fontWeight = FontWeight.Bold)
                            Text("Pending: ₹${record.pendingAmount}", style = MaterialTheme.typography.titleMedium, color = DangerRed, fontWeight = FontWeight.Bold)
                        }
                        Text(record.village, style = MaterialTheme.typography.bodyMedium, color = TextMuted)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { navController.navigate(Screen.RecordDetail.createRoute(record.id)) }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Saffron, contentColor = DarkBackground)) {
                            Text("Collect Payment", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

package com.example.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.data.PaymentStatus
import com.example.data.WorkRecord
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordDetailScreenReal(viewModel: MainViewModel, navController: NavController, recordId: Int) {
    var record by remember { mutableStateOf<WorkRecord?>(null) }
    val scope = rememberCoroutineScope()
    
    var showPartialSheet by remember { mutableStateOf(false) }
    var partialAmountInput by remember { mutableStateOf("") }
    
    LaunchedEffect(recordId) {
        record = viewModel.repository.getRecordById(recordId)
    }

    record?.let { currentRecord ->
        Scaffold { innerPadding ->
            Column(modifier = Modifier.padding(innerPadding).fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                
                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = DarkSurface)) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(currentRecord.customerName, style = MaterialTheme.typography.headlineSmall, color = TextPrimary, fontWeight = FontWeight.Bold)
                        Text(currentRecord.village, style = MaterialTheme.typography.bodyLarge, color = TextMuted)
                        Text(if (currentRecord.phoneNumber.isNotBlank()) currentRecord.phoneNumber else "No Phone", color = TextMuted)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Runtime: ${currentRecord.runtimeMinutes / 60}h ${currentRecord.runtimeMinutes % 60}m", color = TextPrimary)
                        Text("Amount: ₹${currentRecord.totalAmount}", style = MaterialTheme.typography.titleLarge, color = Saffron, fontWeight = FontWeight.Bold)
                    }
                }
                
                val statusColor = when (currentRecord.paymentStatus) {
                    PaymentStatus.PAID -> SuccessGreen
                    PaymentStatus.PARTIAL -> WarningAmber
                    PaymentStatus.UNPAID -> DangerRed
                }

                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = DeepViolet)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Payment Status: ${currentRecord.paymentStatus.name}", style = MaterialTheme.typography.titleMedium, color = statusColor)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Paid: ₹${currentRecord.paidAmount} | Pending: ₹${currentRecord.pendingAmount}", color = TextPrimary)
                    }
                }
                
                if (currentRecord.paymentStatus != PaymentStatus.PAID) {
                    Button(
                        onClick = {
                            viewModel.updateRecord(currentRecord.copy(
                                paymentStatus = PaymentStatus.PAID,
                                paidAmount = currentRecord.totalAmount,
                                pendingAmount = 0
                            ))
                            navController.popBackStack()
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen, contentColor = Color.White)
                    ) {
                        Text("Mark as Paid (Full)", fontWeight = FontWeight.Bold)
                    }
                    
                    Button(
                        onClick = { showPartialSheet = true },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = WarningAmber, contentColor = DarkBackground)
                    ) {
                        Text("Mark Partial Payment", fontWeight = FontWeight.Bold)
                    }
                }
                
                Spacer(modifier = Modifier.weight(1f))
                
                Button(
                    onClick = {
                        viewModel.deleteRecord(currentRecord.id)
                        navController.popBackStack()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = DangerRed, contentColor = Color.White)
                ) {
                    Text("Delete Record")
                }
            }
        }
        
        if (showPartialSheet) {
            ModalBottomSheet(onDismissRequest = { showPartialSheet = false }, containerColor = DarkSurface) {
                Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
                    Text("Enter Partial Amount Collected", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = partialAmountInput,
                        onValueChange = { partialAmountInput = it },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            val newPaid = currentRecord.paidAmount + (partialAmountInput.toIntOrNull() ?: 0)
                            val newPending = maxOf(0, currentRecord.totalAmount - newPaid)
                            val newStatus = if (newPending == 0) PaymentStatus.PAID else PaymentStatus.PARTIAL
                            viewModel.updateRecord(currentRecord.copy(
                                paymentStatus = newStatus,
                                paidAmount = newPaid,
                                pendingAmount = newPending
                            ))
                            showPartialSheet = false
                            navController.popBackStack()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Saffron, contentColor = DarkBackground)
                    ) {
                        Text("Save Partial Payment", fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

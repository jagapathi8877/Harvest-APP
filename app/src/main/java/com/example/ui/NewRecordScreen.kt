package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.R
import com.example.data.*
import com.example.ui.theme.*
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewRecordScreenReal(viewModel: MainViewModel, navController: NavController) {
    var customerName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var village by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    val activeSeason by viewModel.activeSeason.collectAsState()
    val rate = activeSeason?.hourlyRate ?: 2600
    val machines by viewModel.allMachines.collectAsState()
    var selectedMachine by remember { mutableStateOf<Machine?>(null) }
    
    // Auto-select first machine if available
    LaunchedEffect(machines) {
        if (selectedMachine == null && machines.isNotEmpty()) {
            selectedMachine = machines.first()
        }
    }

    var startTimeRaw by remember { mutableStateOf("") }
    var startTimeAm by remember { mutableStateOf(true) }
    
    var endTimeRaw by remember { mutableStateOf("") }
    var endTimeAm by remember { mutableStateOf(true) }
    
    var breakMinutes by remember { mutableStateOf(0) }

    var paymentStatus by remember { mutableStateOf(PaymentStatus.UNPAID) }
    var amountCollected by remember { mutableStateOf("") }

    var showTimePadFor by remember { mutableStateOf<String?>(null) }
    
    fun formatTimeDisplay(raw: String, isAm: Boolean): String {
        if (raw.isEmpty()) return "-- : --"
        var hStr = if (raw.length <= 2) raw else raw.substring(0, raw.length - 2)
        var mStr = if (raw.length <= 2) "00" else raw.substring(raw.length - 2)
        return "$hStr : $mStr ${if (isAm) "AM" else "PM"}"
    }

    fun calculateMillis(raw: String, isAm: Boolean): Long? {
        if (raw.isEmpty() || raw.length < 3) return null // Need at least 3 digits e.g. 100 for 1:00
        val hStr = raw.substring(0, raw.length - 2)
        val mStr = raw.substring(raw.length - 2)
        val h = hStr.toIntOrNull() ?: return null
        val m = mStr.toIntOrNull() ?: return null
        if (h > 12 || m > 59) return null
        
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR, if (h == 12) 0 else h)
        cal.set(Calendar.MINUTE, m)
        cal.set(Calendar.AM_PM, if (isAm) Calendar.AM else Calendar.PM)
        return cal.timeInMillis
    }

    var runtimeMinutes = 0
    var finalAmount = 0
    
    val startMillis = calculateMillis(startTimeRaw, startTimeAm)
    val endMillis = calculateMillis(endTimeRaw, endTimeAm)
    
    if (startMillis != null && endMillis != null) {
        var diff = ((endMillis - startMillis) / 60000).toInt()
        if (diff < 0) diff += 24 * 60 // Next day
        runtimeMinutes = maxOf(0, diff - breakMinutes)
        finalAmount = Math.round((runtimeMinutes / 60f) * rate)
    }

    if (showTimePadFor != null) {
        Dialog(onDismissRequest = { showTimePadFor = null }) {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                modifier = Modifier.padding(16.dp).fillMaxWidth()
            ) {
                var tempRaw by remember { mutableStateOf(if (showTimePadFor == "start") startTimeRaw else endTimeRaw) }
                var tempAm by remember { mutableStateOf(if (showTimePadFor == "start") startTimeAm else endTimeAm) }
                
                Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(if (showTimePadFor == "start") stringResource(R.string.record_start_time) else stringResource(R.string.record_end_time), style = MaterialTheme.typography.titleMedium, color = TextMuted)
                    
                    Text(formatTimeDisplay(tempRaw, tempAm), style = TextStyle(fontSize = 36.sp, fontWeight = FontWeight.Bold, color = Saffron))
                    
                    Row(modifier = Modifier.background(InputBackground, RoundedCornerShape(8.dp)).padding(4.dp)) {
                        Box(modifier = Modifier.clip(RoundedCornerShape(4.dp)).background(if (tempAm) Saffron else Color.Transparent).clickable { tempAm = true }.padding(horizontal = 16.dp, vertical = 8.dp)) { Text("AM", color = if (tempAm) DarkBackground else TextMuted, fontWeight = FontWeight.Bold) }
                        Box(modifier = Modifier.clip(RoundedCornerShape(4.dp)).background(if (!tempAm) Saffron else Color.Transparent).clickable { tempAm = false }.padding(horizontal = 16.dp, vertical = 8.dp)) { Text("PM", color = if (!tempAm) DarkBackground else TextMuted, fontWeight = FontWeight.Bold) }
                    }
                    
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        val rows = listOf(listOf("1","2","3"), listOf("4","5","6"), listOf("7","8","9"), listOf("C","0","OK"))
                        rows.forEach { row ->
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                row.forEach { padBtn ->
                                    Box(
                                        modifier = Modifier.size(64.dp).background(InputBackground, RoundedCornerShape(8.dp))
                                        .clickable {
                                            if (padBtn == "C") tempRaw = ""
                                            else if (padBtn == "OK") {
                                                if (showTimePadFor == "start") { startTimeRaw = tempRaw; startTimeAm = tempAm }
                                                else { endTimeRaw = tempRaw; endTimeAm = tempAm }
                                                showTimePadFor = null
                                            }
                                            else if (tempRaw.length < 4) tempRaw += padBtn
                                        },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(padBtn, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = if (padBtn == "OK") Saffron else TextPrimary)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding).fillMaxSize().verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp).padding(top = 16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = customerName, onValueChange = { customerName = it },
                    label = { Text(stringResource(R.string.record_customer) + " *") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = phone, onValueChange = { phone = it },
                    label = { Text(stringResource(R.string.record_phone)) },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = village, onValueChange = { village = it },
                    label = { Text(stringResource(R.string.record_village)) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            
            Column(modifier = Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(stringResource(R.string.record_machine) + " *", style = MaterialTheme.typography.labelLarge, color = TextMuted)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(machines) { machine ->
                        val isSelected = selectedMachine?.id == machine.id
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) Saffron else DarkSurface)
                                .clickable { selectedMachine = machine }
                                .padding(horizontal = 16.dp, vertical = 10.dp)
                        ) {
                            Text(machine.name, color = if (isSelected) DarkBackground else TextMuted, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Row(modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                TimeCard(
                    title = "Start",
                    display = formatTimeDisplay(startTimeRaw, startTimeAm),
                    isActive = showTimePadFor == "start",
                    modifier = Modifier.weight(1f)
                ) { showTimePadFor = "start" }
                
                TimeCard(
                    title = "End",
                    display = formatTimeDisplay(endTimeRaw, endTimeAm),
                    isActive = showTimePadFor == "end",
                    modifier = Modifier.weight(1f)
                ) { showTimePadFor = "end" }
            }

            Column(modifier = Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = { breakMinutes += 5 }, colors = ButtonDefaults.buttonColors(containerColor = InputBackground, contentColor = TextPrimary)) { Text("+5 min") }
                    Button(onClick = { breakMinutes += 10 }, colors = ButtonDefaults.buttonColors(containerColor = InputBackground, contentColor = TextPrimary)) { Text("+10 min") }
                    Button(onClick = { breakMinutes += 15 }, colors = ButtonDefaults.buttonColors(containerColor = InputBackground, contentColor = TextPrimary)) { Text("+15 min") }
                }
                Text("Break: $breakMinutes min", color = TextMuted)
                if (breakMinutes > 0) {
                    TextButton(onClick = { breakMinutes = 0 }) { Text("Clear", color = Saffron) }
                }
            }

            // Calculation
            Card(modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = DeepViolet)) {
                Row(modifier = Modifier.padding(24.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column {
                        Text("Runtime", color = Color.White.copy(alpha=0.7f), style = MaterialTheme.typography.bodyMedium)
                        Text("${runtimeMinutes / 60}h ${runtimeMinutes % 60}m", color = Color.White, style = MaterialTheme.typography.titleLarge)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("₹$rate / hr", color = Color.White.copy(alpha=0.7f), style = MaterialTheme.typography.bodyMedium)
                        Text("₹$finalAmount", color = Color.White, style = TextStyle(fontSize = 32.sp, fontWeight = FontWeight.Bold))
                    }
                }
            }
            
            Column(modifier = Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(modifier = Modifier.fillMaxWidth().background(InputBackground, RoundedCornerShape(8.dp)).padding(4.dp)) {
                    PaymentStatus.values().forEach { status ->
                        val isSelected = paymentStatus == status
                        var lbl = stringResource(R.string.status_unpaid)
                        var col = DangerRed
                        if (status == PaymentStatus.PAID) { lbl = stringResource(R.string.status_paid); col = SuccessGreen }
                        else if (status == PaymentStatus.PARTIAL) { lbl = stringResource(R.string.status_partial); col = WarningAmber }
                        
                        Box(
                            modifier = Modifier.weight(1f).clip(RoundedCornerShape(4.dp)).background(if (isSelected) col else Color.Transparent).clickable { paymentStatus = status }.padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(lbl, color = if (isSelected) Color.White else TextMuted, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                if (paymentStatus == PaymentStatus.PARTIAL) {
                    OutlinedTextField(
                        value = amountCollected, onValueChange = { amountCollected = it },
                        label = { Text(stringResource(R.string.record_paid_amount)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                OutlinedTextField(
                    value = notes, onValueChange = { notes = it },
                    label = { Text(stringResource(R.string.record_notes)) },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        if (selectedMachine != null && startMillis != null && endMillis != null) {
                            val record = WorkRecord(
                                customerName = customerName,
                                phoneNumber = phone,
                                village = village,
                                dateMillis = System.currentTimeMillis(),
                                machineId = selectedMachine!!.id,
                                startTimeMillis = startMillis,
                                endTimeMillis = endMillis,
                                breakMinutes = breakMinutes,
                                runtimeMinutes = runtimeMinutes,
                                hourlyRate = rate,
                                totalAmount = finalAmount,
                                paymentStatus = paymentStatus,
                                paidAmount = if (paymentStatus == PaymentStatus.PAID) finalAmount else amountCollected.toIntOrNull() ?: 0,
                                pendingAmount = finalAmount - (if (paymentStatus == PaymentStatus.PAID) finalAmount else amountCollected.toIntOrNull() ?: 0),
                                notes = notes
                            )
                            viewModel.saveRecord(record)
                            navController.popBackStack()
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Saffron, contentColor = DarkBackground),
                    enabled = customerName.isNotBlank() && selectedMachine != null && startMillis != null && endMillis != null
                ) {
                    Text(stringResource(R.string.record_save), fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun TimeCard(title: String, display: String, isActive: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Card(
        modifier = modifier.clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = InputBackground),
        shape = RoundedCornerShape(12.dp),
        border = if (isActive) androidx.compose.foundation.BorderStroke(1.dp, Saffron) else null
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(title, style = MaterialTheme.typography.labelMedium, color = TextMuted)
            Text(display, style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextPrimary))
        }
    }
}

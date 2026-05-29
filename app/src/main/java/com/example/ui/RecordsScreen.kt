package com.example.ui

import android.app.DatePickerDialog
import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.R
import com.example.data.Machine
import com.example.data.PaymentStatus
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.Saffron
import com.example.util.PdfExporter
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordsScreenReal(viewModel: MainViewModel, navController: NavController) {
    val allRecords by viewModel.allRecords.collectAsState()
    val machines by viewModel.allMachines.collectAsState()
    
    var searchQuery by remember { mutableStateOf("") }
    var filterVillage by remember { mutableStateOf("") }
    var filterMachine by remember { mutableStateOf<Machine?>(null) }
    var filterStatus by remember { mutableStateOf<String>("All") }
    
    var filterStartDateMillis by remember { mutableStateOf<Long?>(null) }
    var filterEndDateMillis by remember { mutableStateOf<Long?>(null) }

    var isFilterPanelOpen by remember { mutableStateOf(false) }

    fun clearFilters() {
        searchQuery = ""
        filterVillage = ""
        filterMachine = null
        filterStatus = "All"
        filterStartDateMillis = null
        filterEndDateMillis = null
    }

    val filteredRecords = allRecords.filter { record ->
        val matchesSearch = record.customerName.contains(searchQuery, ignoreCase = true) || 
                            record.village.contains(searchQuery, ignoreCase = true)
        val matchesVillage = filterVillage.isEmpty() || record.village.contains(filterVillage, ignoreCase = true)
        val matchesMachine = filterMachine == null || record.machineId == filterMachine?.id
        val matchesStatus = when (filterStatus) {
            "Paid" -> record.paymentStatus == PaymentStatus.PAID
            "Partial" -> record.paymentStatus == PaymentStatus.PARTIAL
            else -> true
        }
        val matchesDate = if (filterStartDateMillis != null && filterEndDateMillis != null) {
            record.dateMillis in filterStartDateMillis!!..filterEndDateMillis!!
        } else if (filterStartDateMillis != null) {
            val cal = Calendar.getInstance().apply { timeInMillis = filterStartDateMillis!! }
            val nextDay = cal.apply { add(Calendar.DAY_OF_YEAR, 1) }.timeInMillis
            record.dateMillis in filterStartDateMillis!!..nextDay
        } else true

        matchesSearch && matchesVillage && matchesMachine && matchesStatus && matchesDate
    }

    val context = LocalContext.current

    val exportLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/pdf")) { uri ->
        if (uri != null) {
            val outputStream: OutputStream? = context.contentResolver.openOutputStream(uri)
            if (outputStream != null) {
                PdfExporter.exportRecordsToPdf(context, outputStream, filteredRecords, filterMachine?.name ?: "All Machines")
            }
        }
    }

    fun pickDate(onDatePicked: (Long) -> Unit) {
        val cal = Calendar.getInstance()
        DatePickerDialog(context, { _, y, m, d ->
            val sel = Calendar.getInstance().apply {
                set(y, m, d, 0, 0, 0)
                set(Calendar.MILLISECOND, 0)
            }
            onDatePicked(sel.timeInMillis)
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text(stringResource(R.string.nav_records), fontWeight = FontWeight.Bold) },
            actions = {
                IconButton(onClick = { isFilterPanelOpen = !isFilterPanelOpen }) {
                    Icon(Icons.Default.List, contentDescription = "Filters", tint = if (isFilterPanelOpen) Saffron else LocalContentColor.current)
                }
                TextButton(onClick = { exportLauncher.launch("KothaKhata_Records.pdf") }) {
                    Text(stringResource(R.string.export_pdf), color = Saffron, fontWeight = FontWeight.Bold)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkSurface)
        )

        AnimatedVisibility(visible = isFilterPanelOpen) {
            Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp), colors = CardDefaults.cardColors(containerColor = DarkSurface)) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Filters", style = MaterialTheme.typography.titleMedium)
                    OutlinedTextField(
                        value = filterVillage, onValueChange = { filterVillage = it },
                        label = { Text("Village") }, modifier = Modifier.fillMaxWidth()
                    )
                    
                    Text("Machine", style = MaterialTheme.typography.labelMedium)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        item {
                            FilterChip(selected = filterMachine == null, onClick = { filterMachine = null }, label = { Text("All") })
                        }
                        items(machines) { m ->
                            FilterChip(selected = filterMachine?.id == m.id, onClick = { filterMachine = m }, label = { Text(m.name) })
                        }
                    }

                    Text("Status", style = MaterialTheme.typography.labelMedium)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("All", "Paid", "Partial").forEach { st ->
                            FilterChip(selected = filterStatus == st, onClick = { filterStatus = st }, label = { Text(st) })
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        OutlinedButton(onClick = { pickDate { filterStartDateMillis = it } }, modifier = Modifier.weight(1f)) {
                            val txt = if (filterStartDateMillis == null) "From Date" else SimpleDateFormat("dd/MM/yy", Locale.getDefault()).format(Date(filterStartDateMillis!!))
                            Text(txt)
                        }
                        OutlinedButton(onClick = { pickDate { filterEndDateMillis = it } }, modifier = Modifier.weight(1f)) {
                            val txt = if (filterEndDateMillis == null) "To Date" else SimpleDateFormat("dd/MM/yy", Locale.getDefault()).format(Date(filterEndDateMillis!!))
                            Text(txt)
                        }
                    }

                    TextButton(onClick = { clearFilters() }, modifier = Modifier.align(Alignment.End)) {
                        Text("Clear All Filters")
                    }
                }
            }
        }

        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                placeholder = { Text("Search by name or village") },
                leadingIcon = { Icon(Icons.Default.Search, null) }
            )
            
            Text("${filteredRecords.size} records found", style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(bottom = 8.dp))

            LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(8.dp), contentPadding = PaddingValues(bottom = 80.dp)) {
                items(filteredRecords) { record ->
                    RecordItemCard(record) {
                        navController.navigate(Screen.RecordDetail.createRoute(record.id))
                    }
                }
            }
        }
    }
}

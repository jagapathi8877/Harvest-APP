package com.example.ui

import android.app.Activity
import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.Machine
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreenReal(viewModel: MainViewModel) {
    val activeSeason by viewModel.activeSeason.collectAsState()
    val machines by viewModel.allMachines.collectAsState()
    
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    var currentLanguage by remember { mutableStateOf(prefs.getString("language", "en") ?: "en") }

    var showRateSheet by remember { mutableStateOf(false) }
    var showMachineSheet by remember { mutableStateOf(false) }
    var editingMachine by remember { mutableStateOf<Machine?>(null) }
    
    var showDeleteConfig by remember { mutableStateOf<Machine?>(null) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(stringResource(R.string.nav_settings), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(24.dp))
        
        LazyColumn(verticalArrangement = Arrangement.spacedBy(24.dp)) {
            item {
                SettingsSection(title = stringResource(R.string.settings_language)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        LanguageButton(
                            text = "English",
                            isSelected = currentLanguage == "en",
                            modifier = Modifier.weight(1f),
                            onClick = {
                                prefs.edit().putString("language", "en").apply()
                                currentLanguage = "en"
                                (context as? Activity)?.recreate()
                            }
                        )
                        LanguageButton(
                            text = "తెలుగు",
                            isSelected = currentLanguage == "te",
                            modifier = Modifier.weight(1f),
                            onClick = {
                                prefs.edit().putString("language", "te").apply()
                                currentLanguage = "te"
                                (context as? Activity)?.recreate()
                            }
                        )
                    }
                }
            }
            item {
                SettingsSection(title = stringResource(R.string.calc_rate)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Current Rate: ₹${activeSeason?.hourlyRate ?: 2600} / hr", style = MaterialTheme.typography.titleMedium)
                        }
                        Button(onClick = { showRateSheet = true }) {
                            Text(stringResource(R.string.calc_change_rate))
                        }
                    }
                }
            }
            item {
                SettingsSection(title = "Machines") {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        machines.forEach { machine ->
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(machine.name, style = MaterialTheme.typography.bodyLarge)
                                Row {
                                    IconButton(onClick = { editingMachine = machine; showMachineSheet = true }) {
                                        Icon(Icons.Default.Edit, contentDescription = "Edit")
                                    }
                                    IconButton(onClick = { showDeleteConfig = machine }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = DangerRed)
                                    }
                                }
                            }
                            HorizontalDivider(color = BorderDivider)
                        }
                        TextButton(
                            onClick = { editingMachine = null; showMachineSheet = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("+ " + stringResource(R.string.settings_add_machine), color = Saffron)
                        }
                    }
                }
            }
        }
    }

    if (showRateSheet) {
        ModalBottomSheet(onDismissRequest = { showRateSheet = false }, containerColor = DarkSurface) {
            var newRate by remember { mutableStateOf(activeSeason?.hourlyRate?.toString() ?: "") }
            Column(modifier = Modifier.padding(16.dp).padding(bottom = 32.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(stringResource(R.string.calc_change_rate), style = MaterialTheme.typography.titleLarge)
                OutlinedTextField(
                    value = newRate,
                    onValueChange = { newRate = it },
                    label = { Text("Rate (₹/hr)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                Button(
                    onClick = {
                        val parsed = newRate.toIntOrNull()
                        if (parsed != null && parsed > 0) {
                            viewModel.updateSeasonRate(parsed)
                            showRateSheet = false
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Save")
                }
            }
        }
    }
    
    if (showMachineSheet) {
        ModalBottomSheet(onDismissRequest = { showMachineSheet = false }, containerColor = DarkSurface) {
            var newName by remember { mutableStateOf(editingMachine?.name ?: "") }
            Column(modifier = Modifier.padding(16.dp).padding(bottom = 32.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(if (editingMachine != null) "Edit Machine" else "Add Machine", style = MaterialTheme.typography.titleLarge)
                OutlinedTextField(
                    value = newName,
                    onValueChange = { newName = it },
                    label = { Text("Machine Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Button(
                    onClick = {
                        if (newName.isNotBlank()) {
                            if (editingMachine != null) {
                                viewModel.updateMachine(editingMachine!!.copy(name = newName))
                            } else {
                                viewModel.addMachine(newName)
                            }
                            showMachineSheet = false
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Save")
                }
            }
        }
    }

    if (showDeleteConfig != null) {
        AlertDialog(
            onDismissRequest = { showDeleteConfig = null },
            title = { Text("Delete Machine?") },
            text = { Text("Are you sure you want to delete ${showDeleteConfig!!.name}?") },
            confirmButton = {
                TextButton(onClick = { viewModel.deleteMachine(showDeleteConfig!!); showDeleteConfig = null }) {
                    Text("Delete", color = DangerRed)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfig = null }) { Text("Cancel") }
            }
        )
    }
}

@Composable
fun SettingsSection(title: String, content: @Composable () -> Unit) {
    Column {
        Text(title, style = MaterialTheme.typography.labelLarge, color = TextMuted, modifier = Modifier.padding(bottom = 8.dp))
        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = DarkSurface)) {
            Column(modifier = Modifier.padding(16.dp)) {
                content()
            }
        }
    }
}

@Composable
fun LanguageButton(text: String, isSelected: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    if (isSelected) {
        Button(onClick = onClick, modifier = modifier, colors = ButtonDefaults.buttonColors(containerColor = Saffron, contentColor = DarkBackground)) {
            Text(text)
        }
    } else {
        OutlinedButton(onClick = onClick, modifier = modifier, border = androidx.compose.foundation.BorderStroke(1.dp, BorderDivider)) {
            Text(text, color = TextPrimary)
        }
    }
}

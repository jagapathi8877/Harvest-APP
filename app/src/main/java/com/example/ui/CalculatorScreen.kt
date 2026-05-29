package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.R
import com.example.ui.theme.*
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorScreenReal(viewModel: MainViewModel, navController: NavController) {
    val activeSeason by viewModel.activeSeason.collectAsState()
    var rate by remember(activeSeason) { mutableStateOf(activeSeason?.hourlyRate?.toString() ?: "2600") }
    
    var rawRuntime by remember { mutableStateOf("") }
    
    fun reset() {
        rate = activeSeason?.hourlyRate?.toString() ?: "2600"
        rawRuntime = ""
    }

    val parsedRate = rate.toFloatOrNull() ?: 0f
    
    var h = 0
    var m = 0
    if (rawRuntime.isNotEmpty() && rawRuntime.length <= 4) {
        val num = rawRuntime.toIntOrNull() ?: 0
        if (rawRuntime.length <= 2) {
            h = num
            m = 0
        } else if (rawRuntime.length == 3) {
            h = rawRuntime.substring(0, 1).toIntOrNull() ?: 0
            m = rawRuntime.substring(1, 3).toIntOrNull() ?: 0
        } else if (rawRuntime.length == 4) {
            h = rawRuntime.substring(0, 2).toIntOrNull() ?: 0
            m = rawRuntime.substring(2, 4).toIntOrNull() ?: 0
        }
    }
    
    var finalAmount = 0
    val runtimeMinutes = (h * 60) + m
    if (runtimeMinutes > 0) {
        finalAmount = Math.round((runtimeMinutes / 60f) * parsedRate)
    }
    
    val format = NumberFormat.getNumberInstance(Locale("en", "IN"))

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding).fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(stringResource(R.string.nav_calculator), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)

            // Rate Field
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(stringResource(R.string.calc_rate) + " (₹/hr)", style = MaterialTheme.typography.labelLarge, color = TextMuted)
                OutlinedTextField(
                    value = rate,
                    onValueChange = { rate = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    textStyle = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Saffron),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = InputBackground, 
                        unfocusedContainerColor = InputBackground,
                        focusedBorderColor = Saffron,
                        unfocusedBorderColor = BorderDivider
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
            
            // Runtime Field
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(stringResource(R.string.calc_runtime), style = MaterialTheme.typography.labelLarge, color = TextMuted)
                val runtimeDisplay = if (rawRuntime.isEmpty()) "H:MM" else {
                    if (rawRuntime.length <= 2) "${rawRuntime}" else if (rawRuntime.length == 3) "${rawRuntime.substring(0,1)}:${rawRuntime.substring(1,3)}" else "${rawRuntime.substring(0,2)}:${rawRuntime.substring(2,4)}"
                }
                
                BasicTextField(
                    value = rawRuntime,
                    onValueChange = { if (it.length <= 4 && it.all { char -> char.isDigit() }) rawRuntime = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    textStyle = TextStyle(fontSize = 48.sp, fontWeight = FontWeight.Bold, color = TextPrimary, textAlign = TextAlign.Center),
                    modifier = Modifier.fillMaxWidth(),
                    decorationBox = { innerTextField ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(InputBackground, RoundedCornerShape(12.dp))
                                .border(1.dp, BorderDivider, RoundedCornerShape(12.dp))
                                .padding(vertical = 24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            if (rawRuntime.isEmpty()) {
                                Text(runtimeDisplay, style = TextStyle(fontSize = 48.sp, fontWeight = FontWeight.Bold, color = TextMuted))
                            } else {
                                Text(runtimeDisplay, style = TextStyle(fontSize = 48.sp, fontWeight = FontWeight.Bold, color = TextPrimary))
                            }
                            // Hidden text field just to capture input
                            Box(modifier = Modifier.matchParentSize().alpha(0f)) {
                                innerTextField()
                            }
                        }
                    }
                )
            }
            
            if (runtimeMinutes > 0 && parsedRate > 0) {
                Card(
                    modifier = Modifier.fillMaxWidth(), 
                    colors = CardDefaults.cardColors(containerColor = DeepViolet)
                ) {
                    Column(modifier = Modifier.fillMaxWidth().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Runtime: $h hr $m min", color = Color.White.copy(alpha=0.7f), style = MaterialTheme.typography.bodyMedium)
                        Text(stringResource(R.string.calc_amount) + ": ₹${format.format(finalAmount)}", color = Color.White, style = TextStyle(fontSize = 32.sp, fontWeight = FontWeight.Bold))
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { reset() },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Saffron, contentColor = DarkBackground)
            ) {
                Text(stringResource(R.string.calc_reset), fontWeight = FontWeight.Bold)
            }
        }
    }
}

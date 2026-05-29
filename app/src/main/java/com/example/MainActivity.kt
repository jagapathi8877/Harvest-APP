package com.example

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.MainViewModel
import com.example.ui.KothaKhataAppContent
import com.example.ui.theme.MyApplicationTheme
import java.util.Locale

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    
    // Apply locale before setting content
    val prefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    val language = prefs.getString("language", "en") ?: "en"
    val locale = Locale(language)
    Locale.setDefault(locale)
    val config = resources.configuration
    config.setLocale(locale)
    resources.updateConfiguration(config, resources.displayMetrics)
    
    enableEdgeToEdge()
    setContent {
      val app = application as KothaKhataApp
      val factory = MainViewModel.Factory(app.repository)
      val viewModel: MainViewModel = viewModel(factory = factory)
      
      MyApplicationTheme {
        KothaKhataAppContent(viewModel)
      }
    }
  }
}

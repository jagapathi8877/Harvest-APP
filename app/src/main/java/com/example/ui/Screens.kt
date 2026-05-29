package com.example.ui

sealed class Screen(val route: String) {
    object Main : Screen("main")
    object NewRecord : Screen("new_record")
    object RecordDetail : Screen("record_detail/{recordId}") {
        fun createRoute(recordId: Int) = "record_detail/$recordId"
    }
}

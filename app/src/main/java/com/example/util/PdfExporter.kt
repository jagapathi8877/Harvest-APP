package com.example.util

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import com.example.data.PaymentStatus
import com.example.data.WorkRecord
import java.io.OutputStream
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PdfExporter {
    fun exportRecordsToPdf(
        context: Context,
        outStream: OutputStream,
        records: List<WorkRecord>,
        machineName: String
    ) {
        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 width, height
        var page = document.startPage(pageInfo)
        var canvas = page.canvas

        val paint = Paint()
        paint.isAntiAlias = true

        var y = 50f
        val margin = 50f

        // Draw header
        paint.color = Color.BLACK
        paint.textSize = 20f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("KothaKhata — Work Records", margin, y, paint)
        y += 25f

        paint.textSize = 12f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        val dateString = dateFormat.format(Date())
        canvas.drawText("Machine: $machineName", margin, y, paint)
        canvas.drawText("Export Date: $dateString", margin + 300, y, paint)
        y += 30f

        // Table Header
        paint.color = Color.DKGRAY
        canvas.drawRect(margin, y - 15f, 595f - margin, y + 10f, paint)
        paint.color = Color.WHITE
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        
        val colX = floatArrayOf(margin + 5, margin + 30, margin + 110, margin + 210, margin + 290, margin + 370, margin + 440)
        canvas.drawText("#", colX[0], y, paint)
        canvas.drawText("Date", colX[1], y, paint)
        canvas.drawText("Customer", colX[2], y, paint)
        canvas.drawText("Village", colX[3], y, paint)
        canvas.drawText("Runtime", colX[4], y, paint)
        canvas.drawText("Amount", colX[5], y, paint)
        canvas.drawText("Status", colX[6], y, paint)
        
        y += 25f

        // Records
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        val numberFormat = NumberFormat.getNumberInstance(Locale("en", "IN"))
        
        var totalRuntimeMinutes = 0
        var totalAmount = 0
        var totalPending = 0

        for ((index, record) in records.withIndex()) {
            if (y > 780f) {
                document.finishPage(page)
                page = document.startPage(pageInfo)
                canvas = page.canvas
                y = 50f
            }

            // Alternating rows
            if (index % 2 == 0) {
                paint.color = Color.parseColor("#F5F5F5")
                canvas.drawRect(margin, y - 15f, 595f - margin, y + 10f, paint)
            }
            paint.color = Color.BLACK

            val dateStr = SimpleDateFormat("dd/MM/yy", Locale.getDefault()).format(Date(record.dateMillis))
            val runtimeStr = "${record.runtimeMinutes / 60}h ${record.runtimeMinutes % 60}m"
            val amtStr = "Rs ${numberFormat.format(record.totalAmount)}"
            
            canvas.drawText((index + 1).toString(), colX[0], y, paint)
            canvas.drawText(dateStr, colX[1], y, paint)
            
            val custStr = if (record.customerName.length > 15) record.customerName.take(12) + "..." else record.customerName
            canvas.drawText(custStr, colX[2], y, paint)
            
            val vilStr = if (record.village.length > 12) record.village.take(10) + "..." else record.village
            canvas.drawText(vilStr, colX[3], y, paint)
            
            canvas.drawText(runtimeStr, colX[4], y, paint)
            canvas.drawText(amtStr, colX[5], y, paint)
            canvas.drawText(record.paymentStatus.name, colX[6], y, paint)
            
            totalRuntimeMinutes += record.runtimeMinutes
            totalAmount += record.totalAmount
            totalPending += record.pendingAmount
            
            y += 20f
        }
        
        y += 20f
        
        if (y > 750f) {
            document.finishPage(page)
            page = document.startPage(pageInfo)
            canvas = page.canvas
            y = 50f
        }

        // Footer Summary
        paint.color = Color.parseColor("#E0E0E0")
        canvas.drawRect(margin, y - 15f, 595f - margin, y + 10f, paint)
        paint.color = Color.BLACK
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        
        canvas.drawText("Total: ${records.size}", colX[0], y, paint)
        canvas.drawText("RT: ${totalRuntimeMinutes/60}h ${totalRuntimeMinutes%60}m", colX[4], y, paint)
        canvas.drawText("Amt: Rs ${numberFormat.format(totalAmount)}", colX[5], y, paint)
        
        y += 20f
        canvas.drawText("Pending: Rs ${numberFormat.format(totalPending)}", colX[5], y, paint)

        document.finishPage(page)
        
        try {
            document.writeTo(outStream)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            document.close()
            outStream.close()
        }
    }
}

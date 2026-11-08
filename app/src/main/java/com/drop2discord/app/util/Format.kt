package com.drop2discord.app.util

import java.util.Locale

object Format {
    fun bytes(size: Long): String {
        if (size < 0) return "Unknown size"
        if (size == 0L) return "0 B"
        val units = arrayOf("B", "KB", "MB", "GB", "TB")
        var value = size.toDouble()
        var unit = 0
        while (value >= 1024 && unit < units.lastIndex) {
            value /= 1024
            unit++
        }
        return if (unit == 0) {
            String.format(Locale.US, "%d %s", size, units[unit])
        } else {
            String.format(Locale.US, "%.1f %s", value, units[unit])
        }
    }
}

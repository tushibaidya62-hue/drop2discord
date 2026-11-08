package com.drop2discord.app.util

import android.content.Context
import android.net.Uri
import android.os.StatFs
import android.provider.OpenableColumns
import java.io.File

data class SelectedFile(val uri: Uri, val name: String, val size: Long)

object FileUtils {

    fun freeBytes(context: Context): Long {
        val stat = StatFs(context.filesDir.absolutePath)
        return stat.availableBytes
    }

    fun queueDir(context: Context): File = File(context.filesDir, "queue")

    fun deleteQueue(context: Context): Boolean {
        val dir = queueDir(context)
        return if (dir.exists()) dir.deleteRecursively() else false
    }

    fun uniqueFile(dir: File, preferredName: String): File {
        val safe = if (preferredName.isBlank()) "file" else preferredName
        var candidate = File(dir, safe)
        if (!candidate.exists()) return candidate
        val dot = safe.lastIndexOf('.')
        val base = if (dot > 0) safe.substring(0, dot) else safe
        val ext = if (dot > 0) safe.substring(dot) else ""
        var n = 1
        while (candidate.exists()) {
            candidate = File(dir, base + " (" + n + ")" + ext)
            n++
        }
        return candidate
    }

    fun displayName(context: Context, uri: Uri): String? {
        val resolver = context.contentResolver
        resolver.query(uri, null, null, null, null)?.use { cursor ->
            val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (index >= 0 && cursor.moveToFirst()) {
                val value = cursor.getString(index)
                if (!value.isNullOrBlank()) return value
            }
        }
        return null
    }

    fun fileSize(context: Context, uri: Uri): Long {
        val resolver = context.contentResolver
        resolver.query(uri, null, null, null, null)?.use { cursor ->
            val index = cursor.getColumnIndex(OpenableColumns.SIZE)
            if (index >= 0 && cursor.moveToFirst()) {
                val value = cursor.getLong(index)
                if (value >= 0) return value
            }
        }
        return -1
    }
}

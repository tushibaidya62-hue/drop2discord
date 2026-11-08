package com.drop2discord.app.batch

import android.content.ClipData
import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import com.drop2discord.app.R
import com.drop2discord.app.util.FileUtils
import com.drop2discord.app.util.SelectedFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileNotFoundException

object ShareHelper {

    private val copyMutex = Mutex()

    suspend fun enoughSpace(context: Context, neededBytes: Long): Boolean = withContext(Dispatchers.IO) {
        neededBytes <= 0 || FileUtils.freeBytes(context) >= neededBytes + 300L * 1024 * 1024
    }

    suspend fun copyToQueue(
        context: Context,
        files: List<SelectedFile>
    ): Result<List<File>> = withContext(Dispatchers.IO) {
        copyMutex.withLock {
            runCatching {
                val batchDir = File(FileUtils.queueDir(context), "batch_0")
                if (batchDir.exists()) batchDir.deleteRecursively()
                batchDir.mkdirs()
                val copied = ArrayList<File>(files.size)
                val resolver = context.contentResolver
                for (item in files) {
                    val name = item.name.ifBlank { "file" }
                    val out = FileUtils.uniqueFile(batchDir, name)
                    val input = resolver.openInputStream(item.uri)
                        ?: throw FileNotFoundException("File missing: " + name)
                    input.use { stream ->
                        out.outputStream().use { output ->
                            val buffer = ByteArray(64 * 1024)
                            while (true) {
                                val read = stream.read(buffer)
                                if (read < 0) break
                                if (read > 0) output.write(buffer, 0, read)
                            }
                        }
                    }
                    copied.add(out)
                }
                copied
            }
        }
    }

    fun commonMimeType(context: Context, files: List<SelectedFile>): String {
        val types = files.mapNotNull { context.contentResolver.getType(it.uri)?.lowercase() }.toSet()
        if (types.size == 1 && types.isNotEmpty()) {
            val only = types.first()
            if (only.contains("/")) return only
        }
        if (types.isNotEmpty() && types.all { it.startsWith("image/") }) return "image/*"
        if (types.isNotEmpty() && types.all { it.startsWith("video/") }) return "video/*"
        return "*/*"
    }

    fun shareFiles(context: Context, copied: List<File>, mimeType: String): Boolean {
        if (copied.isEmpty()) return false
        val authority = context.packageName + ".fileprovider"
        val uris = copied.map { FileProvider.getUriForFile(context, authority, it) }
        val clip = ClipData(mimeType, arrayOf(mimeType), ClipData.Item(uris.first()))
        for (i in 1 until uris.size) clip.addItem(ClipData.Item(uris[i]))
        val intent = if (copied.size == 1) {
            Intent(Intent.ACTION_SEND).apply {
                type = mimeType
                putExtra(Intent.EXTRA_STREAM, uris.first())
                clipData = clip
            }
        } else {
            Intent(Intent.ACTION_SEND_MULTIPLE).apply {
                type = mimeType
                putParcelableArrayListExtra(Intent.EXTRA_STREAM, ArrayList(uris))
                clipData = clip
            }
        }
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.setPackage("com.discord")
        return try {
            context.startActivity(intent)
            true
        } catch (_: Exception) {
            try {
                val chooser = Intent.createChooser(intent.apply { setPackage(null) }, context.getString(R.string.share_chooser_title))
                chooser.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                context.startActivity(chooser)
                true
            } catch (_: Exception) {
                false
            }
        }
    }
}

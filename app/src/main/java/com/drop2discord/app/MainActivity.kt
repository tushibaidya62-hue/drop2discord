package com.drop2discord.app

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.drop2discord.app.batch.ShareHelper
import com.drop2discord.app.ui.Drop2DiscordTheme
import com.drop2discord.app.ui.HomeScreen
import com.drop2discord.app.util.FileUtils
import com.drop2discord.app.util.SelectedFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Drop2DiscordTheme {
                var files by remember { mutableStateOf<List<SelectedFile>>(emptyList()) }
                var isCopying by remember { mutableStateOf(false) }
                var message by remember { mutableStateOf<String?>(null) }
                val scope = rememberCoroutineScope()

                val launcher = rememberLauncherForActivityResult(
                    ActivityResultContracts.OpenMultipleDocuments()
                ) { uris: List<Uri> ->
                    if (uris.isEmpty()) return@rememberLauncherForActivityResult
                    if (uris.size > 10) {
                        message = getString(R.string.too_many)
                        return@rememberLauncherForActivityResult
                    }
                    val picked = ArrayList<SelectedFile>(uris.size)
                    val seen = mutableSetOf<String>()
                    for (uri in uris) {
                        val name = FileUtils.displayName(this, uri) ?: getString(R.string.file_name_unknown)
                        val size = FileUtils.fileSize(this, uri)
                        val key = uri.toString()
                        if (seen.add(key)) picked.add(SelectedFile(uri, name, size))
                    }
                    files = picked
                    message = null
                }

                HomeScreen(
                    files = files,
                    onPickFiles = { message = null; launcher.launch(arrayOf("*/*")) },
                    onShareAll = {
                        if (files.isEmpty()) {
                            message = getString(R.string.pick_first)
                            return@HomeScreen
                        }
                        if (isCopying) return@HomeScreen
                        isCopying = true
                        message = null
                        val toShare = files
                        scope.launch {
                            val totalSize = toShare.sumOf { if (it.size > 0) it.size else 0L }
                            val ok = ShareHelper.enoughSpace(this@MainActivity, totalSize)
                            if (!ok) {
                                isCopying = false
                                message = getString(R.string.low_storage)
                                return@launch
                            }
                            val copyResult = ShareHelper.copyToQueue(this@MainActivity, toShare)
                            val copied = copyResult.getOrNull()
                            if (copied == null || copied.isEmpty()) {
                                isCopying = false
                                message = copyResult.exceptionOrNull()?.message
                                    ?: getString(R.string.file_missing)
                                return@launch
                            }
                            val mimeType = withContext(Dispatchers.Main) {
                                ShareHelper.commonMimeType(this@MainActivity, toShare)
                            }
                            val shared = ShareHelper.shareFiles(this@MainActivity, copied, mimeType)
                            isCopying = false
                            message = if (shared) getString(R.string.copied_and_shared) else getString(R.string.file_missing)
                        }
                    },
                    onDeleteCopies = {
                        val removed = FileUtils.deleteQueue(this)
                        message = if (removed) getString(R.string.copies_deleted) else getString(R.string.no_space_for_delete)
                    },
                    isCopying = isCopying,
                    message = message
                )
            }
        }
    }
}

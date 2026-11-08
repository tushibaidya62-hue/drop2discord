package com.drop2discord.app.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.drop2discord.app.R
import com.drop2discord.app.util.Format
import com.drop2discord.app.util.SelectedFile

@Composable
fun HomeScreen(
    files: List<SelectedFile>,
    onPickFiles: () -> Unit,
    onShareAll: () -> Unit,
    onNext: () -> Unit,
    onDeleteCopies: () -> Unit,
    isCopying: Boolean,
    message: String?,
    queueActive: Boolean,
    queueIndex: Int
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(text = stringResource(R.string.screen_title), style = MaterialTheme.typography.headlineSmall)
        Text(text = stringResource(R.string.screen_subtitle), style = MaterialTheme.typography.bodyMedium)

        Button(onClick = onPickFiles, modifier = Modifier.fillMaxWidth()) {
            Text(text = stringResource(R.string.pick_files))
        }

        if (files.isEmpty()) {
            Text(text = stringResource(R.string.files_count_none), style = MaterialTheme.typography.bodyMedium)
        } else {
            Text(
                text = stringResource(R.string.files_count, files.size),
                style = MaterialTheme.typography.bodyMedium
            )
            HorizontalDivider()
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(files, key = { _, it -> it.uri.toString() }) { index, file ->
                    val status = when {
                        !queueActive && files.isNotEmpty() && queueIndex == 0 -> stringResource(R.string.waiting_file)
                        index < queueIndex || (!queueActive && queueIndex >= files.size) -> stringResource(R.string.done_file)
                        index == queueIndex && queueActive -> stringResource(R.string.current_file)
                        index < queueIndex -> stringResource(R.string.done_file)
                        else -> stringResource(R.string.waiting_file)
                    }
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (queueActive) status + " " + file.name else file.name,
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                text = Format.bytes(file.size),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        if (isCopying) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                CircularProgressIndicator()
                Text(text = stringResource(R.string.copying))
            }
        }

        Button(
            onClick = onShareAll,
            enabled = files.isNotEmpty() && !isCopying && !queueActive,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(R.string.send_one))
        }

        Button(
            onClick = onNext,
            enabled = queueActive && !isCopying,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(R.string.send_next))
        }

        OutlinedButton(onClick = onDeleteCopies, modifier = Modifier.fillMaxWidth()) {
            Text(text = stringResource(R.string.delete_copies))
        }

        if (message != null) {
            Text(text = message, style = MaterialTheme.typography.bodyMedium)
        }

        Spacer(modifier = Modifier.height(4.dp))
    }
}

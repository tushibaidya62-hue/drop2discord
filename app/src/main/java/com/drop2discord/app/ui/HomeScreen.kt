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
import androidx.compose.foundation.lazy.items
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
    onDeleteCopies: () -> Unit,
    isCopying: Boolean,
    message: String?
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
                items(files, key = { it.uri.toString() }) { file ->
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = file.name, style = MaterialTheme.typography.bodyLarge)
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
            enabled = files.isNotEmpty() && !isCopying,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(R.string.share_all))
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

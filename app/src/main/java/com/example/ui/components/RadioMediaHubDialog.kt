package com.example.ui.components

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.util.Log
import android.widget.MediaController
import android.widget.VideoView
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AudioFile
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.VideoFile
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.MediaStream
import com.example.ui.theme.BahrainRed
import com.example.ui.theme.CallAcceptGreen
import com.example.ui.theme.MeetGoogleBlue

@Composable
fun RadioMediaHubDialog(
    mediaStreams: List<MediaStream>,
    onAddMediaStream: (title: String, url: String, type: String) -> Unit,
    onDeleteMediaStream: (Long) -> Unit,
    onExitToDialer: () -> Unit = {},
    onDismiss: () -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var activeStream by remember { mutableStateOf<MediaStream?>(null) }
    var isPlaying by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var playerError by remember { mutableStateOf<String?>(null) }

    // Audio MediaPlayer state
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }

    DisposableEffect(activeStream) {
        if (activeStream != null && activeStream?.mediaType == "AUDIO") {
            try {
                mediaPlayer?.release()
                mediaPlayer = null
                isLoading = true
                playerError = null

                val mp = MediaPlayer().apply {
                    setAudioAttributes(
                        AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .build()
                    )
                    setDataSource(activeStream!!.url)
                    setOnPreparedListener {
                        isLoading = false
                        start()
                        isPlaying = true
                    }
                    setOnErrorListener { _, what, extra ->
                        isLoading = false
                        isPlaying = false
                        playerError = "Playback error ($what, $extra). Verify URL stream connectivity."
                        true
                    }
                    prepareAsync()
                }
                mediaPlayer = mp
            } catch (e: Exception) {
                isLoading = false
                isPlaying = false
                playerError = "Failed to open stream: ${e.message}"
            }
        } else if (activeStream == null) {
            mediaPlayer?.release()
            mediaPlayer = null
            isPlaying = false
            isLoading = false
        }

        onDispose {
            mediaPlayer?.release()
            mediaPlayer = null
        }
    }

    if (showAddDialog) {
        AddMediaStreamDialog(
            onSave = { title, url, type ->
                onAddMediaStream(title, url, type)
                showAddDialog = false
            },
            onDismiss = { showAddDialog = false }
        )
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .clip(RoundedCornerShape(24.dp)),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 12.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(BahrainRed),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Radio, contentDescription = null, tint = Color.White)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                "Bahrain Radio & Saved Media",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                "Live Quran FM, Bahrain FM & Custom Audio/Video Streams",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Button(
                        onClick = { onExitToDialer() },
                        colors = ButtonDefaults.buttonColors(containerColor = BahrainRed),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.testTag("exit_radio_button")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Exit Radio", tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("EXIT RADIO 🚪", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Active Stream Player Panel
                if (activeStream != null) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                        shape = RoundedCornerShape(18.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = if (activeStream?.mediaType == "VIDEO") Icons.Default.Videocam else Icons.Default.GraphicEq,
                                        contentDescription = null,
                                        tint = BahrainRed
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = activeStream?.title ?: "Playing Stream",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.weight(1f, fill = false)
                                    )
                                }

                                IconButton(
                                    onClick = { activeStream = null },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = "Stop Stream", tint = BahrainRed)
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            if (activeStream?.mediaType == "VIDEO") {
                                // Embedded VideoView Player for Video Streams (.m3u8, .mp4, etc.)
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color.Black),
                                    contentAlignment = Alignment.Center
                                ) {
                                    AndroidView(
                                        factory = { ctx ->
                                            VideoView(ctx).apply {
                                                setVideoPath(activeStream?.url)
                                                val mediaController = MediaController(ctx)
                                                mediaController.setAnchorView(this)
                                                setMediaController(mediaController)
                                                setOnPreparedListener { mp ->
                                                    mp.isLooping = true
                                                    start()
                                                }
                                                start()
                                            }
                                        },
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                            } else {
                                // Audio Player Controls
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    if (isLoading) {
                                        CircularProgressIndicator(modifier = Modifier.size(32.dp), strokeWidth = 3.dp)
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text("Connecting to stream...", fontSize = 13.sp)
                                    } else {
                                        Surface(
                                            modifier = Modifier
                                                .size(48.dp)
                                                .clip(CircleShape)
                                                .clickable {
                                                    if (isPlaying) {
                                                        mediaPlayer?.pause()
                                                        isPlaying = false
                                                    } else {
                                                        mediaPlayer?.start()
                                                        isPlaying = true
                                                    }
                                                }
                                                .testTag("toggle_media_play_pause"),
                                            color = BahrainRed
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(
                                                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                                    contentDescription = "Play/Pause",
                                                    tint = Color.White,
                                                    modifier = Modifier.size(28.dp)
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.width(16.dp))

                                        Button(
                                            onClick = { activeStream = null },
                                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                            shape = RoundedCornerShape(12.dp)
                                        ) {
                                            Icon(Icons.Default.Stop, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("Stop")
                                        }
                                    }
                                }
                            }

                            if (playerError != null) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = playerError!!,
                                    color = BahrainRed,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Add Link Button
                Button(
                    onClick = { showAddDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = CallAcceptGreen),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("add_custom_media_button"),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Select / Paste Audio or Video Link", fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    "AVAILABLE STREAMS & STATIONS",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(mediaStreams) { stream ->
                        val isSelected = activeStream?.id == stream.id

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    activeStream = stream
                                }
                                .testTag("stream_item_${stream.id}"),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            ),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(42.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(
                                                if (stream.mediaType == "VIDEO") MeetGoogleBlue.copy(alpha = 0.2f) else BahrainRed.copy(alpha = 0.2f)
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = if (stream.mediaType == "VIDEO") Icons.Default.VideoFile else Icons.Default.AudioFile,
                                            contentDescription = null,
                                            tint = if (stream.mediaType == "VIDEO") MeetGoogleBlue else BahrainRed
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = stream.title,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp,
                                                color = MaterialTheme.colorScheme.onSurface,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            if (stream.isPreset) {
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Surface(
                                                    color = BahrainRed.copy(0.2f),
                                                    shape = RoundedCornerShape(6.dp)
                                                ) {
                                                    Text(
                                                        "BAHRAIN RADIO",
                                                        fontSize = 9.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = BahrainRed,
                                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                    )
                                                }
                                            }
                                        }

                                        Text(
                                            text = stream.url,
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape),
                                        color = if (isSelected && isPlaying) CallAcceptGreen else MaterialTheme.colorScheme.surface
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = if (isSelected && isPlaying) Icons.Default.GraphicEq else Icons.Default.PlayArrow,
                                                contentDescription = "Play Stream",
                                                tint = if (isSelected && isPlaying) Color.White else MaterialTheme.colorScheme.onSurface,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }

                                    if (!stream.isPreset) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        IconButton(
                                            onClick = { onDeleteMediaStream(stream.id) },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Gray, modifier = Modifier.size(18.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { onExitToDialer() },
                    colors = ButtonDefaults.buttonColors(containerColor = BahrainRed),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("bottom_exit_radio_button")
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Exit", tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("EXIT RADIO & BACK TO DIAL PAD 🚪", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.White)
                }
            }
        }
    }
}

@Composable
fun AddMediaStreamDialog(
    onSave: (title: String, url: String, mediaType: String) -> Unit,
    onDismiss: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var url by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf("AUDIO") } // "AUDIO" or "VIDEO"
    var errorText by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Link, contentDescription = null, tint = BahrainRed)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Add Audio or Video Stream", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    "Paste your custom audio or video link (.m3u8, .mp3, .mp4, etc.) to play and save it.",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Stream Title") },
                    placeholder = { Text("e.g. My Bahrain Channel") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("media_title_input"),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = url,
                    onValueChange = { url = it },
                    label = { Text("Audio / Video Stream URL") },
                    placeholder = { Text("https://example.com/live/playlist.m3u8") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("media_url_input"),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text("Media Type:", fontWeight = FontWeight.Bold, fontSize = 13.sp)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clickable { selectedType = "AUDIO" }
                            .padding(end = 16.dp)
                    ) {
                        RadioButton(
                            selected = selectedType == "AUDIO",
                            onClick = { selectedType = "AUDIO" }
                        )
                        Text("Audio 🎵")
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { selectedType = "VIDEO" }
                    ) {
                        RadioButton(
                            selected = selectedType == "VIDEO",
                            onClick = { selectedType = "VIDEO" }
                        )
                        Text("Video 📹")
                    }
                }

                if (errorText != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(errorText!!, color = BahrainRed, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isBlank()) {
                        errorText = "Please enter a stream title"
                        return@Button
                    }
                    if (url.isBlank() || !url.startsWith("http")) {
                        errorText = "Please enter a valid HTTP/HTTPS URL"
                        return@Button
                    }
                    onSave(title.trim(), url.trim(), selectedType)
                },
                colors = ButtonDefaults.buttonColors(containerColor = CallAcceptGreen),
                modifier = Modifier.testTag("save_media_stream_button")
            ) {
                Text("Save & Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

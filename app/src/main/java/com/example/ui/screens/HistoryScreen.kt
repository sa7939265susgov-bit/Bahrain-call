package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CallMade
import androidx.compose.material.icons.filled.CallReceived
import androidx.compose.material.icons.filled.ClearAll
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.SimCard
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CallLogEntry
import com.example.ui.theme.BahrainRed
import com.example.ui.theme.CallAcceptGreen
import com.example.ui.theme.MeetGoogleBlue
import com.example.viewmodel.BahrainMeetViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoryScreen(
    viewModel: BahrainMeetViewModel,
    onRedial: (target: String, isVideo: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val callLogs by viewModel.callLogs.collectAsState()
    var selectedFilter by remember { mutableStateOf("All") }

    val filters = listOf("All", "Incoming", "Outgoing", "Emergency")

    val incomingCount = remember(callLogs) {
        callLogs.count { it.direction.equals("INCOMING", ignoreCase = true) }
    }
    val outgoingCount = remember(callLogs) {
        callLogs.count { !it.direction.equals("INCOMING", ignoreCase = true) }
    }

    val filteredLogs = remember(callLogs, selectedFilter) {
        when (selectedFilter) {
            "Incoming" -> callLogs.filter { it.direction.equals("INCOMING", ignoreCase = true) }
            "Outgoing" -> callLogs.filter { !it.direction.equals("INCOMING", ignoreCase = true) }
            "Emergency" -> callLogs.filter { it.isEmergency }
            else -> callLogs
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("history_screen")
    ) {
        // Title Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.History, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Call History Log", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }

            if (callLogs.isNotEmpty()) {
                IconButton(
                    onClick = { viewModel.clearCallLogs() },
                    modifier = Modifier.testTag("clear_history_button")
                ) {
                    Icon(Icons.Default.ClearAll, contentDescription = "Clear History")
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Stats Summary Badge Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "Total: ${callLogs.size}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
            Surface(
                color = CallAcceptGreen.copy(alpha = 0.15f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "↙ Incoming: $incomingCount",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = CallAcceptGreen,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
            Surface(
                color = MeetGoogleBlue.copy(alpha = 0.15f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "↗ Outgoing: $outgoingCount",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MeetGoogleBlue,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Filter Chips Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            filters.forEach { filter ->
                FilterChip(
                    selected = selectedFilter == filter,
                    onClick = { selectedFilter = filter },
                    label = { Text(filter, fontSize = 12.sp, fontWeight = FontWeight.Medium) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    modifier = Modifier.testTag("history_filter_${filter.lowercase()}")
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (filteredLogs.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.4f)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = if (callLogs.isEmpty()) "No Call History Yet" else "No $selectedFilter Calls",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Your recent incoming and outgoing calls with timestamps will appear here.",
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredLogs, key = { it.id }) { log ->
                    CallLogCard(
                        log = log,
                        onRedial = { onRedial(log.numberOrEmail, log.callType == "VIDEO") }
                    )
                }
            }
        }
    }
}

@Composable
fun CallLogCard(
    log: CallLogEntry,
    onRedial: () -> Unit
) {
    val isVideo = log.callType == "VIDEO"
    val isIncoming = log.direction.equals("INCOMING", ignoreCase = true)

    val formattedDate = remember(log.timestamp) {
        SimpleDateFormat("MMM dd, h:mm a", Locale.ENGLISH).format(Date(log.timestamp))
    }
    val formattedDuration = remember(log.durationSeconds) {
        if (log.durationSeconds >= 60) {
            val mins = log.durationSeconds / 60
            val secs = log.durationSeconds % 60
            "${mins}m ${secs}s"
        } else {
            "${log.durationSeconds}s"
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("call_log_card_${log.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(
                        when {
                            log.isEmergency -> BahrainRed.copy(0.2f)
                            isIncoming -> CallAcceptGreen.copy(0.18f)
                            else -> MeetGoogleBlue.copy(0.15f)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when {
                        log.isEmergency -> Icons.Default.Warning
                        isVideo -> Icons.Default.Videocam
                        isIncoming -> Icons.Default.CallReceived
                        else -> Icons.Default.CallMade
                    },
                    contentDescription = null,
                    tint = when {
                        log.isEmergency -> BahrainRed
                        isIncoming -> CallAcceptGreen
                        else -> MeetGoogleBlue
                    }
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = log.contactName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (log.numberOrEmail.isNotEmpty() && log.numberOrEmail != log.contactName) {
                    Text(
                        text = log.numberOrEmail,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Direction Badge
                    Surface(
                        color = when {
                            log.isEmergency -> BahrainRed.copy(0.15f)
                            isIncoming -> CallAcceptGreen.copy(0.15f)
                            else -> MeetGoogleBlue.copy(0.15f)
                        },
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = when {
                                log.isEmergency -> "🚨 Emergency"
                                isIncoming -> "↙ Incoming"
                                else -> "↗ Outgoing"
                            },
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = when {
                                log.isEmergency -> BahrainRed
                                isIncoming -> CallAcceptGreen
                                else -> MeetGoogleBlue
                            },
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Carrier Badge
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.SimCard, contentDescription = null, modifier = Modifier.size(10.dp))
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(log.carrierUsed, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Timestamp and Duration
                    Text(
                        text = "$formattedDate • $formattedDuration",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            IconButton(
                onClick = onRedial,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(CallAcceptGreen.copy(0.15f))
                    .testTag("redial_button_${log.id}")
            ) {
                Icon(
                    imageVector = if (isVideo) Icons.Default.Videocam else Icons.Default.Call,
                    contentDescription = "Redial",
                    tint = CallAcceptGreen
                )
            }
        }
    }
}


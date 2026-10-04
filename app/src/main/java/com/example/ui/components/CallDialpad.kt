package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CallMade
import androidx.compose.material.icons.filled.CallReceived
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CallAcceptGreen
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import com.example.data.CallLogEntry
import com.example.ui.theme.BahrainRed
import com.example.ui.theme.MeetGoogleBlue
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CallDialpad(
    dialInput: String,
    onInputChange: (String) -> Unit,
    onDigitClick: (String) -> Unit,
    onBackspaceClick: () -> Unit,
    onStartCall: (isVideo: Boolean) -> Unit,
    modifier: Modifier = Modifier,
    onClearClick: () -> Unit = {},
    recentLogs: List<CallLogEntry> = emptyList(),
    onSelectHistoryTab: () -> Unit = {}
) {
    var showConfirmDialog by remember { mutableStateOf(false) }
    var pendingIsVideo by remember { mutableStateOf(false) }
    var selectedTargetName by remember { mutableStateOf("") }

    // Preset contacts list for letter & digit search
    val presetContacts = remember {
        listOf(
            "King Fahd Causeway" to "17796000",
            "999 Emergency" to "999",
            "199 Traffic Police" to "199",
            "299 Civil Defense" to "299",
            "Bahrain Airport BIA" to "33678882",
            "Manama Harbour" to "17214433",
            "Seef City Centre" to "17177771",
            "Riffa Jasmi's" to "17770077",
            "KFC Bahrain" to "17111111",
            "Talabat Rider Ahmed" to "39123456",
            "140 English Weather" to "140",
            "141 Arabic Weather" to "141",
            "181 Directory" to "181"
        )
    }

    val matchedContacts = remember(dialInput) {
        if (dialInput.isBlank()) emptyList()
        else presetContacts.filter { (name, num) ->
            name.contains(dialInput, ignoreCase = true) || num.contains(dialInput, ignoreCase = true)
        }
    }

    // Integrate with existing call history storage: show matching logs or recent quick dials
    val historySuggestions = remember(dialInput, recentLogs) {
        if (dialInput.isBlank()) {
            recentLogs.distinctBy { it.numberOrEmail }.take(6)
        } else {
            recentLogs.filter { log ->
                log.numberOrEmail.contains(dialInput, ignoreCase = true) ||
                    log.contactName.contains(dialInput, ignoreCase = true)
            }.distinctBy { it.numberOrEmail }.take(6)
        }
    }

    val lastOutgoingLog = remember(recentLogs) {
        recentLogs.firstOrNull { !it.direction.equals("INCOMING", ignoreCase = true) }
    }

    // Call Confirmation Dialog (Yes / No)
    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = {
                Text(
                    text = "📞 Call Confirmation",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Column {
                    Text(
                        text = "Do you want to automatically call this contact?",
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = selectedTargetName.ifBlank { dialInput },
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            if (dialInput.isNotBlank() && selectedTargetName.isNotBlank()) {
                                Text(
                                    text = "Number: $dialInput",
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(0.8f)
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                val confirmIcon = if (pendingIsVideo) Icons.Default.Videocam else Icons.Default.Call
                val confirmColor = if (pendingIsVideo) MeetGoogleBlue else CallAcceptGreen
                PulsingCallButton(
                    onClick = {
                        showConfirmDialog = false
                        onStartCall(pendingIsVideo)
                    },
                    enabled = true,
                    text = "YES (CALL NOW)",
                    icon = confirmIcon,
                    containerColor = confirmColor,
                    testTag = "confirm_call_yes_button"
                )
            },
            dismissButton = {
                Button(
                    onClick = { showConfirmDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("confirm_call_no_button")
                ) {
                    Text("NO (CANCEL)", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Dial Input TextField (Supports standard phone keyboard, letters, and numbers)
        OutlinedTextField(
            value = dialInput,
            onValueChange = {
                onInputChange(it)
                selectedTargetName = ""
            },
            placeholder = { Text("Write name or number (e.g. King Fahd, 999)...", fontSize = 14.sp, color = Color.Gray) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("dial_input_field"),
            trailingIcon = {
                if (dialInput.isNotEmpty()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = onClearClick,
                            modifier = Modifier.testTag("dial_clear_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear All",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        IconButton(
                            onClick = onBackspaceClick,
                            modifier = Modifier.testTag("dial_backspace_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Backspace,
                                contentDescription = "Delete",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = Color.Transparent
            ),
            shape = RoundedCornerShape(12.dp)
        )

        // Call History Integration Section (Recent or Matching Call History)
        if (historySuggestions.isNotEmpty()) {
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (dialInput.isBlank()) "Recent Call History" else "Matching Call History",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (recentLogs.isNotEmpty()) {
                    TextButton(
                        onClick = onSelectHistoryTab,
                        modifier = Modifier.testTag("history_shortcut_button")
                    ) {
                        Text("All History (${recentLogs.size}) →", fontSize = 11.sp)
                    }
                }
            }
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(historySuggestions, key = { "history_${it.id}" }) { log ->
                    val isIncoming = log.direction.equals("INCOMING", ignoreCase = true)
                    val timeStr = remember(log.timestamp) {
                        SimpleDateFormat("MMM dd, h:mm a", Locale.ENGLISH).format(Date(log.timestamp))
                    }
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .clickable {
                                onInputChange(log.numberOrEmail)
                                selectedTargetName = log.contactName
                                pendingIsVideo = log.callType == "VIDEO"
                                showConfirmDialog = true
                            },
                        color = when {
                            log.isEmergency -> BahrainRed.copy(alpha = 0.15f)
                            isIncoming -> CallAcceptGreen.copy(alpha = 0.15f)
                            else -> MeetGoogleBlue.copy(alpha = 0.15f)
                        }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = when {
                                    log.isEmergency -> Icons.Default.Warning
                                    isIncoming -> Icons.Default.CallReceived
                                    else -> Icons.Default.CallMade
                                },
                                contentDescription = null,
                                tint = when {
                                    log.isEmergency -> BahrainRed
                                    isIncoming -> CallAcceptGreen
                                    else -> MeetGoogleBlue
                                },
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Column {
                                Text(
                                    text = log.contactName.ifEmpty { log.numberOrEmail },
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "${log.numberOrEmail} • $timeStr",
                                    fontSize = 9.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        // Letter Matching Contact Chips
        if (matchedContacts.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(matchedContacts) { (name, num) ->
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .clickable {
                                onInputChange(num)
                                selectedTargetName = name
                                pendingIsVideo = false
                                showConfirmDialog = true
                            },
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Call, contentDescription = null, tint = CallAcceptGreen, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "$name ($num)",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Grid of Keys: 1-9, *, 0, #
        val keys = listOf(
            listOf("1" to "", "2" to "ABC", "3" to "DEF"),
            listOf("4" to "GHI", "5" to "JKL", "6" to "MNO"),
            listOf("7" to "PQRS", "8" to "TUV", "9" to "WXYZ"),
            listOf("*" to "", "0" to "+", "#" to "")
        )

        keys.forEach { row ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                row.forEach { (digit, sub) ->
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .testTag("dial_key_$digit")
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                            .clickable { onDigitClick(digit) },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = digit,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            if (sub.isNotEmpty()) {
                                Text(
                                    text = sub,
                                    fontSize = 9.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Call Action Buttons (Video Call & Audio Call)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // Audio Call Button
            PulsingCallButton(
                onClick = {
                    if (dialInput.isBlank() && lastOutgoingLog != null) {
                        onInputChange(lastOutgoingLog.numberOrEmail)
                        selectedTargetName = lastOutgoingLog.contactName
                    }
                    pendingIsVideo = false
                    showConfirmDialog = true
                },
                enabled = dialInput.isNotBlank() || lastOutgoingLog != null,
                text = if (dialInput.isBlank() && lastOutgoingLog != null) "Redial Last" else "Audio Call",
                icon = Icons.Default.Call,
                containerColor = CallAcceptGreen,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 6.dp),
                testTag = "audio_call_button"
            )

            // Video Call Button (Google Meet Style)
            PulsingCallButton(
                onClick = {
                    if (dialInput.isBlank() && lastOutgoingLog != null) {
                        onInputChange(lastOutgoingLog.numberOrEmail)
                        selectedTargetName = lastOutgoingLog.contactName
                    }
                    pendingIsVideo = true
                    showConfirmDialog = true
                },
                enabled = dialInput.isNotBlank() || lastOutgoingLog != null,
                text = if (dialInput.isBlank() && lastOutgoingLog != null) "Redial Video" else "Video Call",
                icon = Icons.Default.Videocam,
                containerColor = MeetGoogleBlue,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 6.dp),
                testTag = "video_call_button"
            )
        }
    }
}

@Composable
fun PulsingCallButton(
    onClick: () -> Unit,
    enabled: Boolean,
    text: String,
    icon: ImageVector,
    containerColor: Color,
    modifier: Modifier = Modifier,
    testTag: String = ""
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val infiniteTransition = rememberInfiniteTransition(label = "callButtonPulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = if (enabled) 1.035f else 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1100, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    val rippleScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = if (enabled) 1.15f else 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1600, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rippleScale"
    )
    val rippleAlpha by infiniteTransition.animateFloat(
        initialValue = if (enabled) 0.38f else 0f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1600, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rippleAlpha"
    )

    val pressScale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1.0f,
        animationSpec = tween(durationMillis = 100),
        label = "pressScale"
    )

    val combinedScale = pulseScale * pressScale

    Box(
        modifier = modifier
            .height(52.dp)
            .scale(combinedScale),
        contentAlignment = Alignment.Center
    ) {
        if (enabled) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .scale(rippleScale)
                    .clip(RoundedCornerShape(14.dp))
                    .border(
                        width = 2.dp,
                        color = containerColor.copy(alpha = rippleAlpha),
                        shape = RoundedCornerShape(14.dp)
                    )
            )
        }

        Button(
            onClick = onClick,
            enabled = enabled,
            interactionSource = interactionSource,
            modifier = Modifier
                .fillMaxSize()
                .testTag(testTag),
            colors = ButtonDefaults.buttonColors(
                containerColor = containerColor,
                contentColor = Color.White,
                disabledContainerColor = containerColor.copy(alpha = 0.35f),
                disabledContentColor = Color.White.copy(alpha = 0.6f)
            ),
            shape = RoundedCornerShape(14.dp)
        ) {
            Icon(imageVector = icon, contentDescription = text)
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = text, fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }
    }
}


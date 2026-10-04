package com.example.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.CellTower
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SimCard
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VideocamOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Dialpad
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BahrainRed
import com.example.ui.theme.CallAcceptGreen
import com.example.ui.theme.CallEndRed
import com.example.ui.theme.MeetDarkBackground
import com.example.ui.theme.MeetGoogleBlue
import com.example.ui.theme.MeetSurfaceDark
import com.example.viewmodel.ActiveCall
import com.example.viewmodel.BahrainMeetViewModel
import kotlinx.coroutines.delay

@Composable
fun InCallScreen(
    call: ActiveCall,
    viewModel: BahrainMeetViewModel,
    onEndCall: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isMuted by remember { mutableStateOf(false) }
    var isVideoOff by remember { mutableStateOf(!call.isVideo) }
    var isSpeakerOn by remember { mutableStateOf(true) }
    var callDuration by remember { mutableIntStateOf(0) }
    var isChatActive by remember { mutableStateOf(false) }
    var chatInputText by remember { mutableStateOf("") }
    var showAddCallDialog by remember { mutableStateOf(false) }
    var addNumberInput by remember { mutableStateOf("") }

    LaunchedEffect(call.isConnected) {
        if (call.isConnected) {
            while (true) {
                delay(1000)
                callDuration++
            }
        }
    }

    // Emergency Flashing Beacon Effect
    val infiniteTransition = rememberInfiniteTransition(label = "beacon")
    val beaconAlpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(600),
            repeatMode = RepeatMode.Reverse
        ),
        label = "beaconAlpha"
    )

    val bgColor = when {
        call.isEmergency -> Color(0xFF3B0000)
        call.isZombie -> Color(0xFF0F2613)
        else -> MeetDarkBackground
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(bgColor)
            .padding(16.dp)
    ) {
        // Emergency Flashing Banner
        if (call.isEmergency) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(BahrainRed.copy(alpha = beaconAlpha))
                    .align(Alignment.TopCenter),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Warning, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "BAHRAIN EMERGENCY HOTLINE CONNECTED",
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        fontSize = 13.sp
                    )
                }
            }
        }

        // Top Header info
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = if (call.isEmergency) 60.dp else 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                color = MeetSurfaceDark,
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.SimCard,
                        contentDescription = null,
                        tint = MeetGoogleBlue,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Via ${call.carrier} SIM",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    AnimatedSignalBars(modifier = Modifier.testTag("in_call_signal_bars"))
                    Spacer(modifier = Modifier.width(10.dp))
                    if (!call.isConnected && call.isRinging) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF422010)
                        ) {
                            Text(
                                text = "🇧🇭 Ringing (${call.ringRepetition}/${call.totalRings})",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFFD54F),
                                modifier = Modifier
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                    .testTag("in_call_ringing_indicator")
                            )
                        }
                    } else {
                        Text(
                            text = "%02d:%02d".format(callDuration / 60, callDuration % 60),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Green,
                            modifier = Modifier.testTag("in_call_duration_timer")
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = if (call.isConference) "👥 Conference Call (${call.conferenceNumbers.size + 1} Talking)" else call.contactName,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Text(
                text = if (call.isConference) "Primary: ${call.contactName} (${call.numberOrEmail})" else call.numberOrEmail,
                fontSize = 14.sp,
                color = Color.LightGray,
                textAlign = TextAlign.Center
            )

            if (call.conferenceNumbers.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                androidx.compose.foundation.lazy.LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.padding(horizontal = 8.dp)
                ) {
                    items(call.conferenceNumbers) { participant ->
                        Surface(
                            color = CallAcceptGreen.copy(alpha = 0.3f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Call,
                                    contentDescription = null,
                                    tint = CallAcceptGreen,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = participant,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }

        // Center Content: Video Avatar, Live Speech, GCC Roaming Prompt, & In-Call Keypad
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Check for GCC Roaming Prompt First
            if (call.isGccPrompt) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp)),
                    color = MeetSurfaceDark
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CellTower, contentDescription = null, tint = BahrainRed)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "GCC ROAMING CALL RESTRICTION",
                                fontWeight = FontWeight.Bold,
                                color = BahrainRed,
                                fontSize = 15.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "GCC Roaming is currently ACTIVE in ${call.gccCountry}.\nDo you want to shut off roaming and switch your SIM back to local network?",
                            color = Color.White,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center,
                            lineHeight = 20.sp
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = { viewModel.confirmGccSwitchLocal() },
                                colors = ButtonDefaults.buttonColors(containerColor = CallAcceptGreen),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .testTag("gcc_incall_yes_button"),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("YES (1): Switch Local", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }

                            OutlinedButton(
                                onClick = { viewModel.continueGccRoamingCall() },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .testTag("gcc_incall_no_button"),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("NO (2): Roam Call", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
                            }
                        }
                    }
                }
            } else {
                // Video Preview (WhatsApp / Doorbell Camera) or Avatar Circle
                if (!isVideoOff || call.isVideo) {
                    // Full WhatsApp & Doorbell Live Camera Feed Display
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(210.dp)
                            .clip(RoundedCornerShape(20.dp)),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF020617))
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            // Video Background Canvas with Camera Grid & Stream Effect
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color(0xFF09101D))
                                    .padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Videocam,
                                    contentDescription = "Camera Feed Active",
                                    tint = MeetGoogleBlue,
                                    modifier = Modifier.size(54.dp)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = if (call.contactName.contains("Doorbell", ignoreCase = true) || call.numberOrEmail.contains("doorbell", ignoreCase = true))
                                        "📹 VILLA SMART DOORBELL CAM #1 (4K ULTRA HD)"
                                    else "📹 WHATSAPP HD VIDEO CALL CAM - ${call.contactName.uppercase()}",
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text = "1080p • 60 FPS • Encrypted Stream Active",
                                    color = CallAcceptGreen,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            // Top Left Status Badge
                            Surface(
                                color = BahrainRed,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .padding(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(Color.White)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("LIVE CAM", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 9.sp)
                                }
                            }

                            // Top Right Picture-in-Picture (PIP) Selfie Camera Window (WhatsApp Style)
                            Surface(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(10.dp)
                                    .size(width = 75.dp, height = 95.dp)
                                    .clip(RoundedCornerShape(12.dp)),
                                color = Color(0xFF1E293B)
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(
                                            imageVector = Icons.Default.Person,
                                            contentDescription = "Front Selfie Cam",
                                            tint = MeetGoogleBlue,
                                            modifier = Modifier.size(24.dp)
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text("YOU", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            // Bottom Bar Camera Quick Controls (Doorbell Unlatch / Camera Flip)
                            Row(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .fillMaxWidth()
                                    .background(Color.Black.copy(0.6f))
                                    .padding(horizontal = 12.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "📷 Front Cam Active",
                                    color = Color.LightGray,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold
                                )

                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Surface(
                                        color = MeetGoogleBlue.copy(0.3f),
                                        shape = RoundedCornerShape(6.dp),
                                        modifier = Modifier.clickable { }
                                    ) {
                                        Text("🔄 Flip", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                                    }

                                    Surface(
                                        color = CallAcceptGreen.copy(0.3f),
                                        shape = RoundedCornerShape(6.dp),
                                        modifier = Modifier.clickable { }
                                    ) {
                                        Text("🔓 Unlock Doorbell", color = CallAcceptGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                                    }
                                }
                            }
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .size(130.dp)
                            .clip(CircleShape)
                            .background(
                                if (call.isZombie) Color(0xFF1E421E)
                                else if (call.isEmergency) BahrainRed
                                else MeetSurfaceDark
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = call.contactName.take(1).uppercase(),
                            fontSize = 48.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Subtitles / Live Voice Speech Output Box
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp)),
                    color = if (call.carrierError != null) BahrainRed.copy(0.2f) else MeetSurfaceDark.copy(alpha = 0.9f)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = when {
                                call.carrierError != null -> "CARRIER MISMATCH WARNING"
                                !call.isConnected && call.isRinging -> "🇧🇭 OUTGOING CALL • RINGING (${call.ringRepetition}/${call.totalRings})"
                                else -> "LIVE HOTLINE VOICE RESPONSE"
                            },
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = when {
                                call.carrierError != null -> BahrainRed
                                !call.isConnected && call.isRinging -> Color(0xFFFFD54F)
                                else -> MeetGoogleBlue
                            }
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (!call.isConnected && call.isRinging) {
                                "Calling ${call.contactName} via ${call.carrier}...\nPlaying Bahraini Ringtone (Repeat ${call.ringRepetition} of ${call.totalRings}). Connecting shortly..."
                            } else {
                                call.responseSpeech
                            },
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White,
                            textAlign = TextAlign.Center,
                            lineHeight = 20.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Mode Selector Bar (Keypad DTMF vs Live Text Chat)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MeetSurfaceDark)
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { isChatActive = false }
                            .testTag("tab_ivr_keypad"),
                        color = if (!isChatActive) MeetGoogleBlue else Color.Transparent
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Dialpad, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("IVR Keypad", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }

                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { isChatActive = true }
                            .testTag("tab_live_chat"),
                        color = if (isChatActive) MeetGoogleBlue else Color.Transparent
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Chat, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Live Text Chat", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                if (!isChatActive) {
                    // Interactive IVR Keypad Controls
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Interactive Voice Response Keypad (DTMF):",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.LightGray
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        // Primary Language Quick Action Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable { viewModel.pressIvrKey("1") }
                                    .testTag("ivr_key_1_english"),
                                color = MeetGoogleBlue.copy(alpha = 0.3f)
                            ) {
                                Text(
                                    text = "1: English 🇬🇧",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }

                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable { viewModel.pressIvrKey("2") }
                                    .testTag("ivr_key_2_arabic"),
                                color = CallAcceptGreen.copy(alpha = 0.3f)
                            ) {
                                Text(
                                    text = "2: العربية 🇧🇭",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        val keypadRows = listOf(
                            listOf("1", "2", "3"),
                            listOf("4", "5", "6"),
                            listOf("7", "8", "9"),
                            listOf("*", "0", "#")
                        )

                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            keypadRows.forEach { rowKeys ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    rowKeys.forEach { key ->
                                        Surface(
                                            modifier = Modifier
                                                .weight(1f)
                                                .clip(RoundedCornerShape(8.dp))
                                                .clickable { viewModel.pressIvrKey(key) }
                                                .testTag("dtmf_key_$key"),
                                            color = MeetSurfaceDark
                                        ) {
                                            Text(
                                                text = key,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White,
                                                textAlign = TextAlign.Center,
                                                fontSize = 15.sp,
                                                modifier = Modifier.padding(vertical = 6.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // Live Text Chat Panel
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(210.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MeetSurfaceDark)
                            .padding(8.dp)
                    ) {
                        Text(
                            text = "In-Call Realtime Chat & Location Reply:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MeetGoogleBlue
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        // Quick Location Reply Chips
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            val locations = listOf("📍 Manama", "📍 Muharraq", "📍 Riffa", "📍 Seef")
                            locations.forEach { loc ->
                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(6.dp))
                                        .clickable {
                                            val locText = "My location is ${loc.replace("📍 ", "")}"
                                            viewModel.sendInCallTextMessage(locText)
                                        },
                                    color = Color.White.copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = loc,
                                        fontSize = 9.sp,
                                        color = Color.White,
                                        textAlign = TextAlign.Center,
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier.padding(vertical = 4.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // Messages History Box
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .background(Color.Black.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                .padding(6.dp)
                        ) {
                            if (call.chatMessages.isEmpty()) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        "Type a text below to chat with ${call.contactName} live during the call.",
                                        fontSize = 11.sp,
                                        color = Color.Gray,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            } else {
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    call.chatMessages.takeLast(4).forEach { msg ->
                                        val isUser = msg.sender == "YOU"
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
                                        ) {
                                            Surface(
                                                color = if (isUser) MeetGoogleBlue else CallAcceptGreen.copy(alpha = 0.8f),
                                                shape = RoundedCornerShape(8.dp)
                                            ) {
                                                Text(
                                                    text = "${msg.sender}: ${msg.text}",
                                                    fontSize = 11.sp,
                                                    color = Color.White,
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // Message Input Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = chatInputText,
                                onValueChange = { chatInputText = it },
                                placeholder = { Text("Text or send location...", fontSize = 11.sp, color = Color.Gray) },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(46.dp)
                                    .testTag("in_call_chat_input"),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MeetGoogleBlue,
                                    unfocusedBorderColor = Color.Gray,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                )
                            )

                            Spacer(modifier = Modifier.width(6.dp))

                            IconButton(
                                onClick = {
                                    if (chatInputText.isNotBlank()) {
                                        viewModel.sendInCallTextMessage(chatInputText)
                                        chatInputText = ""
                                    }
                                },
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(MeetGoogleBlue)
                                    .testTag("in_call_send_button")
                            ) {
                                Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.White, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }
        }

        // Bottom Call Controls Bar (Mute, Video, Speaker, Hangup)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Add Call Plus Button
            IconButton(
                onClick = { showAddCallDialog = true },
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(MeetGoogleBlue)
                    .testTag("add_call_plus_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Number to Call",
                    tint = Color.White
                )
            }

            // Mute Button
            IconButton(
                onClick = { isMuted = !isMuted },
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(if (isMuted) Color.White else MeetSurfaceDark)
                    .testTag("call_mute_button")
            ) {
                Icon(
                    imageVector = if (isMuted) Icons.Default.MicOff else Icons.Default.Mic,
                    contentDescription = "Mute",
                    tint = if (isMuted) Color.Black else Color.White
                )
            }

            // Video Toggle
            IconButton(
                onClick = { isVideoOff = !isVideoOff },
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(if (isVideoOff) Color.White else MeetSurfaceDark)
                    .testTag("call_video_toggle_button")
            ) {
                Icon(
                    imageVector = if (isVideoOff) Icons.Default.VideocamOff else Icons.Default.Videocam,
                    contentDescription = "Toggle Video",
                    tint = if (isVideoOff) Color.Black else Color.White
                )
            }

            // Speaker Toggle
            IconButton(
                onClick = { isSpeakerOn = !isSpeakerOn },
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(if (isSpeakerOn) MeetGoogleBlue else MeetSurfaceDark)
                    .testTag("call_speaker_button")
            ) {
                Icon(
                    imageVector = Icons.Default.VolumeUp,
                    contentDescription = "Speaker",
                    tint = Color.White
                )
            }

            // End Call FAB Button
            FloatingActionButton(
                onClick = {
                    viewModel.endCall()
                    onEndCall()
                },
                containerColor = CallEndRed,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier
                    .size(64.dp)
                    .testTag("end_call_button")
            ) {
                Icon(
                    imageVector = Icons.Default.CallEnd,
                    contentDescription = "End Call",
                    modifier = Modifier.size(32.dp)
                )
            }
        }

        // Add Call Dialog Overlay
        if (showAddCallDialog) {
            androidx.compose.ui.window.Dialog(onDismissRequest = { showAddCallDialog = false }) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MeetSurfaceDark,
                    tonalElevation = 12.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = MeetGoogleBlue)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Add Number to Call (+)", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 16.sp)
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        OutlinedTextField(
                            value = addNumberInput,
                            onValueChange = { addNumberInput = it },
                            placeholder = { Text("Enter number e.g. +973 17214433", color = Color.Gray) },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("add_number_to_call_input")
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Button(
                                onClick = { showAddCallDialog = false },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                            ) {
                                Text("Cancel", color = Color.LightGray)
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Button(
                                onClick = {
                                    if (addNumberInput.isNotBlank()) {
                                        viewModel.addNumberToConferenceCall(addNumberInput)
                                        addNumberInput = ""
                                        showAddCallDialog = false
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = CallAcceptGreen),
                                modifier = Modifier.testTag("confirm_add_call_button")
                            ) {
                                Text("Merge Line (+)", fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AnimatedSignalBars(modifier: Modifier = Modifier) {
    var barCount by remember { mutableIntStateOf(4) }

    LaunchedEffect(Unit) {
        val pattern = listOf(4, 4, 3, 4, 2, 3, 4, 4, 3, 4)
        var idx = 0
        while (true) {
            delay(1200)
            idx = (idx + 1) % pattern.size
            barCount = pattern[idx]
        }
    }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        for (i in 1..4) {
            val barHeight = (i * 3 + 3).dp
            val isActive = i <= barCount
            Box(
                modifier = Modifier
                    .width(3.5.dp)
                    .height(barHeight)
                    .clip(RoundedCornerShape(1.dp))
                    .background(if (isActive) CallAcceptGreen else Color.Gray.copy(alpha = 0.4f))
            )
        }
    }
}

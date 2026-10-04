package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.BahrainRed
import com.example.ui.theme.CallAcceptGreen
import com.example.ui.theme.MeetGoogleBlue
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class DoorbellVisitorLog(
    val id: String,
    val name: String,
    val timeStr: String,
    val dateStr: String,
    val isMissed: Boolean,
    val photoDesc: String
)

@Composable
fun DoorbellCameraDialog(
    onCallDoorbell: (String) -> Unit,
    onSpeakMessage: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var isDoorLocked by remember { mutableStateOf(true) }
    var isDoorbellRinging by remember { mutableStateOf(false) }
    var statusText by remember { mutableStateOf("Doorbell Camera Ready • Live HD Stream") }

    val visitorLogs = remember {
        mutableStateListOf(
            DoorbellVisitorLog(
                id = "1",
                name = "Talabat Delivery Rider",
                timeStr = "02:15 PM",
                dateStr = "Today",
                isMissed = true,
                photoDesc = "Rider holding food bag at front door"
            ),
            DoorbellVisitorLog(
                id = "2",
                name = "Postman / Courier",
                timeStr = "11:30 AM",
                dateStr = "Today",
                isMissed = true,
                photoDesc = "Courier with package at porch"
            ),
            DoorbellVisitorLog(
                id = "3",
                name = "Neighbor / Visitor",
                timeStr = "08:45 PM",
                dateStr = "Yesterday",
                isMissed = false,
                photoDesc = "Visitor ringing door camera"
            )
        )
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            color = Color(0xFF10141C)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = BahrainRed.copy(0.2f),
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = BahrainRed)
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Bahrain Smart Doorbell 🔔",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                            Text(
                                text = "Manama Villa #402 • Front Porch HD Camera",
                                color = Color.LightGray,
                                fontSize = 12.sp
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_doorbell_dialog")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Doorbell Camera Live Feed Box
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF000000)),
                    elevation = CardDefaults.cardElevation(8.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        // Simulated Live Outdoor Camera Background
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(Color(0xFF1E293B), Color(0xFF0F172A))
                                    )
                                )
                        )

                        // Camera View Overlay Graphic
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = if (isDoorbellRinging) BahrainRed.copy(0.3f) else MeetGoogleBlue.copy(0.2f),
                                modifier = Modifier.size(80.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = if (isDoorbellRinging) Icons.Default.NotificationsActive else Icons.Default.Videocam,
                                        contentDescription = "Doorbell Camera",
                                        tint = if (isDoorbellRinging) BahrainRed else Color.White,
                                        modifier = Modifier.size(48.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = if (isDoorbellRinging) "🔔 SOMEONE IS RINGING THE DOORBELL!" else "📹 Live Doorbell Camera Stream (Front Gate)",
                                color = if (isDoorbellRinging) Color.Yellow else Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = statusText,
                                color = Color.LightGray,
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center
                            )
                        }

                        // Top LIVE badge & Lock status overlay
                        Row(
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                color = Color.Red,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "🔴 LIVE HD",
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                color = if (isDoorLocked) Color.DarkGray else CallAcceptGreen,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = if (isDoorLocked) "🔒 DOOR LOCKED" else "🔓 DOOR UNLOCKED",
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Control Actions Row: Lock/Unlock, Ring Test, Call Doorbell
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Lock / Unlock Button
                    Button(
                        onClick = {
                            isDoorLocked = !isDoorLocked
                            val msg = if (isDoorLocked) "Door has been LOCKED safely!" else "Door UNLOCKED successfully!"
                            statusText = msg
                            onSpeakMessage(msg)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isDoorLocked) CallAcceptGreen else BahrainRed
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("door_lock_toggle_button")
                    ) {
                        Icon(
                            imageVector = if (isDoorLocked) Icons.Default.LockOpen else Icons.Default.Lock,
                            contentDescription = null,
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isDoorLocked) "UNLOCK DOOR" else "LOCK DOOR",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 12.sp
                        )
                    }

                    // Call Doorbell Button
                    Button(
                        onClick = {
                            val timeNow = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
                            onSpeakMessage("Calling Front Doorbell camera... Officially no one is outside at $timeNow.")
                            statusText = "Calling Doorbell: Officially no one outside right now."
                            onCallDoorbell("Doorbell Camera")
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MeetGoogleBlue),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("call_doorbell_button")
                    ) {
                        Icon(Icons.Default.Call, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("CALL DOORBELL", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Trigger Doorbell Ring Simulation Button
                Button(
                    onClick = {
                        isDoorbellRinging = true
                        val timeNow = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
                        onSpeakMessage("Ding Dong! Someone is ringing your door bell at $timeNow!")
                        statusText = "Ding Dong! Visitor at front door at $timeNow!"
                        // Log visitor
                        visitorLogs.add(
                            0,
                            DoorbellVisitorLog(
                                id = System.currentTimeMillis().toString(),
                                name = "Front Door Visitor",
                                timeStr = timeNow,
                                dateStr = "Just now",
                                isMissed = true,
                                photoDesc = "Visitor at front porch ring camera"
                            )
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BahrainRed),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("ring_doorbell_simulation_button")
                ) {
                    Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("SIMULATE VISITOR RINGING DOORBELL 🛎️", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Missed Visitors Log Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.History, contentDescription = null, tint = BahrainRed, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Missed Doorbell Visitor History & Camera Logs",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Visitor Log List
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(visitorLogs) { visitor ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        shape = CircleShape,
                                        color = if (visitor.isMissed) BahrainRed.copy(0.2f) else CallAcceptGreen.copy(0.2f),
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = if (visitor.isMissed) Icons.Default.NotificationsActive else Icons.Default.Person,
                                                contentDescription = null,
                                                tint = if (visitor.isMissed) BahrainRed else CallAcceptGreen,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = visitor.name,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            fontSize = 13.sp
                                        )
                                        Text(
                                            text = "📷 ${visitor.photoDesc}",
                                            fontSize = 11.sp,
                                            color = Color.LightGray
                                        )
                                    }
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Surface(
                                        color = if (visitor.isMissed) BahrainRed else Color.Gray,
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = if (visitor.isMissed) "MISSED 🕒 ${visitor.timeStr}" else "ANSWERED 🕒 ${visitor.timeStr}",
                                            color = Color.White,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = visitor.dateStr,
                                        fontSize = 10.sp,
                                        color = Color.Gray
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

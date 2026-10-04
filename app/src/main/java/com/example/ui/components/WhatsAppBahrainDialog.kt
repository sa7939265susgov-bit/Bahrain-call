package com.example.ui.components

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
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
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

data class WhatsAppContact(
    val name: String,
    val number: String,
    val avatarEmoji: String,
    val lastMessage: String,
    val isGroup: Boolean = false,
    val groupMembers: List<String> = emptyList()
)

data class WhatsAppChatMessage(
    val sender: String,
    val text: String,
    val isVoicemail: Boolean = false,
    val timeStr: String
)

@Composable
fun WhatsAppBahrainDialog(
    userPhone: String,
    userName: String,
    onStartCall: (target: String, isVideo: Boolean) -> Unit,
    onSpeak: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var myName by remember { mutableStateOf(userName.ifBlank { "Bahrain User" }) }
    var myPhone by remember { mutableStateOf(userPhone.ifBlank { "+973 33678882" }) }

    var selectedContact by remember {
        mutableStateOf(
            WhatsAppContact(
                name = "Talabat Rider Ahmed",
                number = "39123456",
                avatarEmoji = "🛵",
                lastMessage = "Your order is here! Please pick it up, I am near your location."
            )
        )
    }

    val contactsList = remember {
        mutableStateListOf(
            WhatsAppContact("Talabat Rider Ahmed", "39123456", "🛵", "Your order is here! Please pick it up, I am near your location."),
            WhatsAppContact("Bahrain Family Group 🇧🇭", "+973-GROUP-01", "👨‍👩‍👧‍👦", "Family group chat active", isGroup = true, groupMembers = listOf("Mother", "Father", "Ali", "Fatima")),
            WhatsAppContact("McDonald's Bahrain", "17221122", "🍔", "Your Big Mac order is confirmed!"),
            WhatsAppContact("Jasmi's Fast Food", "17770077", "🍟", "Chickie Meal on the way!"),
            WhatsAppContact("Sharaf DG Electronics", "80008008", "📱", "iPhone 15 Pro Max available at City Centre!"),
            WhatsAppContact("Costa Coffee Bahrain", "17112000", "☕", "Spanish Latte ready for pickup")
        )
    }

    val chatMessages = remember {
        mutableStateListOf(
            WhatsAppChatMessage("Talabat Rider Ahmed", "Hello! Your order is here! Please pick it up, I am near your location.", false, "02:20 PM"),
            WhatsAppChatMessage("Me", "Thank you, coming down right now!", false, "02:21 PM")
        )
    }

    var messageInput by remember { mutableStateOf("") }
    var showIncomingCallPopup by remember { mutableStateOf(false) }
    var incomingCallerName by remember { mutableStateOf("") }

    if (showIncomingCallPopup) {
        AlertDialog(
            onDismissRequest = { showIncomingCallPopup = false },
            title = {
                Text("📲 Incoming WhatsApp Call", fontWeight = FontWeight.Bold)
            },
            text = {
                Column {
                    Text("$incomingCallerName is calling you on WhatsApp Bahrain!")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Order Status: Driver is near your location in Bahrain.", fontSize = 12.sp, color = Color.Gray)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showIncomingCallPopup = false
                        onStartCall(incomingCallerName, false)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CallAcceptGreen)
                ) {
                    Text("ANSWER CALL 📞", color = Color.White)
                }
            },
            dismissButton = {
                Button(
                    onClick = { showIncomingCallPopup = false },
                    colors = ButtonDefaults.buttonColors(containerColor = BahrainRed)
                ) {
                    Text("DECLINE ❌", color = Color.White)
                }
            }
        )
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            shape = RoundedCornerShape(24.dp),
            color = Color(0xFF075E54) // WhatsApp Dark Teal
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Header Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("💬 WhatsApp Bahrain", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(color = Color(0xFF25D366), shape = RoundedCornerShape(8.dp)) {
                            Text("VERIFIED 🇧🇭", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                        }
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.testTag("close_whatsapp_dialog")) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Profile Registration Card
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF128C7E)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Profile: $myName", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("Number: $myPhone", color = Color.LightGray, fontSize = 11.sp)
                        }

                        // Simulated Incoming Driver Call Trigger Button
                        Surface(
                            color = Color(0xFF25D366),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    incomingCallerName = "Talabat Rider Ahmed"
                                    showIncomingCallPopup = true
                                    onSpeak("Incoming WhatsApp Call from Talabat Rider Ahmed!")
                                }
                                .testTag("sim_driver_call_button")
                        ) {
                            Text("SIM DRIVER CALL 🛵", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Chat View Container
                Row(modifier = Modifier.weight(1f)) {
                    // Contact List Column
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(contactsList) { contact ->
                            val isSelected = selectedContact.name == contact.name
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) Color(0xFF128C7E) else Color(0xFF004D40)
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedContact = contact
                                        chatMessages.clear()
                                        chatMessages.add(
                                            WhatsAppChatMessage(
                                                contact.name,
                                                contact.lastMessage,
                                                false,
                                                "02:20 PM"
                                            )
                                        )
                                    }
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(contact.avatarEmoji, fontSize = 18.sp)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(contact.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(contact.lastMessage, color = Color.LightGray, fontSize = 10.sp, maxLines = 1)
                                }
                            }
                        }
                    }

                    // Active Chat Box Column
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F3832)),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.weight(1.5f)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(10.dp)
                        ) {
                            // Active Chat Bar
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(selectedContact.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text(if (selectedContact.isGroup) "Group: ${selectedContact.groupMembers.joinToString(", ")}" else "Online • WhatsApp Bahrain", color = Color(0xFF25D366), fontSize = 10.sp)
                                }

                                Row {
                                    IconButton(
                                        onClick = { onStartCall(selectedContact.name, false) },
                                        modifier = Modifier.testTag("whatsapp_voice_call_btn")
                                    ) {
                                        Icon(Icons.Default.Call, contentDescription = "Voice Call", tint = Color.White, modifier = Modifier.size(20.dp))
                                    }
                                    IconButton(
                                        onClick = { onStartCall(selectedContact.name, true) },
                                        modifier = Modifier.testTag("whatsapp_video_call_btn")
                                    ) {
                                        Icon(Icons.Default.Videocam, contentDescription = "Video Call", tint = Color.White, modifier = Modifier.size(20.dp))
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Messages Box
                            LazyColumn(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                items(chatMessages) { msg ->
                                    val isMe = msg.sender == "Me"
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
                                    ) {
                                        Surface(
                                            color = if (isMe) Color(0xFF056162) else Color(0xFF263238),
                                            shape = RoundedCornerShape(12.dp)
                                        ) {
                                            Column(modifier = Modifier.padding(8.dp)) {
                                                if (!isMe) {
                                                    Text(msg.sender, color = Color(0xFF25D366), fontWeight = FontWeight.Bold, fontSize = 10.sp)
                                                }
                                                Text(msg.text, color = Color.White, fontSize = 12.sp)
                                                Text(msg.timeStr, color = Color.Gray, fontSize = 9.sp, modifier = Modifier.align(Alignment.End))
                                            }
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Chat Input & Voicemail row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = messageInput,
                                    onValueChange = { messageInput = it },
                                    placeholder = { Text("Type or message...", color = Color.Gray, fontSize = 11.sp) },
                                    singleLine = true,
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(16.dp)
                                )

                                Spacer(modifier = Modifier.width(4.dp))

                                // Send Text Button
                                IconButton(
                                    onClick = {
                                        if (messageInput.isNotBlank()) {
                                            val timeNow = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
                                            chatMessages.add(WhatsAppChatMessage("Me", messageInput, false, timeNow))
                                            val replyText = "Received! Your message to ${selectedContact.name} was sent on WhatsApp."
                                            onSpeak(replyText)
                                            messageInput = ""
                                        }
                                    }
                                ) {
                                    Icon(Icons.Default.Send, contentDescription = "Send", tint = Color(0xFF25D366))
                                }

                                // Send Voicemail Button
                                IconButton(
                                    onClick = {
                                        val timeNow = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
                                        val vmMsg = "🎙️ [Voicemail Recording 0:12]: 'Hello ${selectedContact.name}, please call me back!'"
                                        chatMessages.add(WhatsAppChatMessage("Me", vmMsg, true, timeNow))
                                        onSpeak("Voicemail recorded and sent to ${selectedContact.name} on WhatsApp!")
                                    }
                                ) {
                                    Icon(Icons.Default.Mic, contentDescription = "Voicemail", tint = BahrainRed)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

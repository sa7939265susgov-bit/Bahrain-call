package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CellTower
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.LocalPolice
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.DirectoryContact
import com.example.ui.theme.BahrainRed
import com.example.ui.theme.CallAcceptGreen
import com.example.ui.theme.MeetGoogleBlue
import com.example.viewmodel.BahrainMeetViewModel

@Composable
fun DirectoryScreen(
    viewModel: BahrainMeetViewModel,
    onStartCall: (target: String, isVideo: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val contacts by viewModel.directoryContacts.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }

    val categories = listOf("All", "Government", "Hospital", "Utility", "Restaurant", "Bank", "Cafe", "Electronics", "Telecom", "Court", "Mall", "Hotel", "Airport", "Airline", "Education", "Emergency")

    val filteredContacts = contacts.filter { contact ->
        val matchesCategory = (selectedCategory == "All" || contact.category.equals(selectedCategory, ignoreCase = true))
        val matchesQuery = searchQuery.isBlank() ||
                contact.name.contains(searchQuery, ignoreCase = true) ||
                contact.phoneNumber.contains(searchQuery, ignoreCase = true) ||
                contact.email.contains(searchQuery, ignoreCase = true) ||
                contact.description.contains(searchQuery, ignoreCase = true)
        matchesCategory && matchesQuery
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search KFC, Hotels, Airport 33678882, Emergency...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("directory_search_field"),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Category Filter Chips Row
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories) { cat ->
                val isSelected = selectedCategory == cat
                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .clickable { selectedCategory = cat }
                        .testTag("category_chip_$cat"),
                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ) {
                    Text(
                        text = cat,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                    )
                }
            }
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(filteredContacts, key = { it.id }) { contact ->
                DirectoryContactCard(
                    contact = contact,
                    onVideoCall = { onStartCall(contact.phoneNumber.ifBlank { contact.email }, true) },
                    onAudioCall = { onStartCall(contact.phoneNumber.ifBlank { contact.email }, false) }
                )
            }
        }
    }
}

@Composable
fun DirectoryContactCard(
    contact: DirectoryContact,
    onVideoCall: () -> Unit,
    onAudioCall: () -> Unit
) {
    val isOpen24Hours = contact.openingHours.equals("Open 24 Hours", ignoreCase = true)
    val isOpenNow = isOpen24Hours || contact.openingHours.isNotBlank()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("contact_card_${contact.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = contact.category.take(1).uppercase(),
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontSize = 18.sp
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = contact.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    if (contact.phoneNumber.isNotBlank()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Call,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = contact.phoneNumber,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                // Call buttons
                Row {
                    IconButton(
                        onClick = onAudioCall,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(CallAcceptGreen.copy(0.15f))
                            .testTag("audio_call_contact_${contact.id}")
                    ) {
                        Icon(imageVector = Icons.Default.Call, contentDescription = "Audio Call", tint = CallAcceptGreen, modifier = Modifier.size(18.dp))
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    IconButton(
                        onClick = onVideoCall,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(MeetGoogleBlue.copy(0.15f))
                            .testTag("video_call_contact_${contact.id}")
                    ) {
                        Icon(imageVector = Icons.Default.Videocam, contentDescription = "Video Call", tint = MeetGoogleBlue, modifier = Modifier.size(18.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Hours & Status Row
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = if (isOpenNow) CallAcceptGreen else BahrainRed,
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = if (isOpenNow) "OPEN NOW" else "CLOSED",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Hours: ${contact.openingHours}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Text(
                        text = contact.category,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

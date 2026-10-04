package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.CellTower
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SimCard
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.ui.theme.BahrainRed
import com.example.ui.theme.CallAcceptGreen
import com.example.ui.theme.MeetGoogleBlue

@Composable
fun RoamingManagerCard(
    isGccActive: Boolean,
    currentCountry: String,
    selectedSim: String,
    onToggleRoaming: (Boolean, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedCountry by remember(currentCountry) { mutableStateOf(currentCountry.ifEmpty { "Saudi Arabia" }) }
    var autoSelectCarrier by remember { mutableStateOf(true) }
    var dataLimitAlert by remember { mutableStateOf(true) }

    val gccCountries = listOf("Saudi Arabia", "Kuwait", "Qatar", "UAE", "Oman", "Yemen")

    val partnerCarrier = remember(selectedCountry) {
        when (selectedCountry) {
            "Saudi Arabia" -> "STC KSA / Mobily 5G"
            "Kuwait" -> "Zain KW / Ooredoo 5G"
            "Qatar" -> "Ooredoo Qatar / Vodafone 5G"
            "UAE" -> "e& / du 5G Partner"
            "Oman" -> "Omantel / Ooredoo Oman"
            "Yemen" -> "Sabafon / Yemen Mobile"
            else -> "GCC Partner 5G Network"
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("roaming_manager_card"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(if (isGccActive) CallAcceptGreen.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CellTower,
                            contentDescription = "Roaming Manager",
                            tint = if (isGccActive) CallAcceptGreen else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Roaming Manager",
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "GCC roaming data configurations",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Status Badge
                Surface(
                    color = if (isGccActive) CallAcceptGreen.copy(alpha = 0.15f) else Color.Gray.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = if (isGccActive) "GCC ACTIVE" else "LOCAL ONLY",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isGccActive) CallAcceptGreen else Color.Gray,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Main Master Toggle Row
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                color = Color.Transparent
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "GCC Roaming Data",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (isGccActive) "Data roaming enabled on $selectedSim SIM" else "Enable GCC roaming data configurations",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = isGccActive,
                        onCheckedChange = { active ->
                            onToggleRoaming(active, selectedCountry)
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = CallAcceptGreen
                        ),
                        modifier = Modifier.testTag("roaming_manager_toggle")
                    )
                }
            }

            // Expanded Configurations when GCC Roaming is Active (or always visible for preview)
            AnimatedVisibility(visible = isGccActive) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Select GCC Roaming Country",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Country Selection Grid
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        gccCountries.chunked(3).forEach { rowCountries ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                rowCountries.forEach { country ->
                                    val isSelected = selectedCountry == country
                                    Surface(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(10.dp))
                                            .border(
                                                width = if (isSelected) 2.dp else 1.dp,
                                                color = if (isSelected) BahrainRed else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
                                                shape = RoundedCornerShape(10.dp)
                                            )
                                            .clickable {
                                                selectedCountry = country
                                                onToggleRoaming(true, country)
                                            }
                                            .testTag("roaming_country_${country.replace(" ", "_").lowercase()}"),
                                        color = if (isSelected) BahrainRed.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surface
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Public,
                                                contentDescription = null,
                                                tint = if (isSelected) BahrainRed else MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = country,
                                                fontSize = 12.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                color = if (isSelected) BahrainRed else MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Partner Carrier Card
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = MeetGoogleBlue.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = MeetGoogleBlue,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Connected Partner: $partnerCarrier",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "GCC Data Pass active on $selectedSim • Unlimited VoLTE",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Secondary Data Configuration Toggles
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Auto-Select Partner Network",
                                fontWeight = FontWeight.Medium,
                                fontSize = 13.sp
                            )
                            Text(
                                text = "Automatically connect to strongest 5G GCC carrier",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = autoSelectCarrier,
                            onCheckedChange = { autoSelectCarrier = it },
                            modifier = Modifier.testTag("auto_select_carrier_toggle")
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Roaming Data Saving Mode",
                                fontWeight = FontWeight.Medium,
                                fontSize = 13.sp
                            )
                            Text(
                                text = "Notify when GCC roaming data exceeds 5 GB",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = dataLimitAlert,
                            onCheckedChange = { dataLimitAlert = it },
                            modifier = Modifier.testTag("data_limit_alert_toggle")
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RoamingManagerDialog(
    isGccActive: Boolean,
    currentCountry: String,
    selectedSim: String,
    onToggleRoaming: (Boolean, String) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text("App Settings: Roaming Manager", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                RoamingManagerCard(
                    isGccActive = isGccActive,
                    currentCountry = currentCountry,
                    selectedSim = selectedSim,
                    onToggleRoaming = onToggleRoaming
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier.testTag("roaming_manager_dialog_close_button")
            ) {
                Text("Done", fontWeight = FontWeight.Bold)
            }
        },
        shape = RoundedCornerShape(20.dp)
    )
}

package com.example.ui.components

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CellTower
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
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

@Composable
fun GccRoamingDialog(
    isGccActive: Boolean,
    currentCountry: String,
    onToggleRoaming: (active: Boolean, country: String) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedCountry by remember { mutableStateOf(currentCountry) }
    var targetNumber by remember { mutableStateOf("") }
    var statusMessage by remember { mutableStateOf<String?>(null) }

    val gccCountries = listOf("Saudi Arabia", "Kuwait", "Qatar", "UAE", "Oman", "Yemen")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.CellTower,
                    contentDescription = null,
                    tint = BahrainRed,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text("GCC Roaming (#973*)", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                if (isGccActive) {
                    // Turn OFF flow: "Do you want to shut off the roaming service? Are you in Bahrain?"
                    Text(
                        text = "GCC Roaming is currently ACTIVE in $currentCountry.",
                        fontWeight = FontWeight.Bold,
                        color = CallAcceptGreen,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Do you want to shut off the roaming service? Are you in Bahrain?",
                        fontWeight = FontWeight.Medium,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    if (statusMessage != null) {
                        Text(
                            text = statusMessage!!,
                            color = BahrainRed,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Option 1 / YES: Are you in Bahrain? YES -> GCC Roaming OFF, Bahrain Roaming ON
                        Button(
                            onClick = {
                                onToggleRoaming(false, "Bahrain Local")
                                statusMessage = "GCC Roaming turned OFF. Bahrain local network activated!"
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("gcc_roaming_yes_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = CallAcceptGreen),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("YES (1)", fontWeight = FontWeight.Bold)
                        }

                        // Option 2 / NO: Keep GCC Roaming
                        OutlinedButton(
                            onClick = {
                                statusMessage = "GCC Roaming remains ACTIVE in $selectedCountry."
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("gcc_roaming_no_button"),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("NO (2)", fontWeight = FontWeight.Bold)
                        }
                    }
                } else {
                    // Turn ON flow: Select country & enter target number
                    Text(
                        text = "Select a GCC Country for Roaming:",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        gccCountries.chunked(2).forEach { row ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                row.forEach { country ->
                                    val isSelected = selectedCountry == country
                                    Surface(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(8.dp))
                                            .border(
                                                width = if (isSelected) 2.dp else 1.dp,
                                                color = if (isSelected) BahrainRed else MaterialTheme.colorScheme.onSurfaceVariant.copy(0.3f),
                                                shape = RoundedCornerShape(8.dp)
                                            )
                                            .clickable { selectedCountry = country },
                                        color = if (isSelected) BahrainRed.copy(0.15f) else MaterialTheme.colorScheme.surfaceVariant.copy(0.2f)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(8.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Public,
                                                contentDescription = null,
                                                tint = if (isSelected) BahrainRed else MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.padding(end = 4.dp)
                                            )
                                            Text(
                                                text = country,
                                                fontSize = 12.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = targetNumber,
                        onValueChange = { targetNumber = it },
                        label = { Text("Target Phone Number") },
                        placeholder = { Text("e.g. +966 50 123 4567") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("gcc_target_phone_field"),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            onToggleRoaming(true, selectedCountry)
                            statusMessage = "GCC Roaming successfully activated for $selectedCountry!"
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("activate_gcc_roaming_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = BahrainRed),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Enable GCC Roaming in $selectedCountry", fontWeight = FontWeight.Bold)
                    }

                    if (statusMessage != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = statusMessage!!,
                            color = CallAcceptGreen,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.testTag("gcc_dialog_close_button")
            ) {
                Text("Close", color = MaterialTheme.colorScheme.onSurface)
            }
        },
        shape = RoundedCornerShape(20.dp)
    )
}

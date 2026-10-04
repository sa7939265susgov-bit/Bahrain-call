package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CellTower
import androidx.compose.material.icons.filled.Headset
import androidx.compose.material.icons.filled.SimCard
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.RoamingManagerCard
import com.example.ui.components.SimCardSelector
import com.example.ui.theme.BahrainRed
import com.example.ui.theme.BatelcoColor
import com.example.ui.theme.CallAcceptGreen
import com.example.ui.theme.StcColor
import com.example.ui.theme.ZainColor
import com.example.viewmodel.BahrainMeetViewModel

@Composable
fun SimManagerScreen(
    viewModel: BahrainMeetViewModel,
    onStartCall: (target: String, isVideo: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val userProfile by viewModel.userProfile.collectAsState()
    val activeSim = userProfile?.selectedSim ?: "BATELCO"
    val isGccActive = userProfile?.gccRoamingActive == true
    val gccCountry = userProfile?.gccCountry ?: "Saudi Arabia"

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.SimCard, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Bahrain Carrier SIM Manager", fontWeight = FontWeight.Bold, fontSize = 20.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // SIM Switcher Component
        SimCardSelector(
            selectedSim = activeSim,
            onSimSelected = { viewModel.selectSimCarrier(it) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Roaming Manager Settings Card
        RoamingManagerCard(
            isGccActive = isGccActive,
            currentCountry = gccCountry,
            selectedSim = activeSim,
            onToggleRoaming = { active, country ->
                viewModel.toggleGccRoaming(active, country)
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // USSD Actions Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Carrier USSD Shortcodes", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(12.dp))

                // USSD #973* - GCC Roaming
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("GCC Roaming Control", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                        Text("Dial #973* • Toggle GCC & Local Network", fontSize = 12.sp, color = Color.Gray)
                    }
                    Button(
                        onClick = { viewModel.openGccDialog() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isGccActive) BahrainRed else CallAcceptGreen
                        ),
                        modifier = Modifier.testTag("ussd_973_button"),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.CellTower, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("#973*")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // USSD #122* - Check Bill
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Check Balance & Bill", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                        Text("Dial #122* • View line balance & carrier info", fontSize = 12.sp, color = Color.Gray)
                    }
                    Button(
                        onClick = { viewModel.openBillDialog() },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier.testTag("ussd_122_button"),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.AccountBalanceWallet, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("#122*")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Carrier Support Hotlines Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Carrier Customer Support Hotlines", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("Note: Each support number strictly requires its matching SIM card!", fontSize = 12.sp, color = Color.Gray)

                Spacer(modifier = Modifier.height(12.dp))

                // Batelco Support 196
                CarrierSupportRow(
                    name = "Batelco Support (196)",
                    code = "196",
                    color = BatelcoColor,
                    isCurrentSim = activeSim == "BATELCO",
                    onCall = { onStartCall("196", false) }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // STC Support 119
                CarrierSupportRow(
                    name = "STC Support (119)",
                    code = "119",
                    color = StcColor,
                    isCurrentSim = activeSim == "STC",
                    onCall = { onStartCall("119", false) }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Zain Support 195
                CarrierSupportRow(
                    name = "Zain Support (195)",
                    code = "195",
                    color = ZainColor,
                    isCurrentSim = activeSim == "ZAIN",
                    onCall = { onStartCall("195", false) }
                )
            }
        }
    }
}

@Composable
fun CarrierSupportRow(
    name: String,
    code: String,
    color: Color,
    isCurrentSim: Boolean,
    onCall: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Headset, contentDescription = null, tint = color)
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(name, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                if (isCurrentSim) {
                    Text("Ready on active SIM", fontSize = 11.sp, color = CallAcceptGreen, fontWeight = FontWeight.Bold)
                } else {
                    Text("Requires $code SIM", fontSize = 11.sp, color = Color.Gray)
                }
            }
        }

        OutlinedButton(
            onClick = onCall,
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.testTag("call_support_$code")
        ) {
            Text("Call $code", fontWeight = FontWeight.Bold, color = color)
        }
    }
}

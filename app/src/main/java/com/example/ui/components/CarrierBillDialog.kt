package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BahrainRed
import com.example.ui.theme.BatelcoColor
import com.example.ui.theme.CallAcceptGreen
import com.example.ui.theme.StcColor
import com.example.ui.theme.ZainColor

@Composable
fun CarrierBillDialog(
    selectedSim: String,
    phoneNumber: String,
    balance: Double,
    isGccActive: Boolean,
    gccCountry: String,
    onDismiss: () -> Unit
) {
    var isPaid by remember { mutableStateOf(false) }

    val carrierColor = when (selectedSim) {
        "STC" -> StcColor
        "ZAIN" -> ZainColor
        else -> BatelcoColor
    }

    val carrierFullName = when (selectedSim) {
        "STC" -> "STC Bahrain (119)"
        "ZAIN" -> "Zain Bahrain (195)"
        else -> "Batelco Bahrain (196)"
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.AccountBalanceWallet,
                    contentDescription = null,
                    tint = carrierColor,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text("Carrier Bill & Info (#122*)", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(0.3f), RoundedCornerShape(12.dp))
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Carrier Provider:", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(carrierFullName, fontWeight = FontWeight.Bold, color = carrierColor, fontSize = 14.sp)
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Bahrain Mobile Number:", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(phoneNumber.ifBlank { "+973 33678882" }, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = MaterialTheme.colorScheme.onSurface.copy(0.1f))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Current Outstanding Bill:", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        text = if (isPaid) "0.000 BHD" else "%.3f BHD".format(balance),
                        fontWeight = FontWeight.Bold,
                        color = if (isPaid) CallAcceptGreen else BahrainRed,
                        fontSize = 16.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Active Data Plan:", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("Unlimited 5G Max", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Network Status:", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        text = if (isGccActive) "GCC Roaming ($gccCountry)" else "Bahrain Local Network",
                        fontWeight = FontWeight.Bold,
                        color = if (isGccActive) carrierColor else CallAcceptGreen,
                        fontSize = 13.sp
                    )
                }

                if (isPaid) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = CallAcceptGreen)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Bill successfully settled!", color = CallAcceptGreen, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        },
        confirmButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (!isPaid) {
                    Button(
                        onClick = { isPaid = true },
                        colors = ButtonDefaults.buttonColors(containerColor = CallAcceptGreen),
                        modifier = Modifier.testTag("pay_bill_button"),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Pay Bill Now", fontWeight = FontWeight.Bold)
                    }
                }
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier.testTag("close_bill_dialog_button"),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Close", color = MaterialTheme.colorScheme.onSurface)
                }
            }
        },
        shape = RoundedCornerShape(20.dp)
    )
}

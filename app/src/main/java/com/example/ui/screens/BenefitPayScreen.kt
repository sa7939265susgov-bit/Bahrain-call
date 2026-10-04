package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
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

data class BenefitTransaction(
    val id: String,
    val title: String,
    val bank: String,
    val amountBhd: Double,
    val isIncoming: Boolean,
    val timestamp: String
)

@Composable
fun BenefitPayScreen(
    modifier: Modifier = Modifier
) {
    var accountBalance by remember { mutableDoubleStateOf(142.500) }
    var selectedBank by remember { mutableStateOf("NBB (National Bank of Bahrain)") }
    var recipientInput by remember { mutableStateOf("+973 39887766") }
    var transferAmount by remember { mutableStateOf("10.000") }
    var showSendForm by remember { mutableStateOf(false) }
    var showQrDialog by remember { mutableStateOf(false) }
    var successMsg by remember { mutableStateOf<String?>(null) }

    var transactions by remember {
        mutableStateOf(
            listOf(
                BenefitTransaction("1", "Ali Al-Mansoori", "NBB Bank", 45.000, true, "Today, 11:20 AM"),
                BenefitTransaction("2", "Jasmi's Bahrain", "Merchant Pay", 2.800, false, "Today, 10:15 AM"),
                BenefitTransaction("3", "Government Benefit Direct", "CBB Central Bank", 100.000, true, "Yesterday"),
                BenefitTransaction("4", "Batelco Mobile Bill", "Batelco Pay", 15.000, false, "2 Days ago")
            )
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A))
    ) {
        // BenefitPay Header
        Surface(
            color = BahrainRed,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Smartphone, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("BenefitPay 🇧🇭", fontWeight = FontWeight.ExtraBold, color = Color.White, fontSize = 18.sp)
                    }
                    Surface(
                        color = Color.White.copy(0.2f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Fawri+ Instant", fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.Bold, modifier = Modifier.padding(6.dp))
                    }
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Bahraini Bank Card Display
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("benefitpay_card"),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(selectedBank, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                            Icon(Icons.Default.CreditCard, contentDescription = null, tint = BahrainRed)
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        Text("BAHRAIN USER", fontWeight = FontWeight.SemiBold, color = Color.Gray, fontSize = 11.sp)
                        Text("BH973 NBB0 0000 3367 8882 01", fontWeight = FontWeight.Bold, color = Color.LightGray, fontSize = 13.sp)

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Column {
                                Text("Available Balance", fontSize = 11.sp, color = Color.Gray)
                                Text("BHD ${"%.3f".format(accountBalance)}", fontWeight = FontWeight.ExtraBold, color = CallAcceptGreen, fontSize = 22.sp)
                            }

                            Button(
                                onClick = { showQrDialog = !showQrDialog },
                                colors = ButtonDefaults.buttonColors(containerColor = MeetGoogleBlue),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.QrCode, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("My QR", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Quick Actions Bar
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = { showSendForm = !showSendForm },
                        colors = ButtonDefaults.buttonColors(containerColor = CallAcceptGreen),
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                            .testTag("fawri_transfer_button"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Fawri+ Send", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            accountBalance += 25.000
                            successMsg = "Received BHD 25.000 via Fawri+ Transfer!"
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MeetGoogleBlue),
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                            .testTag("receive_money_button"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.ArrowDownward, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Receive 25 BHD", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Success banner
            successMsg?.let { msg ->
                item {
                    Surface(
                        color = CallAcceptGreen,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(msg, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }

            // Fawri+ Transfer Form
            if (showSendForm) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("Fawri+ Instant Transfer (Bahrain Banks)", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = recipientInput,
                                onValueChange = { recipientInput = it },
                                label = { Text("Mobile Phone or IBAN", color = Color.Gray) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("fawri_recipient_field")
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = transferAmount,
                                onValueChange = { transferAmount = it },
                                label = { Text("Amount (BHD)", color = Color.Gray) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("fawri_amount_field")
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Button(
                                onClick = {
                                    val amt = transferAmount.toDoubleOrNull() ?: 10.000
                                    if (accountBalance >= amt) {
                                        accountBalance -= amt
                                        val newTx = BenefitTransaction(
                                            id = System.currentTimeMillis().toString(),
                                            title = recipientInput,
                                            bank = "Fawri+ Transfer",
                                            amountBhd = amt,
                                            isIncoming = false,
                                            timestamp = "Just now"
                                        )
                                        transactions = listOf(newTx) + transactions
                                        successMsg = "Successfully transferred BHD ${"%.3f".format(amt)} to $recipientInput!"
                                        showSendForm = false
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = BahrainRed),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("CONFIRM FAWRI+ TRANSFER", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // QR Code Modal
            if (showQrDialog) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("BenefitPay Scan to Pay QR", fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 15.sp)
                            Spacer(modifier = Modifier.height(12.dp))
                            Box(
                                modifier = Modifier
                                    .size(140.dp)
                                    .background(Color.Black),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(" [ QR CODE ] ", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Text("Scan this QR in any store or bank in Bahrain", fontSize = 11.sp, color = Color.Gray)
                        }
                    }
                }
            }

            // Transfer History
            item {
                Text("Recent BenefitPay Transactions:", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
            }

            items(transactions) { tx ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(if (tx.isIncoming) CallAcceptGreen.copy(0.2f) else BahrainRed.copy(0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (tx.isIncoming) Icons.Default.ArrowDownward else Icons.Default.ArrowUpward,
                                contentDescription = null,
                                tint = if (tx.isIncoming) CallAcceptGreen else BahrainRed,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(tx.title, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                            Text("${tx.bank} • ${tx.timestamp}", fontSize = 11.sp, color = Color.Gray)
                        }

                        Text(
                            text = "${if (tx.isIncoming) "+" else "-"}BHD ${"%.3f".format(tx.amountBhd)}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = if (tx.isIncoming) CallAcceptGreen else BahrainRed
                        )
                    }
                }
            }
        }
    }
}

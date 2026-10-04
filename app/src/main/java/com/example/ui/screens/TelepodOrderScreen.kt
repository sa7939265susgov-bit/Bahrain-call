package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.BorderStroke
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.PhoneInTalk
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BahrainRed
import com.example.ui.theme.CallAcceptGreen
import com.example.ui.theme.MeetGoogleBlue
import kotlinx.coroutines.delay

data class MenuItem(
    val id: String,
    val name: String,
    val priceBhd: Double,
    val storeName: String,
    val category: String
)

@Composable
fun TelepodOrderScreen(
    onOpenBenefitPay: () -> Unit,
    onStartCall: (String, Boolean) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier
) {
    var selectedStore by remember { mutableStateOf("Jasmi's Bahrain") }
    var selectedPaymentMethod by remember { mutableStateOf("BenefitPay") } // BenefitPay, Bahrain Card, Cash, Apple Pay
    var buildingNo by remember { mutableStateOf("Building 12") }
    var roadNo by remember { mutableStateOf("Road 2801") }
    var blockNo by remember { mutableStateOf("Block 428, Seef") }
    var cartItems by remember { mutableStateOf(mapOf<MenuItem, Int>()) }

    // Telepod Tracking state
    var isOrderActive by remember { mutableStateOf(false) }
    var deliveryProgress by remember { mutableIntStateOf(1) } // 1: Confirmed, 2: Preparing, 3: Ocean Causeway Telepod, 4: Arrived
    var podUnlocked by remember { mutableStateOf(false) }

    val stores = listOf("Jasmi's Bahrain", "KFC Bahrain", "Al Baik Bahrain", "Starbucks Cafe", "Sharaf DG Electronics")

    val menuCatalog = remember {
        listOf(
            MenuItem("j1", "Jasmi's Mighty Chicken Meal", 2.800, "Jasmi's Bahrain", "Food"),
            MenuItem("j2", "Jasmi's Special Burger & Fries", 2.200, "Jasmi's Bahrain", "Food"),
            MenuItem("k1", "KFC Zinger Box Meal", 2.900, "KFC Bahrain", "Food"),
            MenuItem("a1", "Al Baik 4pc Broasted Meal", 2.100, "Al Baik Bahrain", "Food"),
            MenuItem("s1", "Starbucks Caramel Macchiato", 1.800, "Starbucks Cafe", "Cafe"),
            MenuItem("s2", "Starbucks Iced Latte", 1.600, "Starbucks Cafe", "Cafe"),
            MenuItem("e1", "Wireless Earbuds Noise Cancel", 18.500, "Sharaf DG Electronics", "Electronics"),
            MenuItem("e2", "Fast PowerBank 20000mAh", 12.000, "Sharaf DG Electronics", "Electronics")
        )
    }

    val storeItems = menuCatalog.filter { it.storeName == selectedStore }
    val totalPrice = cartItems.entries.sumOf { it.key.priceBhd * it.value }

    LaunchedEffect(isOrderActive) {
        if (isOrderActive) {
            deliveryProgress = 1
            podUnlocked = false
            delay(3000)
            deliveryProgress = 2
            delay(4000)
            deliveryProgress = 3
            delay(5000)
            deliveryProgress = 4
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 4.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFF5722)), // Talabat Orange
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.ShoppingBag, contentDescription = null, tint = Color.White)
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text("Talabat Express Delivery 🛵 (طلبات)", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text("Fastest Food & Grocery Delivery in Bahrain", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }

        if (isOrderActive) {
            // Live Talabat Delivery Tracking Screen
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                color = Color(0xFFFF5722),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text("talabat", fontWeight = FontWeight.Black, color = Color.White, fontSize = 12.sp, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("LIVE RIDER ROAD TRACKING", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                        }
                        Spacer(modifier = Modifier.height(10.dp))

                        // Talabat Bahrain Road Simulation Canvas
                        Canvas(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(130.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF0F172A))
                        ) {
                            val w = size.width
                            val h = size.height

                            // Highway Road
                            drawLine(
                                color = Color(0xFF475569),
                                start = Offset(10f, h / 2),
                                end = Offset(w - 10f, h / 2),
                                strokeWidth = 14f
                            )
                            // Dash line
                            drawLine(
                                color = Color.Yellow,
                                start = Offset(10f, h / 2),
                                end = Offset(w - 10f, h / 2),
                                strokeWidth = 2f
                            )

                            // Rider position offset
                            val riderX = when (deliveryProgress) {
                                1 -> w * 0.15f
                                2 -> w * 0.35f
                                3 -> w * 0.65f
                                else -> w * 0.90f
                            }

                            // Store
                            drawCircle(color = BahrainRed, radius = 16f, center = Offset(w * 0.10f, h / 2))
                            // User Home
                            drawCircle(color = CallAcceptGreen, radius = 16f, center = Offset(w * 0.90f, h / 2))

                            // Talabat Rider Bike
                            drawCircle(color = Color(0xFFFF5722), radius = 20f, center = Offset(riderX, h / 2))
                            drawCircle(color = Color.White, radius = 8f, center = Offset(riderX, h / 2))
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        val statusText = when (deliveryProgress) {
                            1 -> "Order Confirmed at $selectedStore"
                            2 -> "Jasmi's / Store preparing order for Talabat Rider #BH-902..."
                            3 -> "Talabat Rider is driving on Seef Highway towards $buildingNo..."
                            else -> "Talabat Rider Arrived at $buildingNo! Ready for Pickup."
                        }

                        Text(statusText, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(12.dp))

                        if (deliveryProgress == 4) {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = CallAcceptGreen.copy(alpha = 0.15f)),
                                border = BorderStroke(1.dp, CallAcceptGreen),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Call, contentDescription = null, tint = CallAcceptGreen)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text("📞 TALABAT RIDER CALLING...", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                                            Text("Ahmed (+973 39123456): 'Assalamu Alaikum! I am near to your house at $buildingNo!'", fontSize = 11.sp, color = Color.LightGray)
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Button(
                                        onClick = { onStartCall("Talabat Rider Ahmed (+973 39123456)", false) },
                                        colors = ButtonDefaults.buttonColors(containerColor = CallAcceptGreen),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("answer_talabat_rider_call"),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Icon(Icons.Default.PhoneInTalk, contentDescription = null, tint = Color.White)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("ANSWER TALABAT RIDER CALL 📞", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Button(
                                onClick = { podUnlocked = true },
                                colors = ButtonDefaults.buttonColors(containerColor = if (podUnlocked) CallAcceptGreen else Color(0xFFFF5722)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("unlock_telepod_button"),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(if (podUnlocked) Icons.Default.CheckCircle else Icons.Default.DirectionsCar, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(if (podUnlocked) "ORDER DELIVERED - BON APPÉTIT!" else "CONFIRM MEAL RECEIVED FROM RIDER", fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Button(
                            onClick = { isOrderActive = false },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("New Order / Close Tracking", color = MaterialTheme.colorScheme.onSurface)
                        }
                    }
                }
            }
        } else {
            // Order Form
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Store Selector Chips
                item {
                    Text("Select Bahrain Store / Cafe:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        stores.take(3).forEach { store ->
                            val isSel = selectedStore == store
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable { selectedStore = store }
                                    .testTag("store_chip_$store"),
                                color = if (isSel) BahrainRed else MaterialTheme.colorScheme.surfaceVariant
                            ) {
                                Text(
                                    text = store.replace(" Bahrain", ""),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSel) Color.White else MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp)
                                )
                            }
                        }
                    }
                }

                // Items list
                items(storeItems) { item ->
                    val qty = cartItems[item] ?: 0
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(item.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("BHD ${"%.3f".format(item.priceBhd)}", color = MeetGoogleBlue, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(
                                    onClick = {
                                        val newMap = cartItems.toMutableMap()
                                        if (qty > 1) newMap[item] = qty - 1
                                        else newMap.remove(item)
                                        cartItems = newMap
                                    }
                                ) {
                                    Icon(Icons.Default.Remove, contentDescription = "Minus")
                                }
                                Text("$qty", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                IconButton(
                                    onClick = {
                                        val newMap = cartItems.toMutableMap()
                                        newMap[item] = qty + 1
                                        cartItems = newMap
                                    }
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = "Plus")
                                }
                            }
                        }
                    }
                }

                // Delivery Address Section
                item {
                    Text("Bahrain Delivery Address:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = buildingNo,
                            onValueChange = { buildingNo = it },
                            label = { Text("Building") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = roadNo,
                            onValueChange = { roadNo = it },
                            label = { Text("Road") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = blockNo,
                            onValueChange = { blockNo = it },
                            label = { Text("Block") },
                            modifier = Modifier.weight(1.2f)
                        )
                    }
                }

                // Payment Method Section
                item {
                    Text("Select Payment Method:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(6.dp))

                    val payMethods = listOf("BenefitPay", "Bahrain Card (NBB/BBK)", "Cash on Delivery", "Apple Pay")
                    payMethods.forEach { method ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    selectedPaymentMethod = method
                                    if (method == "BenefitPay") onOpenBenefitPay()
                                }
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(selected = selectedPaymentMethod == method, onClick = { selectedPaymentMethod = method })
                            Text(method, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                        }
                    }
                }

                // Total & Order Button
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Total Amount:", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("BHD ${"%.3f".format(totalPrice)}", fontWeight = FontWeight.ExtraBold, color = BahrainRed, fontSize = 16.sp)
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Button(
                                onClick = { isOrderActive = true },
                                enabled = totalPrice > 0,
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5722)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("place_telepod_order_button"),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.DirectionsCar, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("PLACE TALABAT EXPRESS ORDER 🛵", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

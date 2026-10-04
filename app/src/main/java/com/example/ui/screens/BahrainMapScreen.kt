package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBag
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
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BahrainRed
import com.example.ui.theme.CallAcceptGreen
import com.example.ui.theme.MeetGoogleBlue

data class MapLocationPin(
    val id: String,
    val name: String,
    val category: String,
    val xRatio: Float, // 0f to 1f on canvas
    val yRatio: Float,
    val phone: String,
    val openingHours: String,
    val description: String
)

@Composable
fun BahrainMapScreen(
    onStartCall: (target: String) -> Unit,
    onOrderTelepod: (storeName: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedLayer by remember { mutableStateOf("Map") } // Map, Satellite, Traffic
    var selectedPin by remember { mutableStateOf<MapLocationPin?>(null) }
    var zoomScale by remember { mutableFloatStateOf(1f) }

    val pins = remember {
        listOf(
            MapLocationPin("manama", "Manama Capital & Financial Harbour", "Capital & Banks", 0.52f, 0.28f, "17214433", "7:30 AM - 3:30 PM", "NBB HQ, Pearl Monument, Financial Centre."),
            MapLocationPin("mcdonalds_seef", "McDonald's Seef Mall", "Restaurant 🍔", 0.43f, 0.26f, "17221122", "6:00 AM - 2:00 AM", "Burgers, fries & drive-thru in Seef District."),
            MapLocationPin("jasmis_riffa", "Jasmi's Fast Food Riffa", "Restaurant 🍟", 0.51f, 0.53f, "17770077", "6:00 AM - 3:00 AM", "Authentic Bahraini burgers & Chickie meal."),
            MapLocationPin("kfc_citycentre", "KFC City Centre Bahrain", "Restaurant 🍗", 0.45f, 0.27f, "17111111", "10:00 AM - 2:00 AM", "Fried chicken delivery & dining."),
            MapLocationPin("sharaf_dg", "Sharaf DG Electronics", "Electronics 📱", 0.46f, 0.28f, "80008008", "10:00 AM - 11:00 PM", "Laptops, iPhones & smart TV gadgets."),
            MapLocationPin("extra_electronics", "eXtra Stores Seef", "Electronics 🖥️", 0.42f, 0.24f, "17500000", "9:30 AM - 10:30 PM", "Smart TVs, appliances & SIM cards."),
            MapLocationPin("starbucks_harbour", "Starbucks Manama Harbour", "Cafe ☕", 0.53f, 0.29f, "17178000", "6:30 AM - 12:00 AM", "Specialty espresso, Frappuccino & pastries."),
            MapLocationPin("costa_bay", "Costa Coffee Bahrain Bay", "Cafe ☕", 0.55f, 0.26f, "17112000", "7:00 AM - 11:30 PM", "Handcrafted coffee & sandwiches."),
            MapLocationPin("nasser_pharmacy", "Nasser Pharmacy Muharraq", "Pharmacy 💊", 0.66f, 0.22f, "17231234", "Open 24 Hours", "24/7 Bahrain medical pharmacy & healthcare."),
            MapLocationPin("lulu_dana", "LuLu Hypermarket Dana Mall", "Supermarket 🛒", 0.48f, 0.31f, "17558888", "8:00 AM - 12:00 AM", "Fresh groceries, electronics & baked goods."),
            MapLocationPin("muharraq", "Muharraq Island & BIA Airport", "Airport & Heritage", 0.65f, 0.20f, "33678882", "Open 24 Hours", "Bahrain International Airport, Souq Muharraq."),
            MapLocationPin("seef", "Seef District & City Centre Mall", "Malls & Telecom", 0.44f, 0.25f, "17177771", "9:00 AM - 11:00 PM", "City Centre Mall, Ritz-Carlton, Batelco & STC Stores."),
            MapLocationPin("causeway", "King Fahd Causeway - Passport Island", "Border Checkpoint", 0.18f, 0.35f, "17796000", "Open 24 Hours", "Official Saudi-Bahrain border checkpoint, passport control & duty free."),
            MapLocationPin("riffa", "Riffa & Jasmi's HQ", "Residential & Food", 0.50f, 0.52f, "17770077", "6:00 AM - 3:00 AM", "Clock Tower, Jasmi's, Royal Golf Club."),
            MapLocationPin("sitra", "Sitra Industrial & Port Area", "Industry & Telecom", 0.60f, 0.42f, "17450000", "7:00 AM - 5:00 PM", "Port facilities, BAPCO refinery, logistics."),
            MapLocationPin("zallaq", "Zallaq Beach & Al Areen", "Resort & Nature", 0.35f, 0.70f, "17845100", "8:00 AM - 8:00 PM", "Sakhir Circuit F1, Sofitel Resort, Al Areen Wildlife."),
            MapLocationPin("durrat", "Durrat Al Bahrain Islands", "Resort", 0.55f, 0.90f, "17590000", "Open 24 Hours", "Luxury artificial crescent islands in south Bahrain.")
        )
    }

    val filteredPins = pins.filter {
        searchQuery.isBlank() || it.name.contains(searchQuery, ignoreCase = true) || it.category.contains(searchQuery, ignoreCase = true)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0D1B2A))
    ) {
        // Search & Filter Top Bar
        Surface(
            color = Color(0xFF1B263B),
            tonalElevation = 6.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = BahrainRed)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        "Bahrain Interactive Map 🇧🇭",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Row {
                        IconButton(onClick = { zoomScale = (zoomScale + 0.2f).coerceAtMost(2.5f) }) {
                            Icon(Icons.Default.Add, contentDescription = "Zoom In", tint = Color.White)
                        }
                        IconButton(onClick = { zoomScale = (zoomScale - 0.2f).coerceAtLeast(0.8f) }) {
                            Icon(Icons.Default.Remove, contentDescription = "Zoom Out", tint = Color.White)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search Passport Island, Manama, Seef, Airport...", color = Color.Gray, fontSize = 12.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("map_search_field"),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Map Layers Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val layers = listOf("Map", "Satellite", "Traffic")
                    layers.forEach { layer ->
                        val isSel = selectedLayer == layer
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { selectedLayer = layer },
                            color = if (isSel) MeetGoogleBlue else Color.White.copy(0.1f)
                        ) {
                            Text(
                                text = layer,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }

        // Map Canvas Box
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .pointerInput(Unit) {
                    detectTransformGestures { _, _, zoom, _ ->
                        zoomScale = (zoomScale * zoom).coerceIn(0.8f, 2.5f)
                    }
                }
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val canvasWidth = size.width
                val canvasHeight = size.height

                // Water background
                drawRect(
                    color = if (selectedLayer == "Satellite") Color(0xFF0A192F) else Color(0xFF1E3A5F),
                    size = size
                )

                // Causeway Path to Passport Island
                val causewayPath = Path().apply {
                    moveTo(canvasWidth * 0.44f, canvasHeight * 0.25f) // Seef
                    lineTo(canvasWidth * 0.30f, canvasHeight * 0.30f)
                    lineTo(canvasWidth * 0.18f, canvasHeight * 0.35f) // Passport Island
                }
                drawPath(
                    path = causewayPath,
                    color = Color.Yellow,
                    style = Stroke(width = 6f * zoomScale)
                )

                // Bahrain Main Island Contour
                val islandPath = Path().apply {
                    moveTo(canvasWidth * 0.45f, canvasHeight * 0.22f) // Northern Tip / Seef
                    quadraticTo(canvasWidth * 0.58f, canvasHeight * 0.22f, canvasWidth * 0.58f, canvasHeight * 0.32f) // Manama Harbour
                    quadraticTo(canvasWidth * 0.65f, canvasHeight * 0.42f, canvasWidth * 0.62f, canvasHeight * 0.55f) // Sitra / Askar
                    quadraticTo(canvasWidth * 0.58f, canvasHeight * 0.85f, canvasWidth * 0.52f, canvasHeight * 0.95f) // Durrat South
                    quadraticTo(canvasWidth * 0.35f, canvasHeight * 0.80f, canvasWidth * 0.32f, canvasHeight * 0.65f) // Zallaq Coast
                    quadraticTo(canvasWidth * 0.38f, canvasHeight * 0.40f, canvasWidth * 0.45f, canvasHeight * 0.22f) // Back to North
                }

                drawPath(
                    path = islandPath,
                    color = if (selectedLayer == "Satellite") Color(0xFF1B4332) else Color(0xFF2D6A4F)
                )
                drawPath(
                    path = islandPath,
                    color = Color(0xFF52B788),
                    style = Stroke(width = 3f * zoomScale)
                )

                // Muharraq Island
                val muharraqPath = Path().apply {
                    addOval(
                        androidx.compose.ui.geometry.Rect(
                            center = Offset(canvasWidth * 0.65f, canvasHeight * 0.20f),
                            radius = 45f * zoomScale
                        )
                    )
                }
                drawPath(
                    path = muharraqPath,
                    color = if (selectedLayer == "Satellite") Color(0xFF1B4332) else Color(0xFF2D6A4F)
                )

                // Passport Island Circle at King Fahd Causeway
                drawCircle(
                    color = BahrainRed,
                    radius = 18f * zoomScale,
                    center = Offset(canvasWidth * 0.18f, canvasHeight * 0.35f)
                )
                drawCircle(
                    color = Color.White,
                    radius = 8f * zoomScale,
                    center = Offset(canvasWidth * 0.18f, canvasHeight * 0.35f)
                )
            }

            // Pins Overlay
            filteredPins.forEach { pin ->
                val pinColor = when {
                    pin.id == "causeway" -> BahrainRed
                    pin.id == "manama" -> MeetGoogleBlue
                    else -> CallAcceptGreen
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(
                            start = (pin.xRatio * 320).dp,
                            top = (pin.yRatio * 450).dp
                        )
                        .clip(CircleShape)
                        .background(pinColor)
                        .clickable { selectedPin = pin }
                        .padding(6.dp)
                        .testTag("pin_${pin.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = pin.name,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            // Causeway Label Overlay
            Surface(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 12.dp, top = 140.dp),
                color = BahrainRed.copy(0.9f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    "📍 King Fahd Causeway & Passport Island",
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }

        // Bottom Details Card if Pin Selected
        selectedPin?.let { pin ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1B263B))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(pin.name, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 15.sp)
                            Text("${pin.category} • Hours: ${pin.openingHours}", color = MeetGoogleBlue, fontSize = 11.sp)
                        }
                        IconButton(onClick = { selectedPin = null }) {
                            Text("✕", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(pin.description, color = Color.LightGray, fontSize = 12.sp)

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { onStartCall(pin.phone) },
                            colors = ButtonDefaults.buttonColors(containerColor = CallAcceptGreen),
                            modifier = Modifier.weight(1f).height(40.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Call", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { onOrderTelepod(pin.name) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5722)),
                            modifier = Modifier.weight(1f).height(40.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.ShoppingBag, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Talabat 🛵", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

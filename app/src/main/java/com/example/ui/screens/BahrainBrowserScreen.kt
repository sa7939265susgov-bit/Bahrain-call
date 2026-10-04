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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Videocam
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlinx.coroutines.delay

data class InternationalCountryInfo(
    val name: String,
    val flag: String,
    val code: String,
    val defaultNum: String,
    val capital: String,
    val timeZoneId: String,
    val callRate: String,
    val weatherStr: String
)

@Composable
fun BahrainBrowserScreen(
    onStartCall: (target: String, isVideo: Boolean) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier
) {
    var activeBrowserTab by remember { mutableIntStateOf(0) } // 0: Web Portals, 1: International Directory & Times
    var urlInput by remember { mutableStateOf("https://www.bahrain.bh") }
    var currentDomain by remember { mutableStateOf("bahrain.bh") }
    var searchQuery by remember { mutableStateOf("") }

    val bookmarks = listOf(
        "eGovernment" to "https://www.bahrain.bh",
        "BenefitPay" to "https://www.benefit.bh",
        "NBB Online" to "https://www.nbbonline.com",
        "Jasmi's Food" to "https://www.jasmis.com",
        "Batelco Care" to "https://www.batelco.com.bh"
    )

    val intlCountries = remember {
        listOf(
            InternationalCountryInfo("India", "🇮🇳", "+91", "+91-22-6000-1234", "New Delhi / Mumbai", "Asia/Kolkata", "0.025 BHD/min", "29°C Clear"),
            InternationalCountryInfo("Saudi Arabia", "🇸🇦", "+966", "+966-11-200-0000", "Riyadh", "Asia/Riyadh", "0.030 BHD/min", "38°C Sunny"),
            InternationalCountryInfo("United Arab Emirates", "🇦🇪", "+971", "+971-4-300-0000", "Abu Dhabi / Dubai", "Asia/Dubai", "0.030 BHD/min", "36°C Sunny"),
            InternationalCountryInfo("Kuwait", "🇰🇼", "+965", "+965-2200-0000", "Kuwait City", "Asia/Kuwait", "0.030 BHD/min", "37°C Clear"),
            InternationalCountryInfo("Qatar", "🇶🇦", "+974", "+974-4400-0000", "Doha", "Asia/Qatar", "0.030 BHD/min", "35°C Sunny"),
            InternationalCountryInfo("Oman", "🇴🇲", "+968", "+968-2400-0000", "Muscat", "Asia/Muscat", "0.030 BHD/min", "34°C Clear"),
            InternationalCountryInfo("United Kingdom", "🇬🇧", "+44", "+44-20-7730-1234", "London", "Europe/London", "0.045 BHD/min", "19°C Mild"),
            InternationalCountryInfo("United States", "🇺🇸", "+1", "+1-212-555-0199", "Washington D.C. / NYC", "America/New_York", "0.040 BHD/min", "24°C Sunny"),
            InternationalCountryInfo("Philippines", "🇵🇭", "+63", "+63-2-8000-0000", "Manila", "Asia/Manila", "0.035 BHD/min", "31°C Tropical"),
            InternationalCountryInfo("Pakistan", "🇵🇰", "+92", "+92-51-111-000-000", "Islamabad / Karachi", "Asia/Karachi", "0.028 BHD/min", "32°C Clear"),
            InternationalCountryInfo("Japan", "🇯🇵", "+81", "+81-3-3573-2371", "Tokyo", "Asia/Tokyo", "0.050 BHD/min", "22°C Clear"),
            InternationalCountryInfo("South Korea", "🇰🇷", "+82", "+82-2-540-1234", "Seoul", "Asia/Seoul", "0.050 BHD/min", "21°C Fair"),
            InternationalCountryInfo("Australia", "🇦🇺", "+61", "+61-2-9000-1234", "Canberra / Sydney", "Australia/Sydney", "0.055 BHD/min", "18°C Breezy"),
            InternationalCountryInfo("Egypt", "🇪🇬", "+20", "+20-2-2500-0000", "Cairo", "Africa/Cairo", "0.032 BHD/min", "33°C Sunny")
        )
    }

    var currentTimeMillis by remember { mutableStateOf(System.currentTimeMillis()) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            currentTimeMillis = System.currentTimeMillis()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A))
    ) {
        // Security Banner & Top Control Header
        Surface(
            color = Color(0xFF1E293B),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Shield, contentDescription = null, tint = CallAcceptGreen)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Bahrain Smart Cyber Browser 🌐", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                        Text("Kingdom Web Portals & International Time/Call Directory", fontSize = 10.sp, color = Color.LightGray)
                    }
                    Surface(
                        color = CallAcceptGreen.copy(0.2f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("VERIFIED 🇧🇭", color = CallAcceptGreen, fontWeight = FontWeight.Bold, fontSize = 10.sp, modifier = Modifier.padding(4.dp))
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Browser Mode Navigation Tabs (Web Portals vs International Calls & Times)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        color = if (activeBrowserTab == 0) BahrainRed else Color(0xFF334155),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { activeBrowserTab = 0 }
                            .testTag("browser_tab_portals")
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Language, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("🇧🇭 Kingdom Portals", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }

                    Surface(
                        color = if (activeBrowserTab == 1) MeetGoogleBlue else Color(0xFF334155),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { activeBrowserTab = 1 }
                            .testTag("browser_tab_international")
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Public, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("🌍 Intl Directory & Times", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Search & Address Input Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.Gray)
                    }
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh", tint = Color.White)
                    }

                    OutlinedTextField(
                        value = if (activeBrowserTab == 0) urlInput else searchQuery,
                        onValueChange = { input ->
                            if (activeBrowserTab == 0) {
                                urlInput = input
                                if (input.contains(".")) {
                                    currentDomain = input.replace("https://", "").replace("http://", "").split("/").firstOrNull() ?: input
                                }
                            } else {
                                searchQuery = input
                            }
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = if (activeBrowserTab == 0) Icons.Default.Lock else Icons.Default.Search,
                                contentDescription = null,
                                tint = if (activeBrowserTab == 0) CallAcceptGreen else MeetGoogleBlue,
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        placeholder = {
                            Text(
                                text = if (activeBrowserTab == 0) "Enter URL (e.g. bahrain.bh)..." else "Search country, code (+91, +44, +966)...",
                                fontSize = 11.sp,
                                color = Color.Gray
                            )
                        },
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("browser_input_field"),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                if (activeBrowserTab == 0) {
                    Spacer(modifier = Modifier.height(8.dp))
                    // Bookmarks Row
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(bookmarks) { (title, url) ->
                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable {
                                        urlInput = url
                                        currentDomain = url.replace("https://www.", "")
                                    }
                                    .testTag("bookmark_$title"),
                                color = MeetGoogleBlue.copy(0.2f)
                            ) {
                                Text(
                                    title,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Tab Content Canvas
        if (activeBrowserTab == 0) {
            // Web Portals Canvas
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier.fillMaxSize(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            color = BahrainRed,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                "Kingdom of Bahrain Verified Portal ($currentDomain)",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Icon(Icons.Default.Security, contentDescription = null, tint = MeetGoogleBlue, modifier = Modifier.size(64.dp))
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("Secure Connection Verified", fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 16.sp)
                        Text("Anti-Phishing & Anti-Malware Active for $currentDomain", fontSize = 12.sp, color = Color.Gray)

                        Spacer(modifier = Modifier.height(20.dp))

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9))
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text("🇧🇭 Official Kingdom Services Loaded:", fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 13.sp)
                                Spacer(modifier = Modifier.height(6.dp))
                                Text("• eGovernment Portal Access (bahrain.bh)", fontSize = 12.sp, color = Color.DarkGray)
                                Text("• CBB Central Bank SSL 256-Bit Standard", fontSize = 12.sp, color = Color.DarkGray)
                                Text("• Safe Payments via BenefitPay Gateways", fontSize = 12.sp, color = Color.DarkGray)
                            }
                        }
                    }
                }
            }
        } else {
            // International Directory & Live Times List
            val filteredCountries = intlCountries.filter { country ->
                searchQuery.isBlank() ||
                        country.name.contains(searchQuery, ignoreCase = true) ||
                        country.code.contains(searchQuery) ||
                        country.capital.contains(searchQuery, ignoreCase = true) ||
                        country.defaultNum.contains(searchQuery)
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Text(
                    text = "🌍 International Country Calling Codes & Live Times",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredCountries) { country ->
                        val tz = TimeZone.getTimeZone(country.timeZoneId)
                        val timeSdf = SimpleDateFormat("hh:mm:ss a", Locale.ENGLISH).apply { timeZone = tz }
                        val dateSdf = SimpleDateFormat("EEE, MMM d, yyyy", Locale.ENGLISH).apply { timeZone = tz }
                        val localTimeStr = timeSdf.format(Date(currentTimeMillis))
                        val localDateStr = dateSdf.format(Date(currentTimeMillis))

                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                // Header: Flag, Name, Code
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(country.flag, fontSize = 28.sp)
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(
                                                text = "${country.name} (${country.code})",
                                                color = Color.White,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 15.sp
                                            )
                                            Text(
                                                text = "Capital: ${country.capital} • Rate: ${country.callRate}",
                                                color = Color.LightGray,
                                                fontSize = 11.sp
                                            )
                                        }
                                    }

                                    Surface(
                                        color = MeetGoogleBlue.copy(0.2f),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(
                                            text = country.code,
                                            color = MeetGoogleBlue,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Live Time & Weather Card
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(10.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.Schedule, contentDescription = null, tint = Color.Yellow, modifier = Modifier.size(18.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Column {
                                                Text(
                                                    text = "Local Time: $localTimeStr",
                                                    color = Color.Yellow,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 12.sp
                                                )
                                                Text(
                                                    text = localDateStr,
                                                    color = Color.Gray,
                                                    fontSize = 10.sp
                                                )
                                            }
                                        }

                                        Text(
                                            text = "🌤️ ${country.weatherStr}",
                                            color = Color.White,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Call Action Buttons
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = {
                                            onStartCall(country.defaultNum, false)
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = CallAcceptGreen),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(40.dp)
                                            .testTag("call_intl_${country.name}")
                                    ) {
                                        Icon(Icons.Default.Call, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("CALL (${country.code})", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 11.sp)
                                    }

                                    Button(
                                        onClick = {
                                            onStartCall(country.defaultNum, true)
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = MeetGoogleBlue),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(40.dp)
                                            .testTag("video_intl_${country.name}")
                                    ) {
                                        Icon(Icons.Default.Videocam, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("VIDEO CALL", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CellTower
import androidx.compose.material.icons.filled.Dialpad
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.SimCard
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.ShoppingBag
import com.example.ui.components.CallDialpad
import com.example.ui.components.CarrierBillDialog
import com.example.ui.components.GccRoamingDialog
import com.example.ui.components.RadioMediaHubDialog
import com.example.ui.components.RoamingManagerDialog
import com.example.ui.components.SimCardSelector
import com.example.ui.theme.BahrainRed
import com.example.ui.theme.CallAcceptGreen
import com.example.ui.theme.MeetGoogleBlue
import com.example.viewmodel.BahrainMeetViewModel

@Composable
fun HomeScreen(
    viewModel: BahrainMeetViewModel,
    modifier: Modifier = Modifier
) {
    val dialInput by viewModel.dialInput.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()
    val showGccDialog by viewModel.showGccDialog.collectAsState()
    val showBillDialog by viewModel.showBillDialog.collectAsState()
    val showRadioHub by viewModel.showRadioHub.collectAsState()
    val mediaStreams by viewModel.mediaStreams.collectAsState()
    val callLogs by viewModel.callLogs.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) } // 0: Dialer, 1: Directory, 2: Map, 3: Order, 4: BenefitPay, 5: Browser, 6: Logs, 7: SIM Hub
    var showMenu by remember { mutableStateOf(false) }
    var showDoorbellDialog by remember { mutableStateOf(false) }
    var showWhatsAppDialog by remember { mutableStateOf(false) }
    var showRoamingManagerDialog by remember { mutableStateOf(false) }

    val activeSim = userProfile?.selectedSim ?: "BATELCO"

    // Dialogs
    if (showDoorbellDialog) {
        com.example.ui.components.DoorbellCameraDialog(
            onCallDoorbell = { target ->
                showDoorbellDialog = false
                viewModel.initiateCall(target, true, isIncoming = true)
            },
            onSpeakMessage = { msg ->
                viewModel.ttsHelper.speak(msg)
            },
            onDismiss = { showDoorbellDialog = false }
        )
    }

    if (showWhatsAppDialog) {
        com.example.ui.components.WhatsAppBahrainDialog(
            userPhone = userProfile?.phoneNumber ?: "+973 33678882",
            userName = userProfile?.userName ?: "Bahrain Resident",
            onStartCall = { target, isVideo ->
                showWhatsAppDialog = false
                viewModel.initiateCall(target, isVideo, isIncoming = false)
            },
            onSpeak = { msg ->
                viewModel.ttsHelper.speak(msg)
            },
            onDismiss = { showWhatsAppDialog = false }
        )
    }

    // Dialogs
    if (showRadioHub) {
        RadioMediaHubDialog(
            mediaStreams = mediaStreams,
            onAddMediaStream = { title, url, type -> viewModel.addCustomMediaStream(title, url, type) },
            onDeleteMediaStream = { id -> viewModel.deleteMediaStream(id) },
            onExitToDialer = {
                selectedTab = 0
                viewModel.promptWhoDoYouWantToCall()
            },
            onDismiss = { viewModel.dismissRadioHub() }
        )
    }

    if (showGccDialog) {
        GccRoamingDialog(
            isGccActive = userProfile?.gccRoamingActive == true,
            currentCountry = userProfile?.gccCountry ?: "Saudi Arabia",
            onToggleRoaming = { active, country -> viewModel.toggleGccRoaming(active, country) },
            onDismiss = { viewModel.dismissGccDialog() }
        )
    }

    if (showBillDialog) {
        CarrierBillDialog(
            selectedSim = activeSim,
            phoneNumber = userProfile?.phoneNumber ?: "+973 33678882",
            balance = userProfile?.accountBalance ?: 25.500,
            isGccActive = userProfile?.gccRoamingActive == true,
            gccCountry = userProfile?.gccCountry ?: "Saudi Arabia",
            onDismiss = { viewModel.dismissBillDialog() }
        )
    }

    if (showRoamingManagerDialog) {
        RoamingManagerDialog(
            isGccActive = userProfile?.gccRoamingActive == true,
            currentCountry = userProfile?.gccCountry ?: "Saudi Arabia",
            selectedSim = activeSim,
            onToggleRoaming = { active, country -> viewModel.toggleGccRoaming(active, country) },
            onDismiss = { showRoamingManagerDialog = false }
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Default.Dialpad, contentDescription = "Dialer") },
                    label = { Text("Dialer", fontSize = 10.sp) },
                    modifier = Modifier.testTag("tab_dialer")
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Default.Business, contentDescription = "Directory") },
                    label = { Text("Directory", fontSize = 10.sp) },
                    modifier = Modifier.testTag("tab_directory")
                )
                NavigationBarItem(
                    selected = selectedTab == 6,
                    onClick = { selectedTab = 6 },
                    icon = { Icon(Icons.Default.History, contentDescription = "Call History") },
                    label = { Text("History", fontSize = 10.sp) },
                    modifier = Modifier.testTag("tab_history")
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Icon(Icons.Default.Map, contentDescription = "Bahrain Map") },
                    label = { Text("Map 🇧🇭", fontSize = 10.sp) },
                    modifier = Modifier.testTag("tab_map")
                )
                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    icon = { Icon(Icons.Default.ShoppingBag, contentDescription = "Talabat Food & Delivery") },
                    label = { Text("Talabat 🛵", fontSize = 10.sp) },
                    modifier = Modifier.testTag("tab_orders")
                )
                NavigationBarItem(
                    selected = selectedTab == 4,
                    onClick = { selectedTab = 4 },
                    icon = { Icon(Icons.Default.Payment, contentDescription = "BenefitPay") },
                    label = { Text("BenefitPay", fontSize = 10.sp) },
                    modifier = Modifier.testTag("tab_benefitpay")
                )
                NavigationBarItem(
                    selected = selectedTab == 5,
                    onClick = { selectedTab = 5 },
                    icon = { Icon(Icons.Default.Security, contentDescription = "Bahrain Browser") },
                    label = { Text("Safe Browser", fontSize = 10.sp) },
                    modifier = Modifier.testTag("tab_browser")
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Header Bar
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 4.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(BahrainRed),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Videocam, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("Bahrain Meet 🇧🇭", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text(
                                userProfile?.phoneNumber?.ifBlank { "Registered (+973)" } ?: "+973 Bahrain",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.clickable { selectedTab = 7 }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.SimCard, contentDescription = null, tint = BahrainRed, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(activeSim, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = MaterialTheme.colorScheme.onPrimaryContainer)
                            }
                        }

                        Spacer(modifier = Modifier.width(4.dp))

                        // 3-Dots Overflow Menu
                        Box {
                            IconButton(
                                onClick = { showMenu = true },
                                modifier = Modifier.testTag("three_dots_menu_button")
                            ) {
                                Icon(Icons.Default.MoreVert, contentDescription = "More options")
                            }

                            DropdownMenu(
                                expanded = showMenu,
                                onDismissRequest = { showMenu = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("🔔 Smart Doorbell Camera") },
                                    onClick = {
                                        showMenu = false
                                        showDoorbellDialog = true
                                    },
                                    leadingIcon = { Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = BahrainRed) },
                                    modifier = Modifier.testTag("menu_doorbell")
                                )
                                DropdownMenuItem(
                                    text = { Text("💬 WhatsApp Bahrain") },
                                    onClick = {
                                        showMenu = false
                                        showWhatsAppDialog = true
                                    },
                                    leadingIcon = { Icon(Icons.Default.Call, contentDescription = null, tint = CallAcceptGreen) },
                                    modifier = Modifier.testTag("menu_whatsapp")
                                )
                                DropdownMenuItem(
                                    text = { Text("📻 Bahrain Radio & Saved Media") },
                                    onClick = {
                                        showMenu = false
                                        viewModel.openRadioHub()
                                    },
                                    leadingIcon = { Icon(Icons.Default.Radio, contentDescription = null, tint = BahrainRed) },
                                    modifier = Modifier.testTag("menu_radio_hub")
                                )
                                DropdownMenuItem(
                                    text = { Text("💳 Carrier Bill Check (#122*)") },
                                    onClick = {
                                        showMenu = false
                                        viewModel.openBillDialog()
                                    },
                                    leadingIcon = { Icon(Icons.Default.SimCard, contentDescription = null, tint = CallAcceptGreen) },
                                    modifier = Modifier.testTag("menu_bill_check")
                                )
                                DropdownMenuItem(
                                    text = { Text("🌐 GCC Roaming Toggle (#973*)") },
                                    onClick = {
                                        showMenu = false
                                        viewModel.openGccDialog()
                                    },
                                    leadingIcon = { Icon(Icons.Default.CellTower, contentDescription = null, tint = MeetGoogleBlue) },
                                    modifier = Modifier.testTag("menu_gcc_roaming")
                                )
                                DropdownMenuItem(
                                    text = { Text("⚙️ Roaming Manager Settings") },
                                    onClick = {
                                        showMenu = false
                                        showRoamingManagerDialog = true
                                    },
                                    leadingIcon = { Icon(Icons.Default.Settings, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                                    modifier = Modifier.testTag("menu_roaming_manager_settings")
                                )
                            }
                        }
                    }
                }
            }

            // Live Timeline, Time, Date & Weather Bar
            TimelineWeatherCard(
                onSpeakTimeWeather = { viewModel.speakCurrentTimelineWeather() }
            )

            // Quick Hotlines Bar
            QuickHotlinesRow(
                onDialClick = { number ->
                    viewModel.updateDialInput(number)
                    selectedTab = 0
                },
                onUssdGcc = { viewModel.openGccDialog() },
                onUssdBill = { viewModel.openBillDialog() },
                onOpenRadio = { viewModel.openRadioHub() }
            )

            // Main Tab Content
            when (selectedTab) {
                0 -> {
                    Column(modifier = Modifier.fillMaxSize().padding(12.dp)) {
                        SimCardSelector(
                            selectedSim = activeSim,
                            onSimSelected = { viewModel.selectSimCarrier(it) }
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        CallDialpad(
                            dialInput = dialInput,
                            onInputChange = { viewModel.updateDialInput(it) },
                            onDigitClick = { viewModel.appendDialDigit(it) },
                            onBackspaceClick = { viewModel.deleteLastDialChar() },
                            onStartCall = { isVideo -> viewModel.initiateCall(dialInput, isVideo) },
                            onClearClick = { viewModel.clearDialInput() },
                            recentLogs = callLogs,
                            onSelectHistoryTab = { selectedTab = 6 }
                        )
                    }
                }
                1 -> DirectoryScreen(viewModel = viewModel, onStartCall = { target, isVideo -> viewModel.initiateCall(target, isVideo) })
                2 -> BahrainMapScreen(
                    onStartCall = { target -> viewModel.initiateCall(target, false) },
                    onOrderTelepod = { selectedTab = 3 }
                )
                3 -> TelepodOrderScreen(
                    onOpenBenefitPay = { selectedTab = 4 },
                    onStartCall = { target, isVideo -> viewModel.initiateCall(target, isVideo) }
                )
                4 -> BenefitPayScreen()
                5 -> BahrainBrowserScreen(
                    onStartCall = { target, isVideo -> viewModel.initiateCall(target, isVideo) }
                )
                6 -> HistoryScreen(viewModel = viewModel, onRedial = { target, isVideo -> viewModel.initiateCall(target, isVideo) })
                7 -> SimManagerScreen(viewModel = viewModel, onStartCall = { target, isVideo -> viewModel.initiateCall(target, isVideo) })
            }
        }
    }
}

@Composable
fun QuickHotlinesRow(
    onDialClick: (String) -> Unit,
    onUssdGcc: () -> Unit,
    onUssdBill: () -> Unit,
    onOpenRadio: () -> Unit
) {
    val quickItems = listOf(
        Triple("📻 Bahrain Radio", "RADIO", BahrainRed),
        Triple("🚨 999 Emergency", "999", BahrainRed),
        Triple("🚑 199 Traffic", "199", BahrainRed),
        Triple("🚒 299 Civil Def.", "299", BahrainRed),
        Triple("🇸🇦 141 Arabic (Nicole)", "141", MeetGoogleBlue),
        Triple("🇬🇧 140 English (Nicole)", "140", MeetGoogleBlue),
        Triple("🌍 181 Intl Directory", "181", MeetGoogleBlue),
        Triple("✈️ Airport BIA", "33678882", CallAcceptGreen),
        Triple("🧟 666 Zombie", "666", Color(0xFF4CAF50)),
        Triple("🌐 #973* GCC Roam", "#973*", MeetGoogleBlue),
        Triple("💳 #122* Bill Check", "#122*", CallAcceptGreen)
    )

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(0.3f))
            .padding(vertical = 8.dp, horizontal = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(quickItems) { (label, code, color) ->
            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable {
                        if (code == "RADIO") onOpenRadio()
                        else if (code == "#973*") onUssdGcc()
                        else if (code == "#122*") onUssdBill()
                        else onDialClick(code)
                    },
                color = color.copy(alpha = 0.15f)
            ) {
                Text(
                    text = label,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = color,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }
        }
    }
}

@Composable
fun TimelineWeatherCard(
    onSpeakTimeWeather: () -> Unit
) {
    val bhTz = java.util.TimeZone.getTimeZone("Asia/Bahrain")
    val timeSdf = java.text.SimpleDateFormat("h:mm:ss a", java.util.Locale.ENGLISH).apply { timeZone = bhTz }
    val dateSdf = java.text.SimpleDateFormat("EEEE, MMMM d, yyyy", java.util.Locale.ENGLISH).apply { timeZone = bhTz }
    val now = java.util.Date()
    val timeStr = timeSdf.format(now)
    val dateStr = dateSdf.format(now)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSpeakTimeWeather() }
            .testTag("timeline_weather_banner"),
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "🕒 $timeStr",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = dateStr,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = "🇧🇭 Manama, Bahrain: 34°C Clear • NW 16 km/h • Humidity 48%",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Surface(
                color = CallAcceptGreen.copy(0.2f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "🔊 Speak Time",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = CallAcceptGreen,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}


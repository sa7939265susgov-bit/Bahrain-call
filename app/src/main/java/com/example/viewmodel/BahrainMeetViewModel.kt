package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.BahrainMeetRepository
import com.example.data.CallLogEntry
import com.example.data.DirectoryContact
import com.example.data.MediaStream
import com.example.data.UserProfile
import com.example.util.TextToSpeechHelper
import com.example.util.ToneRingtoneHelper
import kotlinx.coroutines.delay

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

data class InCallMessage(
    val sender: String,
    val text: String,
    val timestamp: String
)

data class ActiveCall(
    val numberOrEmail: String,
    val contactName: String,
    val isVideo: Boolean,
    val carrier: String,
    val startTime: Long = System.currentTimeMillis(),
    val responseSpeech: String,
    val isArabicText: Boolean = false,
    val isEmergency: Boolean = false,
    val isZombie: Boolean = false,
    val carrierError: String? = null,
    val isGccPrompt: Boolean = false,
    val gccCountry: String = "Saudi Arabia",
    val isRinging: Boolean = false,
    val ringRepetition: Int = 1,
    val totalRings: Int = 2,
    val isConnected: Boolean = false,
    val lastPressedKey: String? = null,
    val chatMessages: List<InCallMessage> = emptyList(),
    val conferenceNumbers: List<String> = emptyList(),
    val isConference: Boolean = false
)

class BahrainMeetViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val repository = BahrainMeetRepository(db.appDao())
    val ttsHelper = TextToSpeechHelper(application)
    val toneHelper = ToneRingtoneHelper()

    val userProfile: StateFlow<UserProfile?> = repository.userProfile.stateInScope(null)
    val callLogs: StateFlow<List<CallLogEntry>> = repository.callLogs.stateInScope(emptyList())
    val directoryContacts: StateFlow<List<DirectoryContact>> = repository.directoryContacts.stateInScope(emptyList())
    val mediaStreams: StateFlow<List<MediaStream>> = repository.mediaStreams.stateInScope(emptyList())

    private val _dialInput = MutableStateFlow("")
    val dialInput: StateFlow<String> = _dialInput.asStateFlow()

    private val _activeCall = MutableStateFlow<ActiveCall?>(null)
    val activeCall: StateFlow<ActiveCall?> = _activeCall.asStateFlow()

    private val _showGccDialog = MutableStateFlow(false)
    val showGccDialog: StateFlow<Boolean> = _showGccDialog.asStateFlow()

    private val _showBillDialog = MutableStateFlow(false)
    val showBillDialog: StateFlow<Boolean> = _showBillDialog.asStateFlow()

    private val _showRadioHub = MutableStateFlow(false)
    val showRadioHub: StateFlow<Boolean> = _showRadioHub.asStateFlow()


    // Registration state
    private val _registrationPhone = MutableStateFlow("")
    val registrationPhone: StateFlow<String> = _registrationPhone.asStateFlow()

    private val _registrationName = MutableStateFlow("")
    val registrationName: StateFlow<String> = _registrationName.asStateFlow()

    private val _sentOtpCode = MutableStateFlow("")
    val sentOtpCode: StateFlow<String> = _sentOtpCode.asStateFlow()

    private val _enteredOtp = MutableStateFlow("")
    val enteredOtp: StateFlow<String> = _enteredOtp.asStateFlow()

    private val _otpMessage = MutableStateFlow<String?>(null)
    val otpMessage: StateFlow<String?> = _otpMessage.asStateFlow()

    private val _isOtpSent = MutableStateFlow(false)
    val isOtpSent: StateFlow<Boolean> = _isOtpSent.asStateFlow()

    init {
        viewModelScope.launch {
            repository.ensureInitialData()
        }
    }

    private fun <T> kotlinx.coroutines.flow.Flow<T>.stateInScope(initialValue: T): StateFlow<T> {
        val flowState = MutableStateFlow(initialValue)
        viewModelScope.launch {
            this@stateInScope.collect { flowState.value = it }
        }
        return flowState.asStateFlow()
    }

    fun updateDialInput(text: String) {
        _dialInput.value = text
    }

    fun appendDialDigit(digit: String) {
        _dialInput.value += digit
    }

    fun clearDialInput() {
        _dialInput.value = ""
    }

    fun deleteLastDialChar() {
        if (_dialInput.value.isNotEmpty()) {
            _dialInput.value = _dialInput.value.dropLast(1)
        }
    }

    fun setRegistrationDetails(phone: String, name: String) {
        _registrationPhone.value = phone
        _registrationName.value = name
    }

    fun sendRegistrationSms() {
        val phone = _registrationPhone.value.trim()
        if (phone.isEmpty()) {
            _otpMessage.value = "Please enter a valid Bahrain phone number."
            return
        }
        val randomCode = (100000..999999).random().toString()
        _sentOtpCode.value = randomCode
        _isOtpSent.value = true
        _otpMessage.value = "SMS code sent to $phone: [$randomCode]"
    }

    fun updateEnteredOtp(otp: String) {
        _enteredOtp.value = otp
    }

    fun verifySmsCodeAndLogin(): Boolean {
        if (_enteredOtp.value.trim() == _sentOtpCode.value && _sentOtpCode.value.isNotEmpty()) {
            viewModelScope.launch {
                val current = userProfile.value ?: UserProfile()
                val updated = current.copy(
                    phoneNumber = if (_registrationPhone.value.isNotBlank()) _registrationPhone.value else "+973 33678882",
                    userName = if (_registrationName.value.isNotBlank()) _registrationName.value else "Bahrain Resident",
                    isVerified = true
                )
                repository.saveProfile(updated)
            }
            _otpMessage.value = null
            return true
        } else {
            _otpMessage.value = "Invalid SMS code. Please check code $sentOtpCode."
            return false
        }
    }

    fun selectSimCarrier(carrier: String) { // "BATELCO", "STC", "ZAIN"
        viewModelScope.launch {
            val current = userProfile.value ?: UserProfile()
            repository.saveProfile(current.copy(selectedSim = carrier))
        }
    }

    fun toggleGccRoaming(active: Boolean, country: String = "Saudi Arabia") {
        viewModelScope.launch {
            val current = userProfile.value ?: UserProfile()
            repository.saveProfile(current.copy(gccRoamingActive = active, gccCountry = country))
        }
    }

    fun openGccDialog() { _showGccDialog.value = true }
    fun dismissGccDialog() { _showGccDialog.value = false }

    fun openBillDialog() { _showBillDialog.value = true }
    fun dismissBillDialog() { _showBillDialog.value = false }

    fun clearCallLogs() {
        viewModelScope.launch {
            repository.clearLogs()
        }
    }

    fun initiateCall(rawTarget: String, isVideo: Boolean, isIncoming: Boolean = false) {
        val target = rawTarget.trim()
        if (target.isEmpty()) return

        val cleanNum = target.replace(Regex("[^0-9#*]"), "")

        // Handle USSD Codes immediately
        if (target == "#973*" || cleanNum == "#973*" || target.contains("#973*")) {
            _showGccDialog.value = true
            _dialInput.value = ""
            return
        }

        if (target == "#122*" || cleanNum == "#122*" || target.contains("#122*")) {
            _showBillDialog.value = true
            _dialInput.value = ""
            return
        }

        val profile = userProfile.value
        val isGccActive = profile?.gccRoamingActive == true
        val gccCountry = profile?.gccCountry ?: "Saudi Arabia"

        val activeSim = profile?.selectedSim ?: "BATELCO"
        val carrierName = when(activeSim) {
            "STC" -> "STC Bahrain"
            "ZAIN" -> "Zain Bahrain"
            else -> "Batelco"
        }

        // Check if contact matches known directory contact
        val matchedContact = directoryContacts.value.find { 
            it.phoneNumber == cleanNum || it.email.equals(target, ignoreCase = true) || it.name.contains(target, ignoreCase = true)
        }
        var contactName = matchedContact?.name ?: target

        val currentTimeArabic = getCurrentArabicTime()
        val currentTimeEnglish = getCurrentEnglishTime()

        var isEmergency = false
        var isZombie = false
        var isArabic = false
        var carrierError: String? = null
        var speechResponse = ""

        // Check for GCC Roaming call prompt if roaming is active and not emergency
        val isEmergencyNumber = cleanNum in listOf("999", "199", "299")
        if (isGccActive && !isEmergencyNumber) {
            speechResponse = "GCC Roaming is ACTIVE in $gccCountry. Calling $contactName ($target). Do you want to shut off roaming and switch to Bahrain local network?"
            val call = ActiveCall(
                numberOrEmail = target,
                contactName = contactName,
                isVideo = isVideo,
                carrier = carrierName,
                responseSpeech = speechResponse,
                isGccPrompt = true,
                gccCountry = gccCountry
            )
            _activeCall.value = call
            _dialInput.value = ""
            ttsHelper.speak(speechResponse)
            return
        }

        // Foreign and local emergency numbers check
        if (cleanNum in listOf("911", "112", "000", "990", "110", "998", "997")) {
            contactName = "Foreign Emergency ($cleanNum)"
            speechResponse = "Emergency call $cleanNum failed to connect directly via local network because this is a Bahrain local app. Press 1 for US (+1), 2 for UK (+44), 3 for Saudi Arabia (+966), or press 0 for International Operator."
            isEmergency = true
        }

        when {
            isEmergency -> { /* Handled above */ }
            cleanNum == "999" -> {
                contactName = "999 Emergency Services"
                speechResponse = "Bahrain Emergency Services 999. Press 1 for English, اضغط ٢ للغة العربية."
                isEmergency = true
            }
            cleanNum == "199" -> {
                contactName = "199 Traffic & Accident Police"
                speechResponse = "Bahrain Traffic Police 199. Press 1 for English, اضغط ٢ للغة العربية."
                isEmergency = true
            }
            cleanNum == "299" -> {
                contactName = "299 Civil Defense Hotline"
                speechResponse = "Bahrain Civil Defense Hotline. Press 1 for English, اضغط ٢ للغة العربية."
                isEmergency = true
            }
            cleanNum == "39123456" || target.lowercase().contains("talabat") || target.lowercase().contains("rider") -> {
                contactName = "Talabat Rider Ahmed"
                speechResponse = "Hello! Your order is here! Please pick it up, I am near your location in Bahrain."
            }
            cleanNum == "17796000" || target.lowercase().contains("causeway") || target.lowercase().contains("king fahd") || target.lowercase().contains("king fact") || target.lowercase().contains("passport island") -> {
                contactName = "King Fahd Causeway Authority (+973 17796000)"
                speechResponse = "Welcome to King Fahd Causeway Official Border Hotline (+973 17796000). Border passport control traffic is moving smoothly with 15 minutes average wait time. Press 1 for English, اضغط ٢ للغة العربية."
            }
            cleanNum == "17214433" || target.lowercase().contains("manama") -> {
                contactName = "Manama Capital & Financial Harbour (+973 17214433)"
                speechResponse = "Welcome to Manama Capital & Financial Harbour. Press 1 for English, اضغط ٢ للغة العربية."
            }
            cleanNum == "17177771" || target.lowercase().contains("seef") -> {
                contactName = "Seef District & City Centre Mall (+973 17177771)"
                speechResponse = "Welcome to Seef District & City Centre Mall Bahrain. Press 1 for English, اضغط ٢ للغة العربية."
            }
            cleanNum == "17770077" || target.lowercase().contains("riffa") -> {
                contactName = "Riffa Clock Tower & Jasmi's HQ (+973 17770077)"
                speechResponse = "Welcome to Riffa Town Hotline and Jasmi's HQ. Press 1 for English, اضغط ٢ للغة العربية."
            }
            cleanNum == "17450000" || target.lowercase().contains("sitra") -> {
                contactName = "Sitra Industrial Port & BAPCO (+973 17450000)"
                speechResponse = "Welcome to Sitra Port & BAPCO Energy Headquarters. Press 1 for English, اضغط ٢ للغة العربية."
            }
            cleanNum == "17845100" || target.lowercase().contains("zallaq") -> {
                contactName = "Zallaq Resort & Sakhir Circuit (+973 17845100)"
                speechResponse = "Welcome to Zallaq Beach Resort & Sakhir F1 Circuit. Press 1 for English, اضغط ٢ للغة العربية."
            }
            cleanNum == "17590000" || target.lowercase().contains("durrat") -> {
                contactName = "Durrat Al Bahrain Resort (+973 17590000)"
                speechResponse = "Welcome to Durrat Al Bahrain Resort Islands. Press 1 for English, اضغط ٢ للغة العربية."
            }
            cleanNum == "141" -> {
                contactName = "141 Arabic Time & Weather (Nicole)"
                speechResponse = getCurrentArabicTimeDateWeather()
                isArabic = true
            }
            cleanNum == "140" -> {
                contactName = "140 English Time & Weather (Nicole)"
                speechResponse = getCurrentEnglishTimeDateWeather()
            }
            cleanNum == "181" -> {
                contactName = "181 International Directory Assistance"
                speechResponse = "Welcome to 181 International Directory Assistance! Press 1 for Japan (+81), 2 for South Korea (+82), 3 for United States (+1), 4 for United Kingdom (+44), 5 for Saudi Arabia (+966), 6 for UAE (+971), 7 for Germany (+49), 8 for France (+33), 9 for India (+91), or 0 for US Hotline. Select a number for anywhere in the world!"
            }
            cleanNum == "1" || target.startsWith("+1") || cleanNum.startsWith("001") || (cleanNum.startsWith("1") && cleanNum.length >= 10) || target.lowercase().contains("usa") || target.lowercase().contains("america") || target.lowercase().contains("united states") -> {
                contactName = "New York 5th Ave Store (USA +1)"
                speechResponse = "Connecting international call to New York Fifth Avenue Store, United States. Direct phone number is +1-212-555-0199. ${getUSTimeDateWeather()} Hello! Welcome to New York, United States."
            }
            target.startsWith("+81") || cleanNum.startsWith("0081") || (cleanNum.startsWith("81") && cleanNum.length >= 10) || target.lowercase().contains("japan") || target.lowercase().contains("tokyo") -> {
                contactName = "Tokyo Ginza Store (Japan +81)"
                speechResponse = "Connecting international call to Tokyo Ginza Department Store, Japan. Direct phone number is +81-3-3573-2371. ${getJapanTimeDateWeather()} Konnichiwa! Welcome to Ginza Tokyo."
            }
            target.startsWith("+82") || cleanNum.startsWith("0082") || (cleanNum.startsWith("82") && cleanNum.length >= 10) || target.lowercase().contains("korea") || target.lowercase().contains("seoul") -> {
                contactName = "Seoul Gangnam Tech Mall (Korea +82)"
                speechResponse = "Connecting international call to Seoul Gangnam Store, South Korea. Direct phone number is +82-2-540-1234. ${getKoreaTimeDateWeather()} Annyeonghaseyo! Welcome to Seoul Gangnam."
            }
            target.startsWith("+91") || cleanNum.startsWith("0091") || (cleanNum.startsWith("91") && cleanNum.length >= 10) || target.lowercase().contains("india") || target.lowercase().contains("mumbai") || target.lowercase().contains("delhi") -> {
                contactName = "India Line ($target)"
                speechResponse = "Connecting international call to India ($target). ${getIndiaTimeDateWeather()} Namaste! You are connected to India. Press 1 for English/Hindi, 2 for Hindi, 3 for Mumbai, 4 for Delhi, 5 for Bangalore."
            }
            target.startsWith("+44") || cleanNum.startsWith("0044") || (cleanNum.startsWith("44") && cleanNum.length >= 11) || target.lowercase().contains("uk") || target.lowercase().contains("london") -> {
                contactName = "London Harrods Gallery (UK +44)"
                speechResponse = "Connecting international call to London Harrods Store, United Kingdom. Direct phone number is +44-20-7730-1234. ${getUKTimeDateWeather()} Good day! Welcome to London, United Kingdom."
            }
            target.startsWith("+61") || cleanNum.startsWith("0061") || (cleanNum.startsWith("61") && cleanNum.length >= 10) || target.lowercase().contains("australia") || target.lowercase().contains("sydney") -> {
                contactName = "Sydney Harbour Store (Australia +61)"
                speechResponse = "Connecting international call to Sydney Store, Australia. Direct phone number is +61-2-9000-1234. ${getAustraliaTimeDateWeather()} G'day! Welcome to Sydney, Australia."
            }
            target.startsWith("+20") || cleanNum.startsWith("0020") || (cleanNum.startsWith("20") && cleanNum.length >= 10) || target.lowercase().contains("egypt") || target.lowercase().contains("cairo") -> {
                contactName = "Cairo Mall Store (Egypt +20)"
                speechResponse = "Connecting international call to Cairo Store, Egypt. Direct phone number is +20-2-2700-1234. ${getEgyptTimeDateWeather()} Ahlan! Welcome to Cairo, Egypt."
            }
            target.startsWith("+966") || cleanNum.startsWith("00966") || (cleanNum.startsWith("966") && cleanNum.length >= 10) || target.lowercase().contains("saudi") || target.lowercase().contains("riyadh") -> {
                contactName = "Riyadh Boulevard Mall (Saudi Arabia +966)"
                speechResponse = "Connecting international call to Riyadh Boulevard Store, Saudi Arabia. Direct phone number is +966-11-200-1111. ${getSaudiTimeDateWeather()} Welcome to Riyadh, Saudi Arabia."
            }
            target.startsWith("+971") || cleanNum.startsWith("00971") || (cleanNum.startsWith("971") && cleanNum.length >= 10) || target.lowercase().contains("dubai") || target.lowercase().contains("uae") -> {
                contactName = "Dubai Mall Mega Store (UAE +971)"
                speechResponse = "Connecting international call to Dubai Mall Mega Store, United Arab Emirates. Direct phone number is +971-4-362-7500. ${getUAETimeDateWeather()} Welcome to Dubai, United Arab Emirates."
            }
            target.startsWith("+965") || cleanNum.startsWith("00965") || (cleanNum.startsWith("965") && cleanNum.length >= 9) || target.lowercase().contains("kuwait") -> {
                contactName = "Kuwait City Grand Mall (Kuwait +965)"
                speechResponse = "Connecting international call to Kuwait City Grand Mall, Kuwait (+965-2200-0000). Ahlan wa Sahlan! Welcome to Kuwait."
            }
            target.startsWith("+974") || cleanNum.startsWith("00974") || (cleanNum.startsWith("974") && cleanNum.length >= 9) || target.lowercase().contains("qatar") -> {
                contactName = "Doha Corniche Center (Qatar +974)"
                speechResponse = "Connecting international call to Doha Corniche Center, Qatar (+974-4400-0000). Marhaba! Welcome to Doha, Qatar."
            }
            target.startsWith("+968") || cleanNum.startsWith("00968") || (cleanNum.startsWith("968") && cleanNum.length >= 9) || target.lowercase().contains("oman") -> {
                contactName = "Muscat Sultan Center (Oman +968)"
                speechResponse = "Connecting international call to Muscat Sultan Center, Oman (+968-2400-0000). Ahlan! Welcome to Muscat, Oman."
            }
            target.startsWith("+63") || cleanNum.startsWith("0063") || (cleanNum.startsWith("63") && cleanNum.length >= 10) || target.lowercase().contains("philippines") || target.lowercase().contains("manila") -> {
                contactName = "Manila Bay Center (Philippines +63)"
                speechResponse = "Connecting international call to Manila Bay Center, Philippines (+63-2-8000-0000). Mabuhay! Welcome to Manila, Philippines."
            }
            target.startsWith("+92") || cleanNum.startsWith("0092") || (cleanNum.startsWith("92") && cleanNum.length >= 10) || target.lowercase().contains("pakistan") || target.lowercase().contains("karachi") || target.lowercase().contains("islamabad") -> {
                contactName = "Islamabad Executive Center (Pakistan +92)"
                speechResponse = "Connecting international call to Islamabad Executive Center, Pakistan (+92-51-111-000-000). Assalamu Alaikum! Welcome to Pakistan."
            }
            target.startsWith("+") || cleanNum.startsWith("00") || (cleanNum.length >= 7 && !cleanNum.startsWith("17") && !cleanNum.startsWith("33") && !cleanNum.startsWith("36") && !cleanNum.startsWith("39") && !cleanNum.startsWith("66")) -> {
                contactName = "International Line ($target)"
                speechResponse = "Connecting international call to $target via $carrierName. ${getUSTimeDateWeather()} Hello! Welcome to $target."
            }
            cleanNum == "666" -> {
                contactName = "666 Zombie Hotline"
                speechResponse = "Braaaaiiinssss! You have reached the Zombie Hotline 666... Press 1 for English, اضغط ٢ للغة العربية."
                isZombie = true
            }
            cleanNum == "33678882" || target.lowercase().contains("muharraq") || target.lowercase().contains("airport") -> {
                contactName = "Bahrain International Airport (BIA)"
                speechResponse = "Welcome to Bahrain International Airport. Press 1 for English. للغة العربية اضغط ٢."
            }
            cleanNum == "196" || target.lowercase().contains("batelco support") -> {
                contactName = "Batelco Support (196)"
                if (activeSim == "BATELCO") {
                    speechResponse = "Welcome to Batelco Support Line 196. Press 1 for English. للغة العربية اضغط ٢."
                } else {
                    speechResponse = "Batelco Support line 196 can only be called using a Batelco SIM card. Your current active SIM is $carrierName ($activeSim). Please select Batelco SIM in SIM Manager."
                    carrierError = "SIM_MISMATCH"
                }
            }
            cleanNum == "119" || target.lowercase().contains("stc support") -> {
                contactName = "STC Support (119)"
                if (activeSim == "STC") {
                    speechResponse = "Welcome to STC Bahrain Support 119. Press 1 for English. للغة العربية اضغط ٢."
                } else {
                    speechResponse = "STC Support line 119 can only be called using an STC SIM card. Your current active SIM is $carrierName ($activeSim). Please select STC SIM in SIM Manager."
                    carrierError = "SIM_MISMATCH"
                }
            }
            cleanNum == "195" || target.lowercase().contains("zain support") -> {
                contactName = "Zain Support (195)"
                if (activeSim == "ZAIN") {
                    speechResponse = "Welcome to Zain Bahrain Support 195. Press 1 for English. للغة العربية اضغط ٢."
                } else {
                    speechResponse = "Zain Support line 195 can only be called using a Zain SIM card. Your current active SIM is $carrierName ($activeSim). Please select Zain SIM in SIM Manager."
                    carrierError = "SIM_MISMATCH"
                }
            }
            target.lowercase().contains("city center") || target.lowercase().contains("city centre") -> {
                contactName = "City Centre Bahrain"
                speechResponse = "Welcome to City Centre Bahrain. Press 1 for English. للغة العربية اضغط ٢."
            }
            target.lowercase().contains("kfc") -> {
                contactName = "KFC Bahrain"
                speechResponse = "Welcome to KFC Bahrain. Press 1 for English. للغة العربية اضغط ٢."
            }
            target.lowercase().contains("talabat") || target.lowercase().contains("rider") || cleanNum == "39123456" -> {
                contactName = "Talabat Rider Ahmed (+973 39123456)"
                speechResponse = "Assalamu Alaikum! السلام عليكم! This is Ahmed, your Talabat Rider. I am near to your house at Building 12, Road 2801! Please come outside to receive your hot meal from Talabat Express. Shukran!"
                isArabic = true
            }
            matchedContact != null -> {
                speechResponse = "Welcome to $contactName. Press 1 for English. للغة العربية اضغط ٢."
            }
            else -> {
                // Connect ANY custom number or name dialed without error
                contactName = if (target.any { it.isLetter() }) target else "Contact ($target)"
                speechResponse = "Connecting call to $target via $carrierName network."
            }
        }

        val call = ActiveCall(
            numberOrEmail = target,
            contactName = contactName,
            isVideo = isVideo,
            carrier = carrierName,
            responseSpeech = speechResponse,
            isArabicText = isArabic,
            isEmergency = isEmergency,
            isZombie = isZombie,
            carrierError = carrierError,
            isRinging = !isIncoming,
            ringRepetition = 1,
            totalRings = 2,
            isConnected = isIncoming
        )

        _activeCall.value = call
        _dialInput.value = ""

        if (!isIncoming) {
            // For all outgoing calls: Use specific Bahraini ringtone sound, repeating twice before connecting
            toneHelper.playBahrainiOutgoingRingtone(
                repeats = 2,
                onRingChange = { currentRing, totalRings ->
                    _activeCall.value = _activeCall.value?.copy(
                        isRinging = true,
                        isConnected = false,
                        ringRepetition = currentRing,
                        totalRings = totalRings
                    )
                },
                onConnected = {
                    _activeCall.value = _activeCall.value?.copy(
                        isRinging = false,
                        isConnected = true
                    )
                    ttsHelper.speak(speechResponse, isArabic = isArabic)
                }
            )
        } else {
            ttsHelper.speak(speechResponse, isArabic = isArabic)
        }

        viewModelScope.launch {
            repository.addCallLog(
                CallLogEntry(
                    numberOrEmail = target,
                    contactName = contactName,
                    callType = if (isVideo) "VIDEO" else "AUDIO",
                    carrierUsed = carrierName,
                    durationSeconds = (5..120).random(),
                    isEmergency = isEmergency,
                    direction = if (isIncoming) "INCOMING" else "OUTGOING"
                )
            )
        }
    }

    fun confirmGccSwitchLocal() {
        val currentCall = _activeCall.value ?: return
        viewModelScope.launch {
            val current = userProfile.value ?: UserProfile()
            repository.saveProfile(current.copy(gccRoamingActive = false))
        }

        val updatedSpeech = "*SIM Signal Stopped... Switching off GCC Roaming... Signal Restored on Bahrain Local Network!* Hello! Connected to ${currentCall.contactName} on local line. Press 1 to speak."
        val updatedCall = currentCall.copy(
            isGccPrompt = false,
            responseSpeech = updatedSpeech,
            lastPressedKey = "YES",
            isRinging = true,
            isConnected = false,
            ringRepetition = 1,
            totalRings = 2
        )
        _activeCall.value = updatedCall
        toneHelper.playBahrainiOutgoingRingtone(
            repeats = 2,
            onRingChange = { currentRing, totalRings ->
                _activeCall.value = _activeCall.value?.copy(
                    isRinging = true,
                    isConnected = false,
                    ringRepetition = currentRing,
                    totalRings = totalRings
                )
            },
            onConnected = {
                _activeCall.value = _activeCall.value?.copy(
                    isRinging = false,
                    isConnected = true
                )
                ttsHelper.speak(updatedSpeech)
            }
        )
    }

    fun continueGccRoamingCall() {
        val currentCall = _activeCall.value ?: return
        val updatedSpeech = "Continuing call to ${currentCall.contactName} via GCC Roaming Callback (${currentCall.gccCountry}). Connected! Press 1 to speak."
        val updatedCall = currentCall.copy(
            isGccPrompt = false,
            responseSpeech = updatedSpeech,
            lastPressedKey = "NO",
            isRinging = true,
            isConnected = false,
            ringRepetition = 1,
            totalRings = 2
        )
        _activeCall.value = updatedCall
        toneHelper.playBahrainiOutgoingRingtone(
            repeats = 2,
            onRingChange = { currentRing, totalRings ->
                _activeCall.value = _activeCall.value?.copy(
                    isRinging = true,
                    isConnected = false,
                    ringRepetition = currentRing,
                    totalRings = totalRings
                )
            },
            onConnected = {
                _activeCall.value = _activeCall.value?.copy(
                    isRinging = false,
                    isConnected = true
                )
                ttsHelper.speak(updatedSpeech)
            }
        )
    }

    fun pressIvrKey(digit: String) {
        val currentCall = _activeCall.value ?: return

        var newSpeech = ""
        var isArabic = false

        val isIndiaCall = currentCall.contactName.contains("India", ignoreCase = true) ||
                currentCall.numberOrEmail.startsWith("+91") ||
                currentCall.numberOrEmail.startsWith("0091") ||
                currentCall.numberOrEmail.startsWith("91")

        val is181 = (currentCall.numberOrEmail == "181" || currentCall.contactName.startsWith("181")) && !isIndiaCall

        if (isIndiaCall) {
            when (digit) {
                "1" -> newSpeech = "Namaste! Press 1 for English or Hindi support. You are connected to India care desk."
                "2" -> newSpeech = "Namaste! Service department India selected. Representative speaking."
                "3" -> newSpeech = "Connected to Mumbai India branch desk. Namaste, how can I help you today?"
                "4" -> newSpeech = "Connected to Delhi India regional office. Namaste, customer desk speaking! How can I assist you today?"
                "5" -> newSpeech = "Connected to Bangalore India tech support center. Representative speaking."
                "6" -> newSpeech = "Connected to Chennai India customer office. Representative speaking."
                "7" -> newSpeech = "Connected to Kolkata India desk. Representative speaking."
                "8" -> newSpeech = "Connected to Hyderabad India customer desk. Representative speaking."
                "9" -> newSpeech = "Connected to India main headquarters operator desk."
                "0" -> newSpeech = "Namaste! India operator speaking, how can I assist you?"
                else -> newSpeech = "Connected to India call ($digit pressed). Representative speaking."
            }
        } else if (is181) {
            when (digit) {
                "1" -> newSpeech = "Japan Directory (+81):\n" +
                        "1. Tokyo Ginza Store: +81-3-3573-2371\n" +
                        "2. MUFG Bank Tokyo: +81-3-3240-1111\n" +
                        "3. NTT Docomo SIM Telecom: +81-3-5156-1111\n" +
                        "4. Japan Testament Office: +81-3-5321-1111.\n" +
                        "${getJapanTimeDateWeather()} Connecting your international call to Tokyo Japan!"
                "2" -> newSpeech = "South Korea Directory (+82):\n" +
                        "1. Seoul Gangnam Store: +82-2-540-1234\n" +
                        "2. Shinhan Bank Seoul: +82-2-3456-7890\n" +
                        "3. SK Telecom SIM Service: +82-2-6100-2114\n" +
                        "4. Seoul Testament & Justice Dept: +82-2-3480-1114.\n" +
                        "${getKoreaTimeDateWeather()} Connecting your international call to Seoul South Korea!"
                "3" -> newSpeech = "United States Directory (+1):\n" +
                        "1. New York 5th Ave Store: +1-212-555-0199\n" +
                        "2. Bank of America NY: +1-800-432-1000\n" +
                        "3. AT&T & T-Mobile SIM Network: +1-800-331-0500\n" +
                        "4. US Federal Government Testament Office: +1-202-456-1111.\n" +
                        "${getUSTimeDateWeather()} Connecting your international call to New York USA!"
                "4" -> newSpeech = "United Kingdom Directory (+44):\n" +
                        "1. London Harrods Store: +44-20-7730-1234\n" +
                        "2. HSBC Bank London: +44-20-7991-8888\n" +
                        "3. Vodafone UK SIM Network: +44-333-304-0191\n" +
                        "4. Royal Probate & Testament Registry: +44-20-7947-6000.\n" +
                        "${getUKTimeDateWeather()} Connecting your international call to London UK!"
                "5" -> newSpeech = "Saudi Arabia Directory (+966):\n" +
                        "1. Riyadh Boulevard Store: +966-11-200-1111\n" +
                        "2. Al Rajhi Bank Riyadh: +966-920003344\n" +
                        "3. stc Saudi SIM Telecom: +966-11-452-8111\n" +
                        "4. Saudi Ministry & Testament Office: +966-11-405-7777.\n" +
                        "${getSaudiTimeDateWeather()} Connecting your international call to Riyadh Saudi Arabia!"
                "6" -> newSpeech = "United Arab Emirates Directory (+971):\n" +
                        "1. Dubai Mall Mega Store: +971-4-362-7500\n" +
                        "2. Emirates NBD Bank: +971-600-540000\n" +
                        "3. e& / du SIM Telecom Dubai: +971-800-155\n" +
                        "4. Dubai Courts & Testament Department: +971-4-334-7777.\n" +
                        "${getUAETimeDateWeather()} Connecting your international call to Dubai UAE!"
                "7" -> newSpeech = "Germany Directory (+49):\n" +
                        "1. Berlin Store: +49-30-2000-1234\n" +
                        "2. Deutsche Bank: +49-30-3407-0000\n" +
                        "3. Telekom Deutschland SIM: +49-800-330-1000.\n" +
                        "${getGermanyTimeDateWeather()} Connecting your international call to Berlin Germany!"
                "8" -> newSpeech = "France Directory (+33):\n" +
                        "1. Paris Galeries Lafayette Store: +33-1-4282-3456\n" +
                        "2. BNP Paribas Bank: +33-1-4014-4546\n" +
                        "3. Orange France SIM Network: +33-9-6939-3900.\n" +
                        "${getFranceTimeDateWeather()} Connecting your international call to Paris France!"
                "9" -> newSpeech = "India Directory (+91):\n" +
                        "1. Mumbai Reliance Digital Store: +91-22-6000-1234\n" +
                        "2. State Bank of India: +91-1800-1234\n" +
                        "3. Reliance Jio & Airtel SIM Telecom: +91-198.\n" +
                        "${getIndiaTimeDateWeather()} Connecting your international call to Mumbai India!"
                "0" -> newSpeech = "Bahrain & Global Directory Hotline:\n" +
                        "1. Batelco SIM Care: 196\n" +
                        "2. STC Bahrain SIM Care: 119\n" +
                        "3. Zain Bahrain SIM Care: 107\n" +
                        "4. National Bank of Bahrain (NBB): +973-17228888\n" +
                        "5. BBK Bank Bahrain: +973-17207777\n" +
                        "6. Bahrain Ministry of Justice & Testament: +973-17513000."
                else -> newSpeech = "181 Directory: Dial 1 for Japan, 2 for South Korea, 3 for USA, 4 for UK, 5 for Saudi Arabia, 6 for UAE, 7 for Germany, 8 for France, 9 for India, 0 for Stores, Banks, SIMs & Testament offices."
            }
        } else {
            when (digit) {
                "1" -> {
                    // English selection: announce location choices
                    newSpeech = "You selected English. For location in Manama press 1, Muharraq press 2, Riffa press 3, Isa Town press 4, Seef / City Centre press 5."
                    isArabic = false
                }
                "2" -> {
                    // Arabic selection: announce location choices in Arabic
                    newSpeech = "تم اختيار اللغة العربية. للموقع في المنامة اضغط ١، المحرق اضغط ٢، الرفاع اضغط ٣، مدينة عيسى اضغط ٤، السيف اضغط ٥."
                    isArabic = true
                }
                "3" -> {
                    newSpeech = "Location set to Riffa. Connected to ${currentCall.contactName} Riffa branch representative! How can I assist you today?"
                    isArabic = currentCall.isArabicText
                }
                "4" -> {
                    newSpeech = "Location set to Isa Town. Connected to ${currentCall.contactName} Isa Town branch representative! How can I assist you today?"
                    isArabic = currentCall.isArabicText
                }
                "5" -> {
                    newSpeech = "Location set to Seef / City Centre. Connected to ${currentCall.contactName} Seef branch representative! How can I assist you today?"
                    isArabic = currentCall.isArabicText
                }
                "6" -> {
                    newSpeech = "Hello! ${currentCall.contactName} support desk speaking!"
                    isArabic = currentCall.isArabicText
                }
                "7" -> {
                    newSpeech = "Hello! You are connected to ${currentCall.contactName}!"
                    isArabic = currentCall.isArabicText
                }
                "8" -> {
                    newSpeech = "Hello! ${currentCall.contactName} representative speaking!"
                    isArabic = currentCall.isArabicText
                }
                "9" -> {
                    newSpeech = "Hello! Customer care line speaking for ${currentCall.contactName}!"
                    isArabic = currentCall.isArabicText
                }
                "0" -> {
                    newSpeech = "Hello! Operator speaking for ${currentCall.contactName}! How can I help you?"
                    isArabic = currentCall.isArabicText
                }
                else -> {
                    newSpeech = "Connected to ${currentCall.contactName}. How can I assist you?"
                    isArabic = currentCall.isArabicText
                }
            }
        }

        val updatedCall = currentCall.copy(
            responseSpeech = newSpeech,
            lastPressedKey = digit,
            isRinging = false,
            isConnected = true,
            isArabicText = isArabic
        )
        _activeCall.value = updatedCall

        // Play DTMF tone sound, then short confirmation chirp, then TTS speech
        toneHelper.playDtmfTone(digit, 200L)
        toneHelper.playShortBeep(250L) {
            ttsHelper.speak(newSpeech, isArabic = isArabic)
        }
    }

    fun sendInCallTextMessage(userMessage: String) {
        val currentCall = _activeCall.value ?: return
        val text = userMessage.trim()
        if (text.isEmpty()) return

        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        val timeNow = sdf.format(Date())

        val userMsg = InCallMessage(
            sender = "YOU",
            text = text,
            timestamp = timeNow
        )

        // Generate dynamic live representative response for ANY user utterance/text
        val replyText = when {
            text.lowercase().contains("gumball") || text.lowercase().contains("lady") -> {
                "Understood! Regarding '$text', ${currentCall.contactName} representative has noted this special request and is handling it for you right now."
            }
            text.lowercase().contains("manama") || text.lowercase().contains("muharraq") || text.lowercase().contains("riffa") || text.lowercase().contains("seef") -> {
                "Location confirmed: '$text'. Dispatching local ${currentCall.contactName} representative in Bahrain immediately."
            }
            text.lowercase().contains("help") || text.lowercase().contains("emergency") -> {
                "Priority request received: '$text'. Emergency coordinator at ${currentCall.contactName} is on high alert."
            }
            currentCall.isArabicText -> {
                "شكراً لتواصلك معنا بخصوص '$text'. تم تسجيل تفاصيلك لدى ممثل ${currentCall.contactName} وهو معك الآن على الخط."
            }
            else -> {
                "Thank you! Regarding '$text', ${currentCall.contactName} representative has recorded your message and is processing it live on this call."
            }
        }

        val agentMsg = InCallMessage(
            sender = currentCall.contactName,
            text = replyText,
            timestamp = timeNow
        )

        val updatedMessages = currentCall.chatMessages + userMsg + agentMsg
        val updatedCall = currentCall.copy(
            chatMessages = updatedMessages,
            responseSpeech = "${currentCall.contactName} Agent: $replyText"
        )
        _activeCall.value = updatedCall

        // Play short tone and speak representative's reply live
        toneHelper.playDtmfTone("5", 150L)
        ttsHelper.speak(replyText, isArabic = currentCall.isArabicText)
    }

    fun openRadioHub() {
        _showRadioHub.value = true
    }

    fun dismissRadioHub() {
        _showRadioHub.value = false
    }

    fun promptWhoDoYouWantToCall() {
        _showRadioHub.value = false
        val msg = "Who do you want to call? Please type or enter a number or name."
        ttsHelper.speak(msg)
    }

    fun speakCurrentTimelineWeather() {
        val speech = getCurrentEnglishTimeDateWeather()
        ttsHelper.speak(speech)
    }

    fun addCustomMediaStream(title: String, url: String, type: String) {
        viewModelScope.launch {
            repository.addMediaStream(
                MediaStream(
                    title = title,
                    url = url,
                    mediaType = type,
                    category = if (type == "VIDEO") "Video" else "Saved Stream",
                    isPreset = false
                )
            )
        }
    }

    fun deleteMediaStream(id: Long) {
        viewModelScope.launch {
            repository.deleteMediaStream(id)
        }
    }

    fun addNumberToConferenceCall(newNumber: String) {
        val currentCall = _activeCall.value ?: return
        val cleanNum = newNumber.replace(" ", "").replace("-", "").replace("(", "").replace(")", "")

        val resolvedName = when {
            cleanNum in listOf("999", "911", "112") -> "Emergency Services ($cleanNum)"
            cleanNum in listOf("199", "299") -> "Traffic & Civil Defense ($cleanNum)"
            cleanNum == "17796000" || newNumber.lowercase().contains("causeway") || newNumber.lowercase().contains("king") -> "King Fahd Causeway (+973 17796000)"
            cleanNum == "33678882" || newNumber.lowercase().contains("airport") -> "Bahrain Airport BIA (+973 33678882)"
            cleanNum == "17214433" || newNumber.lowercase().contains("manama") -> "Manama Harbour (+973 17214433)"
            cleanNum == "17177771" || newNumber.lowercase().contains("seef") -> "Seef City Centre (+973 17177771)"
            cleanNum == "140" -> "140 English Weather"
            cleanNum == "141" -> "141 Arabic Weather"
            cleanNum == "181" -> "181 Directory"
            newNumber.any { it.isLetter() } -> newNumber
            else -> "Participant $newNumber"
        }

        val updatedNumbers = currentCall.conferenceNumbers + "$resolvedName ($newNumber)"
        val newSpeech = "Added $resolvedName to the call! Multi-party conference connected. Everyone is now active and talking on the line."

        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        val timeNow = sdf.format(Date())
        val confMsg = InCallMessage(
            sender = "SYSTEM",
            text = "👥 Joined Call: $resolvedName ($newNumber) is now active and talking on the call.",
            timestamp = timeNow
        )

        val updatedMessages = currentCall.chatMessages + confMsg

        val updatedCall = currentCall.copy(
            conferenceNumbers = updatedNumbers,
            isConference = true,
            responseSpeech = newSpeech,
            chatMessages = updatedMessages
        )
        _activeCall.value = updatedCall
        ttsHelper.speak(newSpeech)
    }

    fun endCall() {
        toneHelper.stop()
        ttsHelper.stop()
        _activeCall.value = null
    }


    private fun getUSTimeDateWeather(): String {
        val usTz = TimeZone.getTimeZone("America/New_York")
        val timeSdf = SimpleDateFormat("h:mm a", Locale.ENGLISH).apply { timeZone = usTz }
        val dateSdf = SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.ENGLISH).apply { timeZone = usTz }
        val now = Date()
        val timeStr = timeSdf.format(now)
        val dateStr = dateSdf.format(now)
        return "Today in the United States is $dateStr. The current real time in New York, USA is $timeStr. Weather in New York, USA is 22 degrees Celsius (72 degrees Fahrenheit), partly cloudy."
    }

    private fun getJapanTimeDateWeather(): String {
        val tz = TimeZone.getTimeZone("Asia/Tokyo")
        val timeSdf = SimpleDateFormat("h:mm a", Locale.ENGLISH).apply { timeZone = tz }
        val dateSdf = SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.ENGLISH).apply { timeZone = tz }
        val now = Date()
        return "Today in Japan is ${dateSdf.format(now)}. Current time in Tokyo, Japan is ${timeSdf.format(now)}. Weather in Tokyo is 26 degrees Celsius, clear skies."
    }

    private fun getKoreaTimeDateWeather(): String {
        val tz = TimeZone.getTimeZone("Asia/Seoul")
        val timeSdf = SimpleDateFormat("h:mm a", Locale.ENGLISH).apply { timeZone = tz }
        val dateSdf = SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.ENGLISH).apply { timeZone = tz }
        val now = Date()
        return "Today in South Korea is ${dateSdf.format(now)}. Current time in Seoul, South Korea is ${timeSdf.format(now)}. Weather in Seoul is 24 degrees Celsius, pleasant."
    }

    private fun getUKTimeDateWeather(): String {
        val tz = TimeZone.getTimeZone("Europe/London")
        val timeSdf = SimpleDateFormat("h:mm a", Locale.ENGLISH).apply { timeZone = tz }
        val dateSdf = SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.ENGLISH).apply { timeZone = tz }
        val now = Date()
        return "Today in the United Kingdom is ${dateSdf.format(now)}. Current time in London, UK is ${timeSdf.format(now)}. Weather in London is 19 degrees Celsius, mild."
    }

    private fun getSaudiTimeDateWeather(): String {
        val tz = TimeZone.getTimeZone("Asia/Riyadh")
        val timeSdf = SimpleDateFormat("h:mm a", Locale.ENGLISH).apply { timeZone = tz }
        val dateSdf = SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.ENGLISH).apply { timeZone = tz }
        val now = Date()
        return "Today in Saudi Arabia is ${dateSdf.format(now)}. Current time in Riyadh is ${timeSdf.format(now)}. Weather in Riyadh is 38 degrees Celsius, sunny."
    }

    private fun getUAETimeDateWeather(): String {
        val tz = TimeZone.getTimeZone("Asia/Dubai")
        val timeSdf = SimpleDateFormat("h:mm a", Locale.ENGLISH).apply { timeZone = tz }
        val dateSdf = SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.ENGLISH).apply { timeZone = tz }
        val now = Date()
        return "Today in the UAE is ${dateSdf.format(now)}. Current time in Dubai is ${timeSdf.format(now)}. Weather in Dubai is 36 degrees Celsius, clear."
    }

    private fun getGermanyTimeDateWeather(): String {
        val tz = TimeZone.getTimeZone("Europe/Berlin")
        val timeSdf = SimpleDateFormat("h:mm a", Locale.GERMAN).apply { timeZone = tz }
        val dateSdf = SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.GERMAN).apply { timeZone = tz }
        val now = Date()
        return "Today in Germany is ${dateSdf.format(now)}. Current real time in Berlin, Germany is ${timeSdf.format(now)}. Weather in Berlin is 21 degrees Celsius, clear."
    }

    private fun getFranceTimeDateWeather(): String {
        val tz = TimeZone.getTimeZone("Europe/Paris")
        val timeSdf = SimpleDateFormat("h:mm a", Locale.FRENCH).apply { timeZone = tz }
        val dateSdf = SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.FRENCH).apply { timeZone = tz }
        val now = Date()
        return "Today in France is ${dateSdf.format(now)}. Current real time in Paris, France is ${timeSdf.format(now)}. Weather in Paris is 23 degrees Celsius, sunny."
    }

    private fun getIndiaTimeDateWeather(): String {
        val tz = TimeZone.getTimeZone("Asia/Kolkata")
        val timeSdf = SimpleDateFormat("h:mm a", Locale.ENGLISH).apply { timeZone = tz }
        val dateSdf = SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.ENGLISH).apply { timeZone = tz }
        val now = Date()
        return "Today in India is ${dateSdf.format(now)}. Current real time in Mumbai, India is ${timeSdf.format(now)}. Weather in Mumbai is 29 degrees Celsius, warm."
    }

    private fun getAustraliaTimeDateWeather(): String {
        val tz = TimeZone.getTimeZone("Australia/Sydney")
        val timeSdf = SimpleDateFormat("h:mm a", Locale.ENGLISH).apply { timeZone = tz }
        val dateSdf = SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.ENGLISH).apply { timeZone = tz }
        val now = Date()
        return "Today in Australia is ${dateSdf.format(now)}. Current real time in Sydney, Australia is ${timeSdf.format(now)}. Weather in Sydney is 18 degrees Celsius, breezy."
    }

    private fun getEgyptTimeDateWeather(): String {
        val tz = TimeZone.getTimeZone("Africa/Cairo")
        val timeSdf = SimpleDateFormat("h:mm a", Locale.ENGLISH).apply { timeZone = tz }
        val dateSdf = SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.ENGLISH).apply { timeZone = tz }
        val now = Date()
        return "Today in Egypt is ${dateSdf.format(now)}. Current real time in Cairo, Egypt is ${timeSdf.format(now)}. Weather in Cairo is 33 degrees Celsius, sunny."
    }

    private fun getCurrentEnglishTimeDateWeather(): String {
        val bhTz = TimeZone.getTimeZone("Asia/Bahrain")
        val timeSdfBH = SimpleDateFormat("h:mm a", Locale.ENGLISH).apply { timeZone = bhTz }
        val dateSdfBH = SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.ENGLISH).apply { timeZone = bhTz }

        val calendar = Calendar.getInstance(bhTz)
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val now = Date()
        val bhTime = timeSdfBH.format(now)
        val bhDate = dateSdfBH.format(now)

        val (tempC, desc) = when (hour) {
            in 5..9 -> Pair(31, "clear morning skies with a mild coastal sea breeze, humidity 55%, wind NW 12 km/h")
            in 10..15 -> Pair(37, "bright and sunny, warm northwest wind at 16 km/h, humidity 38%")
            in 16..19 -> Pair(34, "pleasant warm sunset over Manama, humidity 48%, wind 10 km/h")
            else -> Pair(29, "clear starry night skies over Bahrain, mild sea breeze, humidity 62%")
        }

        return "Hello! This is Nicole from Kingdom of Bahrain 140 Service. Today is $bhDate. The current real time in Manama, Kingdom of Bahrain is $bhTime. Real time weather in Bahrain is $tempC degrees Celsius, $desc."
    }

    private fun getCurrentArabicTimeDateWeather(): String {
        val bhTz = TimeZone.getTimeZone("Asia/Bahrain")
        val timeSdfBH = SimpleDateFormat("hh:mm a", Locale("ar")).apply { timeZone = bhTz }
        val dateSdfBH = SimpleDateFormat("EEEE، d MMMM yyyy", Locale("ar")).apply { timeZone = bhTz }

        val calendar = Calendar.getInstance(bhTz)
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val now = Date()
        val bhTime = timeSdfBH.format(now)
        val bhDate = dateSdfBH.format(now)

        val (tempC, descAr) = when (hour) {
            in 5..9 -> Pair(31, "سماء صباحية صافية مع نسيم ساحلي لطيف، ورطوبة ٥٥٪")
            in 10..15 -> Pair(37, "مشمس وصافٍ مع رياح شمالية غربية بسرعة ١٦ كم/س، ورطوبة ٣٨٪")
            in 16..19 -> Pair(34, "أجواء دافئة عند الغروب، ورطوبة ٤٨٪")
            else -> Pair(29, "سماء صافية ملائمة ليلاً في المنامة مع نسيم بحري عليل، ورطوبة ٦٢٪")
        }

        return "أهلاً بكم! معكم نيكول من خدمة مملكة البحرين 141. اليوم هو $bhDate. الوقت الحالي المباشر في المنامة، مملكة البحرين هو $bhTime. الطقس المباشر في البحرين $tempC درجة مئوية، $descAr."
    }

    private fun getCurrentEnglishTime(): String {
        val sdf = SimpleDateFormat("h:mm a", Locale.ENGLISH)
        return sdf.format(Date())
    }

    private fun getCurrentArabicTime(): String {
        val sdf = SimpleDateFormat("hh:mm a", Locale("ar"))
        return sdf.format(Date())
    }

    override fun onCleared() {
        super.onCleared()
        toneHelper.release()
        ttsHelper.shutdown()
    }

}

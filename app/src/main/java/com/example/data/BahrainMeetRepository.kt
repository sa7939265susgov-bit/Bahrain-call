package com.example.data

import kotlinx.coroutines.flow.Flow

class BahrainMeetRepository(private val dao: AppDao) {

    val userProfile: Flow<UserProfile?> = dao.getUserProfile()
    val callLogs: Flow<List<CallLogEntry>> = dao.getAllCallLogs()
    val directoryContacts: Flow<List<DirectoryContact>> = dao.getAllDirectoryContacts()
    val mediaStreams: Flow<List<MediaStream>> = dao.getAllMediaStreams()

    suspend fun ensureInitialData() {
        if (dao.getUserProfileDirect() == null) {
            dao.insertOrUpdateUserProfile(
                UserProfile(
                    id = 1,
                    phoneNumber = "",
                    userName = "Bahrain User",
                    isVerified = false,
                    selectedSim = "BATELCO",
                    gccRoamingActive = false,
                    gccCountry = "Saudi Arabia",
                    accountBalance = 25.500
                )
            )
        }

        // Preset Bahrain Radio Stations & Traditional Songs
        val presetRadios = listOf(
            MediaStream(
                id = 1,
                title = "Bahrain Quran FM (106.1 FM)",
                url = "https://5c7b683162943.streamlock.net/live/ngrp:radio-106-1_all/playlist.m3u8",
                mediaType = "AUDIO",
                category = "Radio",
                isPreset = true
            ),
            MediaStream(
                id = 2,
                title = "Bahrain FM (93.3 FM)",
                url = "https://5c7b683162943.streamlock.net/live/ngrp:radio-93-3_all/playlist.m3u8",
                mediaType = "AUDIO",
                category = "Radio",
                isPreset = true
            ),
            MediaStream(
                id = 3,
                title = "🎶 Bahrain Ya Watani (National Song)",
                url = "https://5c7b683162943.streamlock.net/live/ngrp:radio-93-3_all/playlist.m3u8",
                mediaType = "AUDIO",
                category = "Songs",
                isPreset = true
            ),
            MediaStream(
                id = 4,
                title = "🎶 Fidwa Li-Ayni Bahrain (Traditional Folk)",
                url = "https://5c7b683162943.streamlock.net/live/ngrp:radio-93-3_all/playlist.m3u8",
                mediaType = "AUDIO",
                category = "Songs",
                isPreset = true
            ),
            MediaStream(
                id = 5,
                title = "🎶 Sawt Bahraini Pearl Diver Melodies",
                url = "https://5c7b683162943.streamlock.net/live/ngrp:radio-93-3_all/playlist.m3u8",
                mediaType = "AUDIO",
                category = "Songs",
                isPreset = true
            )
        )
        dao.insertMediaStreams(presetRadios)

        // Default Bahrain Directory Contacts

        val initialContacts = listOf(
            DirectoryContact("bisb", "BisB Bank (Bahrain Islamic Bank)", "17515151", "contactus@bisb.com", "Bank", "account_balance", "Leading Islamic bank in Bahrain.", "7:30 AM - 3:00 PM"),
            DirectoryContact("nbb", "NBB (National Bank of Bahrain)", "17214433", "contact@nbbonline.com", "Bank", "account_balance", "First local bank in the Kingdom of Bahrain.", "7:30 AM - 3:30 PM"),
            DirectoryContact("bbk", "BBK (Bank of Bahrain & Kuwait)", "17207777", "feedback@bbkonline.com", "Bank", "account_balance", "Major retail & commercial bank in Bahrain.", "8:00 AM - 3:30 PM"),
            DirectoryContact("kfh", "KFH Bahrain (Kuwait Finance House)", "77777777", "info@kfh.bh", "Bank", "account_balance", "Premier Islamic commercial bank.", "8:00 AM - 3:00 PM"),
            DirectoryContact("jasmis", "Jasmi's Bahrain Fast Food", "17770077", "orders@jasmis.com", "Restaurant", "fastfood", "Authentic Bahraini fast food & burgers.", "6:00 AM - 3:00 AM"),
            DirectoryContact("kfc", "KFC Bahrain", "17111111", "delivery@kfc.bh", "Restaurant", "fastfood", "Fried chicken delivery in Bahrain.", "10:00 AM - 2:00 AM"),
            DirectoryContact("mcdonalds", "McDonald's Bahrain", "17221122", "contact@mcdonalds.bh", "Restaurant", "fastfood", "Burgers & fries across Manama & Riffa.", "6:00 AM - 2:00 AM"),
            DirectoryContact("albaik", "Al Baik Bahrain", "17001122", "info@albaik.bh", "Restaurant", "fastfood", "Broasted chicken & garlic sauce.", "10:00 AM - 1:00 AM"),
            DirectoryContact("starbucks", "Starbucks City Centre Bahrain", "17178000", "coffee@starbucks.bh", "Cafe", "local_cafe", "Specialty coffee & pastries.", "6:30 AM - 12:00 AM"),
            DirectoryContact("costa", "Costa Coffee Bahrain", "17112000", "info@costa.bh", "Cafe", "local_cafe", "Handcrafted espresso drinks.", "7:00 AM - 11:30 PM"),
            DirectoryContact("sharafdg", "Sharaf DG Electronics", "80008008", "support@sharafdg.bh", "Electronics", "devices", "Laptops, smartphones & home appliances.", "10:00 AM - 11:00 PM"),
            DirectoryContact("extra", "eXtra Stores Bahrain", "17500000", "care@extra.com.bh", "Electronics", "devices", "Electronics, TVs, SIMs & smart gadgets.", "9:30 AM - 10:30 PM"),
            DirectoryContact("court_testament", "Ministry of Justice & Testament Dept", "17513000", "justice@moj.gov.bh", "Court", "gavel", "Bahrain legal court, wills & testament department.", "7:30 AM - 2:15 PM"),
            DirectoryContact("batelco_store", "Batelco City Centre Store", "196", "support@batelco.com.bh", "Telecom", "sim_card", "Official Batelco SIM card & fiber care.", "9:00 AM - 10:00 PM"),
            DirectoryContact("stc_store", "STC Bahrain Seef Mall Store", "119", "support@stc.com.bh", "Telecom", "sim_card", "STC SIM activations & 5G plans.", "9:00 AM - 10:00 PM"),
            DirectoryContact("zain_store", "Zain Bahrain Seef Store", "195", "support@bh.zain.com", "Telecom", "sim_card", "Zain mobile plans & eSIM registration.", "9:00 AM - 10:00 PM"),
            DirectoryContact("citycentre", "City Centre Bahrain Mall", "17177771", "guestservice@citycentrebahrain.com", "Mall", "store", "Premier shopping & dining mall in Seef.", "10:00 AM - 11:00 PM"),
            DirectoryContact("seefmall", "Seef Mall Manama", "17586000", "info@seef.com.bh", "Mall", "store", "Family shopping mall in Seef district.", "10:00 AM - 10:00 PM"),
            DirectoryContact("fourseasons", "Four Seasons Bahrain Bay", "17115000", "concierge.bahrain@fourseasons.com", "Hotel", "hotel", "Luxury 5-star hotel in Bahrain Bay.", "Open 24 Hours"),
            DirectoryContact("ritz", "The Ritz-Carlton Bahrain", "17580000", "rc.bahrain@ritzcarlton.com", "Hotel", "hotel", "Beachfront resort and luxury villa hotel in Seef.", "Open 24 Hours"),
            DirectoryContact("bia", "Bahrain International Airport (BIA)", "33678882", "info@bahrainairport.bh", "Airport", "flight", "Main international aviation hub in Muharraq.", "Open 24 Hours"),
            DirectoryContact("e999", "Emergency Police & Ambulance (999)", "999", "emergency@interior.gov.bh", "Emergency", "local_police", "Bahrain National Emergency Hotline.", "Open 24 Hours"),
            DirectoryContact("e199", "Traffic & Accident Patrols (199)", "199", "traffic@interior.gov.bh", "Emergency", "traffic", "Bahrain Traffic Accidents & Patrols.", "Open 24 Hours"),
            DirectoryContact("e299", "Civil Defense & Fire Dept (299)", "299", "civildefense@interior.gov.bh", "Emergency", "fire_truck", "Bahrain Civil Defense & Fire Dept.", "Open 24 Hours"),
            DirectoryContact("time_ar", "Arabic Time Hotline (Nicole 141)", "141", "time@batelco.com.bh", "Utility", "schedule", "Spoken time, date & weather in Arabic (141).", "Open 24 Hours"),
            DirectoryContact("time_en", "English Time Hotline (Nicole 140)", "140", "time@batelco.com.bh", "Utility", "schedule", "Spoken time, date & weather in English (140).", "Open 24 Hours"),
            DirectoryContact("intl_dir", "International Directory (181)", "181", "intl@directory.org", "Utility", "public", "Worldwide International Directory Assistance (181).", "Open 24 Hours"),
            DirectoryContact("ewa_utility", "EWA Electricity & Water Authority", "17515555", "customercare@ewa.bh", "Utility", "bolt", "Bahrain Electricity & Water billing and support.", "Open 24 Hours"),
            DirectoryContact("ewa_emergency", "EWA Emergency Line (991)", "991", "emergency@ewa.bh", "Emergency", "warning", "Power outage and water leak emergency hotline.", "Open 24 Hours"),
            DirectoryContact("smc_hospital", "Salmaniya Medical Complex (SMC)", "17288888", "info@health.gov.bh", "Hospital", "local_hospital", "Main public general hospital and trauma center in Manama.", "Open 24 Hours"),
            DirectoryContact("khuh_hospital", "King Hamad University Hospital", "17351111", "care@khuh.org.bh", "Hospital", "local_hospital", "Premier medical facility in Busaiteen.", "Open 24 Hours"),
            DirectoryContact("bdf_hospital", "BDF Royal Medical Services Hospital", "17766666", "info@bdfmedical.org.bh", "Hospital", "local_hospital", "Military and general hospital in Riffa.", "Open 24 Hours"),
            DirectoryContact("gulf_air", "Gulf Air National Carrier", "17373737", "reservations@gulfair.com", "Airline", "flight", "Bahrain's national flag carrier airline reservations.", "Open 24 Hours"),
            DirectoryContact("egov_bahrain", "eGovernment National Contact Centre", "80008001", "support@iga.gov.bh", "Government", "account_balance", "Bahrain Information & eGovernment Authority portal support.", "Open 24 Hours"),
            DirectoryContact("traffic_dir", "General Directorate of Traffic", "17872222", "traffic@interior.gov.bh", "Government", "traffic", "Driver licenses, vehicle registration & traffic services.", "7:00 AM - 2:00 PM"),
            DirectoryContact("moh_bahrain", "Ministry of Health Helpline", "17284000", "contact@health.gov.bh", "Government", "local_hospital", "Public health guidance and medical services information.", "7:00 AM - 2:15 PM"),
            DirectoryContact("consumer_prot", "Consumer Protection Directorate", "80008001", "consumer@moic.gov.bh", "Government", "policy", "Ministry of Industry & Commerce consumer complaints.", "7:30 AM - 3:00 PM"),
            DirectoryContact("bahrain_post", "Bahrain Post General Directorate", "17523333", "support@bahrainpost.gov.bh", "Government", "local_post_office", "Postal delivery, PO Boxes & tracking across Bahrain.", "7:30 AM - 2:00 PM"),
            DirectoryContact("uob_univ", "University of Bahrain (UOB)", "17438888", "info@uob.edu.bh", "Education", "school", "National public university in Sakhir & Isa Town.", "8:00 AM - 4:00 PM"),
            DirectoryContact("coast_guard", "Coast Guard Emergency (994)", "994", "coastguard@interior.gov.bh", "Emergency", "local_police", "Maritime search & rescue and sea emergencies.", "Open 24 Hours"),
            DirectoryContact("zombie", "Zombie Hotline (666)", "666", "spooky@zombie.bh", "Special", "warning", "Spooky zombie operator hotline!", "Open 24 Hours")
        )

        dao.insertDirectoryContacts(initialContacts)

        val now = System.currentTimeMillis()
        val initialLogs = listOf(
            CallLogEntry(
                numberOrEmail = "196",
                contactName = "Batelco Customer Care",
                callType = "AUDIO",
                carrierUsed = "BATELCO",
                timestamp = now - (1000L * 60 * 15), // 15 mins ago
                durationSeconds = 84,
                isEmergency = false,
                direction = "INCOMING"
            ),
            CallLogEntry(
                numberOrEmail = "17111111",
                contactName = "KFC Bahrain Delivery",
                callType = "AUDIO",
                carrierUsed = "STC",
                timestamp = now - (1000L * 60 * 60 * 3), // 3 hours ago
                durationSeconds = 42,
                isEmergency = false,
                direction = "OUTGOING"
            ),
            CallLogEntry(
                numberOrEmail = "doorbell",
                contactName = "Villa Smart Doorbell Cam #1",
                callType = "VIDEO",
                carrierUsed = "BATELCO",
                timestamp = now - (1000L * 60 * 60 * 5), // 5 hours ago
                durationSeconds = 31,
                isEmergency = false,
                direction = "INCOMING"
            ),
            CallLogEntry(
                numberOrEmail = "999",
                contactName = "Emergency Police & Ambulance",
                callType = "AUDIO",
                carrierUsed = "BATELCO",
                timestamp = now - (1000L * 60 * 60 * 24), // yesterday
                durationSeconds = 110,
                isEmergency = true,
                direction = "OUTGOING"
            ),
            CallLogEntry(
                numberOrEmail = "67",
                contactName = "67 Mangoes Meme (67)",
                callType = "AUDIO",
                carrierUsed = "ZAIN",
                timestamp = now - (1000L * 60 * 60 * 48), // 2 days ago
                durationSeconds = 15,
                isEmergency = false,
                direction = "OUTGOING"
            ),
            CallLogEntry(
                numberOrEmail = "+973 39998888",
                contactName = "Ahmed Al-Bahraini (WhatsApp)",
                callType = "VIDEO",
                carrierUsed = "STC",
                timestamp = now - (1000L * 60 * 60 * 72), // 3 days ago
                durationSeconds = 245,
                isEmergency = false,
                direction = "INCOMING"
            )
        )
        dao.insertCallLogs(initialLogs)
    }

    suspend fun saveProfile(userProfile: UserProfile) {
        dao.insertOrUpdateUserProfile(userProfile)
    }

    suspend fun addCallLog(entry: CallLogEntry) {
        dao.insertCallLog(entry)
    }

    suspend fun clearLogs() {
        dao.clearCallLogs()
    }

    suspend fun addMediaStream(stream: MediaStream) {
        dao.insertMediaStream(stream)
    }

    suspend fun deleteMediaStream(id: Long) {
        dao.deleteMediaStream(id)
    }
}


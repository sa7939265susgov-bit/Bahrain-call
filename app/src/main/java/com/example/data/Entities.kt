package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey val id: Int = 1,
    val phoneNumber: String = "+973 33678882",
    val userName: String = "Bahrain User",
    val isVerified: Boolean = false,
    val selectedSim: String = "BATELCO", // BATELCO, STC, ZAIN
    val gccRoamingActive: Boolean = false,
    val gccCountry: String = "Saudi Arabia",
    val accountBalance: Double = 25.500
)

@Entity(tableName = "call_logs")
data class CallLogEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val numberOrEmail: String,
    val contactName: String,
    val callType: String, // "VIDEO", "AUDIO"
    val carrierUsed: String, // "Batelco", "STC", "Zain"
    val timestamp: Long = System.currentTimeMillis(),
    val durationSeconds: Int = 0,
    val isEmergency: Boolean = false,
    val direction: String = "OUTGOING" // "INCOMING" or "OUTGOING"
)

@Entity(tableName = "directory_contacts")
data class DirectoryContact(
    @PrimaryKey val id: String,
    val name: String,
    val phoneNumber: String,
    val email: String,
    val category: String, // "Restaurant", "Bank", "Hotel", "Airport", "Emergency", "Utility", "Support", "USSD", "Telecom", "Court", "Electronics"
    val iconName: String = "business",
    val description: String = "",
    val openingHours: String = "8:00 AM - 11:00 PM"
)

@Entity(tableName = "media_streams")
data class MediaStream(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val url: String,
    val mediaType: String, // "AUDIO" or "VIDEO"
    val category: String = "Radio", // "Radio", "Saved Stream", "Music", "Video"
    val isPreset: Boolean = false,
    val addedTimestamp: Long = System.currentTimeMillis()
)


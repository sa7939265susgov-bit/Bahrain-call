package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    fun getUserProfile(): Flow<UserProfile?>

    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    suspend fun getUserProfileDirect(): UserProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateUserProfile(userProfile: UserProfile)

    @Query("SELECT * FROM call_logs ORDER BY timestamp DESC")
    fun getAllCallLogs(): Flow<List<CallLogEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCallLog(entry: CallLogEntry)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCallLogs(entries: List<CallLogEntry>)

    @Query("DELETE FROM call_logs")
    suspend fun clearCallLogs()

    @Query("SELECT * FROM directory_contacts ORDER BY name ASC")
    fun getAllDirectoryContacts(): Flow<List<DirectoryContact>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDirectoryContacts(contacts: List<DirectoryContact>)

    @Query("SELECT * FROM media_streams ORDER BY isPreset DESC, addedTimestamp DESC")
    fun getAllMediaStreams(): Flow<List<MediaStream>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMediaStream(stream: MediaStream)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMediaStreams(streams: List<MediaStream>)

    @Query("DELETE FROM media_streams WHERE id = :id")
    suspend fun deleteMediaStream(id: Long)
}


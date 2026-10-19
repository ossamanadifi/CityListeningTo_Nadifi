package com.example.citylisteningto_nadifi.classes

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "song_tab")
data class Song(
    @PrimaryKey
    val id: Int,
    val longitude: Double,
    val latitude: Double,
    val hidden: Boolean,
    val bpm : Int,
    val loudness : Double,
    val mood: String?,
    val genre: String?,
    val instrument: String?,
    val filePath: String?,
    val client_id: Int?
)



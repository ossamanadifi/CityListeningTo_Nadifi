package com.example.citylisteningto_nadifi.classes

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "song_import")
class SongImport (
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val longitude: Double,
    val latitude: Double,
    val path : String,
    val client_id : Int)

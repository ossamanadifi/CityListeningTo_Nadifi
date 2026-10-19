package com.example.citylisteningto_nadifi.classes

data class SongMapped (
    val longitude: Float?,
    val latitude: Float?,
    val id: Int,
    val creator_id: Int,
    val creator_username: String,
    val tags: Tags
)
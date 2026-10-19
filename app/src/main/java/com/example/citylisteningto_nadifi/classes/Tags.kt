package com.example.citylisteningto_nadifi.classes

data class Tags(
    val bpm: Int,
    val danceability: Float,
    val loudness: Double,
    val mood: Mood,
    val genre: Genre,
    val instrument: Instrument
    ) {
    override fun toString(): String {
        return "BPM: $bpm\n\nDanceability: ${"%.2f".format(danceability)}\n\nLoudness: ${"%.2f".format(loudness)}\n\nMood:\n${mood}\n\nGenre:\n${genre}\n\nInstrument:\n${instrument}".trimIndent()
    }
}

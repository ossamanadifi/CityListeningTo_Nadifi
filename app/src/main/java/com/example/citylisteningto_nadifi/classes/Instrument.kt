package com.example.citylisteningto_nadifi.classes

data class Instrument(
    val accordion: Double,
    val acousticbassguitar: Double,
    val acousticguitar: Double,
    val bass: Double,
    val beat: Double,
    val bell: Double,
    val bongo: Double,
    val brass: Double,
    val cello: Double,
    val clarinet: Double,
    val classicalguitar: Double,
    val computer: Double,
    val doublebass: Double,
    val drummachine: Double,
    val drums: Double,
    val electricguitar: Double,
    val electricpiano: Double,
    val flute: Double,
    val guitar: Double,
    val harmonica: Double,
    val harp: Double,
    val horn: Double,
    val keyboard: Double,
    val oboe: Double,
    val orchestra: Double,
    val organ: Double,
    val pad: Double,
    val percussion: Double,
    val piano: Double,
    val pipeorgan: Double,
    val rhodes: Double,
    val sampler: Double,
    val saxophone: Double,
    val strings: Double,
    val synthesizer: Double,
    val trombone: Double,
    val trumpet: Double,
    val viola: Double,
    val violin: Double,
    val voice: Double
) {
    override fun toString(): String {
        val filteredValues = mapOf(
            "accordion" to accordion,
            "acousticbassguitar" to acousticbassguitar,
            "acousticguitar" to acousticguitar,
            "bass" to bass,
            "beat" to beat,
            "bell" to bell,
            "bongo" to bongo,
            "brass" to brass,
            "cello" to cello,
            "clarinet" to clarinet,
            "classicalguitar" to classicalguitar,
            "computer" to computer,
            "doublebass" to doublebass,
            "drummachine" to drummachine,
            "drums" to drums,
            "electricguitar" to electricguitar,
            "electricpiano" to electricpiano,
            "flute" to flute,
            "guitar" to guitar,
            "harmonica" to harmonica,
            "harp" to harp,
            "horn" to horn,
            "keyboard" to keyboard,
            "oboe" to oboe,
            "orchestra" to orchestra,
            "organ" to organ,
            "pad" to pad,
            "percussion" to percussion,
            "piano" to piano,
            "pipeorgan" to pipeorgan,
            "rhodes" to rhodes,
            "sampler" to sampler,
            "saxophone" to saxophone,
            "strings" to strings,
            "synthesizer" to synthesizer,
            "trombone" to trombone,
            "trumpet" to trumpet,
            "viola" to viola,
            "violin" to violin,
            "voice" to voice
        ).filter { it.value > 0.1 }

        val maxEntry = filteredValues.maxByOrNull { it.value }

        return if (maxEntry != null) {
            "${maxEntry.key}: ${"%.2f".format(maxEntry.value)}"
        } else {
            "No instrument has a score greater than 0.1"
        }
    }
}

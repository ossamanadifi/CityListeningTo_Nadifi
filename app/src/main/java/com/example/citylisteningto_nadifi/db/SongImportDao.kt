package com.example.citylisteningto_nadifi.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.citylisteningto_nadifi.classes.SongImport


@Dao
interface SongImportDao {


    @Insert
    suspend fun insertSong(song : SongImport)

    @Delete
    suspend fun deleteSong(song : SongImport)

    @Query("SELECT * FROM song_import WHERE client_id = :id")
    suspend fun getUserSongs(id : Int): List<SongImport>

    @Query("SELECT path FROM song_import WHERE ID = :id")
    suspend fun getSongPath(id:Int) : String?

    @Query("SELECT * FROM song_import WHERE ID = :id")
    suspend fun getSongById(id: Int) : SongImport

}
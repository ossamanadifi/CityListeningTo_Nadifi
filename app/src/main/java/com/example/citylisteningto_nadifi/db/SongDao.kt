package com.example.citylisteningto_nadifi.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.citylisteningto_nadifi.classes.Song

@Dao
interface SongDao {

    @Insert
    suspend fun insertSong(song : Song)

    @Insert
    suspend fun insertSongs(songs : List<Song>)

    @Delete
    suspend fun deleteSong(song : Song)

    @Query("SELECT * FROM song_tab")
    suspend fun getAllSongs() : List<Song>

    @Query("SELECT * FROM song_tab WHERE client_id = :id")
    suspend fun getMySongs(id : Int): List<Song>

    @Query("SELECT filePath FROM song_tab WHERE ID = :id")
    suspend fun getSongPath(id:Int) : String?

    @Query("DELETE FROM song_tab WHERE client_id = :id")
    suspend fun deleteSongsById(id: Int)

    @Query("UPDATE SONG_TAB SET hidden = (hidden + 1)% 2 WHERE ID =:id ")
    suspend fun UpdateSongVisible(id: Int)


    @Query("SELECT * FROM song_tab WHERE id = :id")
    suspend fun getSongById(id: Int): Song?

}
package com.example.citylisteningto_nadifi.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.citylisteningto_nadifi.classes.Song
import com.example.citylisteningto_nadifi.classes.SongImport

@Database(
    entities = [Song::class , SongImport::class] , version = 9
)
abstract class SongDatabase: RoomDatabase() {

    abstract fun songDao(): SongDao
    abstract fun songImportDao(): SongImportDao

    companion object {
        @Volatile
        private var instance: SongDatabase? = null
        @Synchronized
        fun getInstance(context: Context): SongDatabase {
            if (instance == null) {
                instance = Room.databaseBuilder(context, SongDatabase::class.java, "song").fallbackToDestructiveMigration().build()
            }
            return instance as SongDatabase
        }
    }
}
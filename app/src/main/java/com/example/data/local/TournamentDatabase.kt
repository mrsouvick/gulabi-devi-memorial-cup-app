package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [CachedTournamentContent::class], version = 1, exportSchema = false)
abstract class TournamentDatabase : RoomDatabase() {

    abstract fun tournamentDao(): TournamentDao

    companion object {
        @Volatile
        private var INSTANCE: TournamentDatabase? = null

        fun getInstance(context: Context): TournamentDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TournamentDatabase::class.java,
                    "gulabi_devi_cache.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}

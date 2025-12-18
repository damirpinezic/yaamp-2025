package com.g3ck0.yaamp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.g3ck0.yaamp.data.model.Playlist
import com.g3ck0.yaamp.data.model.PlaylistSongCrossRef
import com.g3ck0.yaamp.data.model.Song

/**
 * Room database for YAAMP
 */
@Database(
    entities = [
        Song::class,
        Playlist::class,
        PlaylistSongCrossRef::class
    ],
    version = 1,
    exportSchema = false
)
abstract class YaampDatabase : RoomDatabase() {
    
    abstract fun songDao(): SongDao
    abstract fun playlistDao(): PlaylistDao
    
    companion object {
        const val DATABASE_NAME = "yaamp_database"
    }
}

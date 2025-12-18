package com.g3ck0.yaamp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Data model representing a playlist
 */
@Entity(tableName = "playlists")
data class Playlist(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val createdAt: Long = System.currentTimeMillis(),
    val modifiedAt: Long = System.currentTimeMillis()
)

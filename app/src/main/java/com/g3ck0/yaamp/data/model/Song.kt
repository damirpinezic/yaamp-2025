package com.g3ck0.yaamp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Data model representing an audio track
 */
@Entity(tableName = "songs")
data class Song(
    @PrimaryKey
    val id: Long,
    val title: String,
    val artist: String,
    val album: String,
    val albumId: Long,
    val duration: Long,
    val path: String,
    val dateAdded: Long,
    var isFavorite: Boolean = false
) {
    /**
     * Returns formatted duration string (e.g., "3:45")
     */
    fun getFormattedDuration(): String {
        val totalSeconds = (duration / 1000).toInt()
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%d:%02d", minutes, seconds)
    }
    
    /**
     * Returns display artist or "Unknown Artist"
     */
    fun getDisplayArtist(): String {
        return if (artist.isBlank() || artist == "<unknown>") {
            "Unknown Artist"
        } else {
            artist
        }
    }
    
    /**
     * Returns display title or "Unknown Title"
     */
    fun getDisplayTitle(): String {
        return if (title.isBlank()) {
            "Unknown Title"
        } else {
            title
        }
    }
}

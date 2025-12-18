package com.g3ck0.yaamp.util

import com.g3ck0.yaamp.data.model.Song

/**
 * Represents the current playback state
 */
data class PlaybackState(
    val isPlaying: Boolean = false,
    val currentSong: Song? = null,
    val currentPosition: Long = 0,
    val duration: Long = 0,
    val shuffleEnabled: Boolean = false,
    val repeatMode: RepeatMode = RepeatMode.OFF
)

/**
 * Repeat modes
 */
enum class RepeatMode {
    OFF,
    ALL,
    ONE
}

package com.g3ck0.yaamp.presentation.nowplaying

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.g3ck0.yaamp.media.PlaybackManager
import com.g3ck0.yaamp.util.PlaybackState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/**
 * ViewModel for the now playing screen
 */
@HiltViewModel
class NowPlayingViewModel @Inject constructor(
    private val playbackManager: PlaybackManager
) : ViewModel() {
    
    val playbackState: StateFlow<PlaybackState> = playbackManager.playbackState
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = PlaybackState()
        )
    
    /**
     * Play or pause playback
     */
    fun playPause() {
        playbackManager.playPause()
    }
    
    /**
     * Skip to next track
     */
    fun skipToNext() {
        playbackManager.skipToNext()
    }
    
    /**
     * Skip to previous track
     */
    fun skipToPrevious() {
        playbackManager.skipToPrevious()
    }
    
    /**
     * Seek to position
     */
    fun seekTo(position: Long) {
        playbackManager.seekTo(position)
    }
    
    /**
     * Toggle shuffle mode
     */
    fun toggleShuffle() {
        playbackManager.toggleShuffle()
    }
    
    /**
     * Toggle repeat mode
     */
    fun toggleRepeatMode() {
        playbackManager.toggleRepeatMode()
    }
}

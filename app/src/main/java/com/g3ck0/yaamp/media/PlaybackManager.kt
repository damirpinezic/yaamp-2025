package com.g3ck0.yaamp.media

import android.content.ComponentName
import android.content.Context
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.g3ck0.yaamp.data.model.Song
import com.g3ck0.yaamp.util.PlaybackState
import com.g3ck0.yaamp.util.RepeatMode
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manager for controlling media playback
 */
@Singleton
class PlaybackManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var mediaControllerFuture: ListenableFuture<MediaController>? = null
    private var mediaController: MediaController? = null
    
    private val _playbackState = MutableStateFlow(PlaybackState())
    val playbackState: StateFlow<PlaybackState> = _playbackState.asStateFlow()
    
    private val _currentQueue = MutableStateFlow<List<Song>>(emptyList())
    val currentQueue: StateFlow<List<Song>> = _currentQueue.asStateFlow()
    
    init {
        initializeController()
    }
    
    private fun initializeController() {
        val sessionToken = SessionToken(
            context,
            ComponentName(context, PlaybackService::class.java)
        )
        
        mediaControllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
        
        mediaControllerFuture?.addListener({
            mediaController = mediaControllerFuture?.get()
            mediaController?.addListener(playerListener)
            Timber.d("MediaController connected")
        }, MoreExecutors.directExecutor())
    }
    
    private val playerListener = object : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            updatePlaybackState()
        }
        
        override fun onPlaybackStateChanged(playbackState: Int) {
            updatePlaybackState()
        }
        
        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            updatePlaybackState()
        }
    }
    
    private fun updatePlaybackState() {
        mediaController?.let { controller ->
            val currentMediaItem = controller.currentMediaItem
            val song = currentMediaItem?.mediaMetadata?.extras?.let { bundle ->
                // Extract song from metadata
                // This is a simplified version - you'd extract all song data here
                null
            }
            
            _playbackState.value = PlaybackState(
                isPlaying = controller.isPlaying,
                currentSong = song,
                currentPosition = controller.currentPosition,
                duration = controller.duration,
                shuffleEnabled = controller.shuffleModeEnabled,
                repeatMode = when (controller.repeatMode) {
                    Player.REPEAT_MODE_ONE -> RepeatMode.ONE
                    Player.REPEAT_MODE_ALL -> RepeatMode.ALL
                    else -> RepeatMode.OFF
                }
            )
        }
    }
    
    /**
     * Play a song
     */
    fun playSong(song: Song) {
        val mediaItem = createMediaItem(song)
        mediaController?.apply {
            setMediaItem(mediaItem)
            prepare()
            play()
        }
        Timber.d("Playing song: ${song.title}")
    }
    
    /**
     * Play a list of songs
     */
    fun playQueue(songs: List<Song>, startIndex: Int = 0) {
        val mediaItems = songs.map { createMediaItem(it) }
        _currentQueue.value = songs
        
        mediaController?.apply {
            setMediaItems(mediaItems, startIndex, 0)
            prepare()
            play()
        }
        Timber.d("Playing queue of ${songs.size} songs starting at index $startIndex")
    }
    
    /**
     * Play or pause
     */
    fun playPause() {
        mediaController?.apply {
            if (isPlaying) {
                pause()
            } else {
                play()
            }
        }
    }
    
    /**
     * Play
     */
    fun play() {
        mediaController?.play()
    }
    
    /**
     * Pause
     */
    fun pause() {
        mediaController?.pause()
    }
    
    /**
     * Stop playback
     */
    fun stop() {
        mediaController?.stop()
    }
    
    /**
     * Skip to next track
     */
    fun skipToNext() {
        mediaController?.seekToNext()
    }
    
    /**
     * Skip to previous track
     */
    fun skipToPrevious() {
        mediaController?.seekToPrevious()
    }
    
    /**
     * Seek to position
     */
    fun seekTo(position: Long) {
        mediaController?.seekTo(position)
    }
    
    /**
     * Toggle shuffle mode
     */
    fun toggleShuffle() {
        mediaController?.apply {
            shuffleModeEnabled = !shuffleModeEnabled
        }
    }
    
    /**
     * Toggle repeat mode
     */
    fun toggleRepeatMode() {
        mediaController?.apply {
            repeatMode = when (repeatMode) {
                Player.REPEAT_MODE_OFF -> Player.REPEAT_MODE_ALL
                Player.REPEAT_MODE_ALL -> Player.REPEAT_MODE_ONE
                Player.REPEAT_MODE_ONE -> Player.REPEAT_MODE_OFF
                else -> Player.REPEAT_MODE_OFF
            }
        }
    }
    
    /**
     * Create MediaItem from Song
     */
    private fun createMediaItem(song: Song): MediaItem {
        val metadata = MediaMetadata.Builder()
            .setTitle(song.getDisplayTitle())
            .setArtist(song.getDisplayArtist())
            .setAlbumTitle(song.album)
            .build()
        
        return MediaItem.Builder()
            .setUri(song.path.toUri())
            .setMediaMetadata(metadata)
            .build()
    }
    
    /**
     * Release resources
     */
    fun release() {
        mediaController?.removeListener(playerListener)
        mediaControllerFuture?.let {
            MediaController.releaseFuture(it)
        }
    }
}

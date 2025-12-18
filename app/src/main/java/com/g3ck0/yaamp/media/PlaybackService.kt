package com.g3ck0.yaamp.media

import android.app.PendingIntent
import android.content.Intent
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.g3ck0.yaamp.presentation.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

/**
 * Media playback service using Media3
 */
@AndroidEntryPoint
class PlaybackService : MediaSessionService() {
    
    private var mediaSession: MediaSession? = null
    private lateinit var player: ExoPlayer
    
    override fun onCreate() {
        super.onCreate()
        Timber.d("PlaybackService created")
        
        initializePlayer()
        initializeMediaSession()
    }
    
    private fun initializePlayer() {
        // Create audio attributes for music playback
        val audioAttributes = AudioAttributes.Builder()
            .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
            .setUsage(C.USAGE_MEDIA)
            .build()
        
        // Initialize ExoPlayer
        player = ExoPlayer.Builder(this)
            .setAudioAttributes(audioAttributes, true)
            .setHandleAudioBecomingNoisy(true)
            .build()
        
        // Add player listener
        player.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                when (playbackState) {
                    Player.STATE_READY -> Timber.d("Player ready")
                    Player.STATE_ENDED -> Timber.d("Playback ended")
                    Player.STATE_BUFFERING -> Timber.d("Player buffering")
                    Player.STATE_IDLE -> Timber.d("Player idle")
                }
            }
            
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                Timber.d("Is playing: $isPlaying")
            }
        })
    }
    
    private fun initializeMediaSession() {
        // Create intent for opening the app when notification is clicked
        val sessionActivityIntent = Intent(this, MainActivity::class.java)
        val sessionActivityPendingIntent = PendingIntent.getActivity(
            this,
            0,
            sessionActivityIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        
        // Create media session
        mediaSession = MediaSession.Builder(this, player)
            .setSessionActivity(sessionActivityPendingIntent)
            .build()
    }
    
    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        return mediaSession
    }
    
    override fun onDestroy() {
        Timber.d("PlaybackService destroyed")
        mediaSession?.run {
            player.release()
            release()
            mediaSession = null
        }
        super.onDestroy()
    }
    
    override fun onTaskRemoved(rootIntent: Intent?) {
        // Stop the service when the task is removed
        val player = mediaSession?.player
        if (player?.playWhenReady == false) {
            stopSelf()
        }
    }
}

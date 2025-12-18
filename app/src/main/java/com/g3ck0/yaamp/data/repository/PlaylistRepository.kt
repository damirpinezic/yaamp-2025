package com.g3ck0.yaamp.data.repository

import com.g3ck0.yaamp.data.local.PlaylistDao
import com.g3ck0.yaamp.data.model.Playlist
import com.g3ck0.yaamp.data.model.PlaylistSongCrossRef
import com.g3ck0.yaamp.data.model.Song
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for managing playlists
 */
@Singleton
class PlaylistRepository @Inject constructor(
    private val playlistDao: PlaylistDao
) {
    
    /**
     * Get all playlists
     */
    fun getAllPlaylists(): Flow<List<Playlist>> = playlistDao.getAllPlaylists()
    
    /**
     * Get playlist by ID
     */
    suspend fun getPlaylistById(playlistId: Long): Playlist? = 
        playlistDao.getPlaylistById(playlistId)
    
    /**
     * Create new playlist
     */
    suspend fun createPlaylist(name: String): Long {
        val playlist = Playlist(name = name)
        return playlistDao.insertPlaylist(playlist)
    }
    
    /**
     * Update playlist
     */
    suspend fun updatePlaylist(playlist: Playlist) {
        playlistDao.updatePlaylist(playlist.copy(modifiedAt = System.currentTimeMillis()))
    }
    
    /**
     * Delete playlist
     */
    suspend fun deletePlaylist(playlist: Playlist) {
        playlistDao.deletePlaylist(playlist)
    }
    
    /**
     * Get songs in playlist
     */
    fun getSongsInPlaylist(playlistId: Long): Flow<List<Song>> = 
        playlistDao.getSongsInPlaylist(playlistId)
    
    /**
     * Add song to playlist
     */
    suspend fun addSongToPlaylist(playlistId: Long, songId: Long, position: Int = 0) {
        val crossRef = PlaylistSongCrossRef(playlistId, songId, position)
        playlistDao.insertPlaylistSong(crossRef)
    }
    
    /**
     * Remove song from playlist
     */
    suspend fun removeSongFromPlaylist(playlistId: Long, songId: Long) {
        val crossRef = PlaylistSongCrossRef(playlistId, songId)
        playlistDao.deletePlaylistSong(crossRef)
    }
    
    /**
     * Clear all songs from playlist
     */
    suspend fun clearPlaylist(playlistId: Long) {
        playlistDao.clearPlaylist(playlistId)
    }
}

package com.g3ck0.yaamp.data.local

import androidx.room.*
import com.g3ck0.yaamp.data.model.Playlist
import com.g3ck0.yaamp.data.model.PlaylistSongCrossRef
import com.g3ck0.yaamp.data.model.Song
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Playlist entities
 */
@Dao
interface PlaylistDao {
    
    @Query("SELECT * FROM playlists ORDER BY modifiedAt DESC")
    fun getAllPlaylists(): Flow<List<Playlist>>
    
    @Query("SELECT * FROM playlists WHERE id = :playlistId")
    suspend fun getPlaylistById(playlistId: Long): Playlist?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlist: Playlist): Long
    
    @Update
    suspend fun updatePlaylist(playlist: Playlist)
    
    @Delete
    suspend fun deletePlaylist(playlist: Playlist)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylistSong(crossRef: PlaylistSongCrossRef)
    
    @Delete
    suspend fun deletePlaylistSong(crossRef: PlaylistSongCrossRef)
    
    @Query("SELECT songs.* FROM songs INNER JOIN playlist_songs ON songs.id = playlist_songs.songId WHERE playlist_songs.playlistId = :playlistId ORDER BY playlist_songs.position")
    fun getSongsInPlaylist(playlistId: Long): Flow<List<Song>>
    
    @Query("DELETE FROM playlist_songs WHERE playlistId = :playlistId")
    suspend fun clearPlaylist(playlistId: Long)
}

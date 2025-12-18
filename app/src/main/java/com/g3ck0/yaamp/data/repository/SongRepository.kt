package com.g3ck0.yaamp.data.repository

import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore
import com.g3ck0.yaamp.data.local.SongDao
import com.g3ck0.yaamp.data.model.Song
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for managing songs data
 */
@Singleton
class SongRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val songDao: SongDao
) {
    
    /**
     * Get all songs from database
     */
    fun getAllSongs(): Flow<List<Song>> = songDao.getAllSongs()
    
    /**
     * Get favorite songs
     */
    fun getFavoriteSongs(): Flow<List<Song>> = songDao.getFavoriteSongs()
    
    /**
     * Search songs by query
     */
    fun searchSongs(query: String): Flow<List<Song>> = songDao.searchSongs(query)
    
    /**
     * Get song by ID
     */
    suspend fun getSongById(songId: Long): Song? = songDao.getSongById(songId)
    
    /**
     * Toggle favorite status of a song
     */
    suspend fun toggleFavorite(songId: Long, isFavorite: Boolean) {
        songDao.updateFavoriteStatus(songId, isFavorite)
    }
    
    /**
     * Scan device for audio files using MediaStore API
     */
    suspend fun scanAudioFiles(): List<Song> = withContext(Dispatchers.IO) {
        val songs = mutableListOf<Song>()
        
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.DATE_ADDED
        )
        
        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
        val sortOrder = "${MediaStore.Audio.Media.DATE_ADDED} DESC"
        
        try {
            context.contentResolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                null,
                sortOrder
            )?.use { cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
                val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
                val albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
                val albumIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)
                val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
                val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
                val dateAddedColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED)
                
                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn)
                    val title = cursor.getString(titleColumn) ?: ""
                    val artist = cursor.getString(artistColumn) ?: ""
                    val album = cursor.getString(albumColumn) ?: ""
                    val albumId = cursor.getLong(albumIdColumn)
                    val duration = cursor.getLong(durationColumn)
                    val path = cursor.getString(dataColumn) ?: ""
                    val dateAdded = cursor.getLong(dateAddedColumn)
                    
                    if (path.isNotEmpty()) {
                        songs.add(
                            Song(
                                id = id,
                                title = title,
                                artist = artist,
                                album = album,
                                albumId = albumId,
                                duration = duration,
                                path = path,
                                dateAdded = dateAdded
                            )
                        )
                    }
                }
                
                Timber.d("Scanned ${songs.size} audio files")
            }
        } catch (e: Exception) {
            Timber.e(e, "Error scanning audio files")
        }
        
        // Insert scanned songs into database
        if (songs.isNotEmpty()) {
            songDao.insertSongs(songs)
        }
        
        songs
    }
    
    /**
     * Get album art URI for a song
     */
    fun getAlbumArtUri(albumId: Long): android.net.Uri {
        return ContentUris.withAppendedId(
            android.net.Uri.parse("content://media/external/audio/albumart"),
            albumId
        )
    }
}

package com.g3ck0.yaamp.presentation.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.g3ck0.yaamp.data.model.Song
import com.g3ck0.yaamp.data.repository.SongRepository
import com.g3ck0.yaamp.media.PlaybackManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * ViewModel for the music library screen
 */
@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val songRepository: SongRepository,
    private val playbackManager: PlaybackManager
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<LibraryUiState>(LibraryUiState.Loading)
    val uiState: StateFlow<LibraryUiState> = _uiState.asStateFlow()
    
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    
    init {
        loadSongs()
    }
    
    /**
     * Load songs from repository
     */
    fun loadSongs() {
        viewModelScope.launch {
            try {
                _uiState.value = LibraryUiState.Loading
                
                songRepository.getAllSongs()
                    .catch { e ->
                        Timber.e(e, "Error loading songs")
                        _uiState.value = LibraryUiState.Error(e.message ?: "Unknown error")
                    }
                    .collect { songs ->
                        if (songs.isEmpty()) {
                            _uiState.value = LibraryUiState.Empty
                        } else {
                            _uiState.value = LibraryUiState.Success(songs)
                        }
                    }
            } catch (e: Exception) {
                Timber.e(e, "Error loading songs")
                _uiState.value = LibraryUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
    
    /**
     * Scan for audio files on device
     */
    fun scanAudioFiles() {
        viewModelScope.launch {
            try {
                _uiState.value = LibraryUiState.Loading
                val songs = songRepository.scanAudioFiles()
                Timber.d("Scanned ${songs.size} songs")
            } catch (e: Exception) {
                Timber.e(e, "Error scanning audio files")
                _uiState.value = LibraryUiState.Error(e.message ?: "Scan failed")
            }
        }
    }
    
    /**
     * Search songs
     */
    fun searchSongs(query: String) {
        _searchQuery.value = query
        viewModelScope.launch {
            if (query.isBlank()) {
                loadSongs()
            } else {
                songRepository.searchSongs(query)
                    .catch { e ->
                        Timber.e(e, "Error searching songs")
                        _uiState.value = LibraryUiState.Error(e.message ?: "Search failed")
                    }
                    .collect { songs ->
                        if (songs.isEmpty()) {
                            _uiState.value = LibraryUiState.Empty
                        } else {
                            _uiState.value = LibraryUiState.Success(songs)
                        }
                    }
            }
        }
    }
    
    /**
     * Play a song
     */
    fun playSong(song: Song) {
        playbackManager.playSong(song)
    }
    
    /**
     * Play all songs in library
     */
    fun playAll(startIndex: Int = 0) {
        viewModelScope.launch {
            val currentState = _uiState.value
            if (currentState is LibraryUiState.Success) {
                playbackManager.playQueue(currentState.songs, startIndex)
            }
        }
    }
    
    /**
     * Toggle favorite status
     */
    fun toggleFavorite(song: Song) {
        viewModelScope.launch {
            try {
                songRepository.toggleFavorite(song.id, !song.isFavorite)
                Timber.d("Toggled favorite for ${song.title}")
            } catch (e: Exception) {
                Timber.e(e, "Error toggling favorite")
            }
        }
    }
}

/**
 * UI state for library screen
 */
sealed class LibraryUiState {
    object Loading : LibraryUiState()
    object Empty : LibraryUiState()
    data class Success(val songs: List<Song>) : LibraryUiState()
    data class Error(val message: String) : LibraryUiState()
}

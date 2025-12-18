package com.g3ck0.yaamp.presentation.main

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.g3ck0.yaamp.R
import com.g3ck0.yaamp.databinding.ActivityMainBinding
import com.g3ck0.yaamp.presentation.library.LibraryUiState
import com.g3ck0.yaamp.presentation.library.LibraryViewModel
import com.g3ck0.yaamp.presentation.library.SongAdapter
import com.g3ck0.yaamp.util.PermissionUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * Main activity for YAAMP
 */
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    private val viewModel: LibraryViewModel by viewModels()
    
    private val songAdapter = SongAdapter(
        onSongClick = { song -> viewModel.playSong(song) },
        onFavoriteClick = { song -> viewModel.toggleFavorite(song) }
    )
    
    // Permission launcher for audio files
    private val audioPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Timber.d("Audio permission granted")
            viewModel.scanAudioFiles()
        } else {
            Timber.w("Audio permission denied")
            showPermissionDeniedDialog()
        }
    }
    
    // Permission launcher for notifications (Android 13+)
    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Timber.d("Notification permission granted")
        } else {
            Timber.w("Notification permission denied")
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        // Install splash screen
        installSplashScreen()
        
        super.onCreate(savedInstanceState)
        
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupToolbar()
        setupRecyclerView()
        setupFab()
        observeViewModel()
        
        // Request permissions
        requestPermissions()
    }
    
    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = getString(R.string.music_library)
    }
    
    private fun setupRecyclerView() {
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = songAdapter
        }
    }
    
    private fun setupFab() {
        binding.fab.setOnClickListener {
            // Play all songs
            val currentState = viewModel.uiState.value
            if (currentState is LibraryUiState.Success && currentState.songs.isNotEmpty()) {
                viewModel.playAll()
                Toast.makeText(this, "Playing all songs", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    updateUi(state)
                }
            }
        }
    }
    
    private fun updateUi(state: LibraryUiState) {
        binding.progressBar.isVisible = state is LibraryUiState.Loading
        binding.emptyView.isVisible = state is LibraryUiState.Empty
        binding.errorView.isVisible = state is LibraryUiState.Error
        binding.recyclerView.isVisible = state is LibraryUiState.Success
        
        when (state) {
            is LibraryUiState.Success -> {
                songAdapter.submitList(state.songs)
                binding.fab.show()
            }
            is LibraryUiState.Empty -> {
                binding.emptyText.text = getString(R.string.no_songs_found)
                binding.fab.hide()
            }
            is LibraryUiState.Error -> {
                binding.errorText.text = state.message
                binding.fab.hide()
            }
            is LibraryUiState.Loading -> {
                binding.fab.hide()
            }
        }
    }
    
    private fun requestPermissions() {
        // Check audio permission
        if (!PermissionUtils.hasAudioPermission(this)) {
            if (shouldShowRequestPermissionRationale(PermissionUtils.getAudioPermission())) {
                showPermissionRationaleDialog()
            } else {
                audioPermissionLauncher.launch(PermissionUtils.getAudioPermission())
            }
        } else {
            // Permission already granted, scan for audio files
            viewModel.scanAudioFiles()
            
            // Request notification permission if needed (Android 13+)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (!PermissionUtils.hasNotificationPermission(this)) {
                    notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        }
    }
    
    private fun showPermissionRationaleDialog() {
        AlertDialog.Builder(this)
            .setTitle(R.string.permission_audio_title)
            .setMessage(R.string.permission_audio_message)
            .setPositiveButton(R.string.grant_permission) { _, _ ->
                audioPermissionLauncher.launch(PermissionUtils.getAudioPermission())
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }
    
    private fun showPermissionDeniedDialog() {
        AlertDialog.Builder(this)
            .setTitle(R.string.permission_denied)
            .setMessage(R.string.permission_required)
            .setPositiveButton(R.string.settings) { _, _ ->
                openAppSettings()
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }
    
    private fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", packageName, null)
        }
        startActivity(intent)
    }
}

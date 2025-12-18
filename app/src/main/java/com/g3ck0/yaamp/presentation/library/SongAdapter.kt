package com.g3ck0.yaamp.presentation.library

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.g3ck0.yaamp.R
import com.g3ck0.yaamp.data.model.Song
import com.g3ck0.yaamp.databinding.ItemSongBinding

/**
 * RecyclerView adapter for displaying songs
 */
class SongAdapter(
    private val onSongClick: (Song) -> Unit,
    private val onFavoriteClick: (Song) -> Unit
) : ListAdapter<Song, SongAdapter.SongViewHolder>(SongDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val binding = ItemSongBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SongViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    inner class SongViewHolder(
        private val binding: ItemSongBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onSongClick(getItem(position))
                }
            }
            
            binding.favoriteButton.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onFavoriteClick(getItem(position))
                }
            }
        }
        
        fun bind(song: Song) {
            binding.apply {
                titleText.text = song.getDisplayTitle()
                artistText.text = song.getDisplayArtist()
                durationText.text = song.getFormattedDuration()
                
                // Load album art using Coil
                albumArt.load(getAlbumArtUri(song.albumId)) {
                    crossfade(true)
                    placeholder(R.drawable.ic_music_note)
                    error(R.drawable.ic_music_note)
                }
                
                // Update favorite icon
                favoriteButton.setImageResource(
                    if (song.isFavorite) {
                        R.drawable.ic_favorite_filled
                    } else {
                        R.drawable.ic_favorite_border
                    }
                )
            }
        }
        
        private fun getAlbumArtUri(albumId: Long): String {
            return "content://media/external/audio/albumart/$albumId"
        }
    }
    
    private class SongDiffCallback : DiffUtil.ItemCallback<Song>() {
        override fun areItemsTheSame(oldItem: Song, newItem: Song): Boolean {
            return oldItem.id == newItem.id
        }
        
        override fun areContentsTheSame(oldItem: Song, newItem: Song): Boolean {
            return oldItem == newItem
        }
    }
}

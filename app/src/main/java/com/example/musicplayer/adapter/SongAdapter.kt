package com.example.musicplayer.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.musicplayer.databinding.ItemLayoutBinding
import com.example.musicplayer.fragment.MusicListFragmentDirections
import com.example.musicplayer.model.Song

class SongAdapter : ListAdapter<Song, SongAdapter.SongViewHolder>(DiffCallBack()) {

    private class DiffCallBack: DiffUtil.ItemCallback<Song>() {
        override fun areItemsTheSame(oldItem: Song, newItem: Song): Boolean {
            return oldItem.songTitle == newItem.songTitle
        }

        override fun areContentsTheSame(oldItem: Song, newItem: Song): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        return SongViewHolder(
            ItemLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        holder.bind(getItem(position), position)
    }

    inner class SongViewHolder(private val binding: ItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(song: Song, pos: Int) {
            binding.apply {
                tvDuration.text = song.songDuration
                songTitle.text = song.songTitle
                songArtist.text = song.songArtist
                tvOrder.text = "${pos.plus(1)}"
            }
            itemView.setOnClickListener {
                val direction = MusicListFragmentDirections.actionMusicListFragmentToPlayMusicFragment(song)
                it.findNavController().navigate(direction)
            }
        }
    }
}
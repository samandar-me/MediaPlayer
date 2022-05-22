package com.example.musicplayer.fragment

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.navArgs
import com.example.musicplayer.R
import com.example.musicplayer.databinding.FragmentPlayMusicBinding
import com.example.musicplayer.model.Song
import com.example.musicplayer.util.Constants
import com.example.musicplayer.util.Constants.durationConverter

class PlayMusicFragment : Fragment() {

    private var _binding: FragmentPlayMusicBinding? = null
    private val binding get() = _binding!!
    private val args: PlayMusicFragmentArgs by navArgs()
    private lateinit var song: Song
    private var mediaPlayer: MediaPlayer? = null
    private var seekLength: Int = 0
    private val seekForwardTime = 5000
    private val seekBackwardTime = 5000

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlayMusicBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        song = args.song!!
        initViews()

    }

    private fun initViews() {
        mediaPlayer = MediaPlayer()
        binding.apply {
            tvAuthor.text = song.songArtist
            tvTitle.text = song.songTitle
            tvDuration.text = song.songDuration
        }

        playSong()

        binding.ibPlay.setOnClickListener {
            playSong()
        }
        binding.ibForwardSong.setOnClickListener {
            forwardSong()
        }
        binding.ibBackwardSong.setOnClickListener {
            backForwardSong()
        }
        binding.ibRepeat.setOnClickListener {
            repeatSong()
        }
        displaySongArt()
    }

    private fun displaySongArt() {
        val mediaDataRetriever = MediaMetadataRetriever()
        mediaDataRetriever.setDataSource(song.songUri)
        val data = mediaDataRetriever.embeddedPicture
        if (data != null) {
            val bitmap = BitmapFactory.decodeByteArray(
                data,
                0,
                data.size
            )
            binding.ibCover.setImageBitmap(bitmap)
        }
    }

    private fun forwardSong() {
        if (mediaPlayer != null) {
            val currentPosition = mediaPlayer!!.currentPosition
            if (currentPosition + seekForwardTime <= mediaPlayer!!.duration) {
                mediaPlayer!!.seekTo(currentPosition + seekForwardTime)
            } else {
                mediaPlayer!!.seekTo(mediaPlayer!!.duration)
            }
        }
    }

    private fun backForwardSong() {
        if (mediaPlayer != null) {
            val currentPosition = mediaPlayer!!.currentPosition
            if (currentPosition - seekBackwardTime >= 0) {
                mediaPlayer!!.seekTo(currentPosition - seekBackwardTime)
            } else {
                mediaPlayer!!.seekTo(0)
            }
        }
    }

    private fun repeatSong() {
        if (!mediaPlayer!!.isLooping) {
            mediaPlayer!!.isLooping = true
            binding.ibRepeat.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.ic_repeat_white
                )
            )
        } else {
            mediaPlayer!!.isLooping = false
            binding.ibRepeat.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.ic_repeat
                )
            )
        }
    }

    private fun playSong() {
        if (!mediaPlayer!!.isPlaying) {
            mediaPlayer!!.reset()
            mediaPlayer!!.setDataSource(song.songUri)
            mediaPlayer!!.prepare()
            mediaPlayer!!.seekTo(seekLength)
            mediaPlayer!!.start()

            binding.ibPlay.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.ic_pause
                )
            )
            updateSeekBar()
        } else {
            mediaPlayer!!.pause()
            seekLength = mediaPlayer!!.currentPosition
            binding.ibPlay.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.ic_play
                )
            )
        }
    }

    private fun updateSeekBar() {
        if (mediaPlayer != null) {
            binding.tvCurrentTime.text =
                Constants.durationConverter(mediaPlayer!!.currentPosition.toLong())
        }
        seekBarSetup()
        Handler(Looper.getMainLooper()!!).postDelayed(runnable, 50)
    }

    private fun seekBarSetup() {
        if (mediaPlayer != null) {
            binding.seekBar.progress = mediaPlayer!!.currentPosition
            binding.seekBar.max = mediaPlayer!!.duration
        }

        binding.seekBar.setOnSeekBarChangeListener(@SuppressLint("AppCompatCustomView")
        object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    mediaPlayer!!.seekTo(progress)
                    binding.tvCurrentTime.text = durationConverter(progress.toLong())
                }
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                if (mediaPlayer!!.isPlaying) {
                    if (seekBar != null) {
                        mediaPlayer!!.seekTo(seekBar.progress)
                    }
                }
            }
        })
    }

    private fun clearMediaPlayer() {
        if (mediaPlayer!!.isPlaying) {
            mediaPlayer!!.stop()
        }
        mediaPlayer!!.release()
        mediaPlayer = null
    }

    private var runnable = Runnable { updateSeekBar() }

    override fun onDestroy() {
        super.onDestroy()
        clearMediaPlayer()
    }
}
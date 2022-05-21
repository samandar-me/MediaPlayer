package com.example.musicplayer.fragment

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musicplayer.R
import com.example.musicplayer.adapter.SongAdapter
import com.example.musicplayer.databinding.FragmentMusicListBinding
import com.example.musicplayer.model.Song
import com.example.musicplayer.util.Constants
import com.example.musicplayer.util.toast

@RequiresApi(Build.VERSION_CODES.R)
class MusicListFragment : Fragment() {

    private var _binding: FragmentMusicListBinding? = null
    private val binding get() = _binding!!
    private var songList: MutableList<Song> = ArrayList()
    private lateinit var songAdapter: SongAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMusicListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    private fun initViews() {
        //permissionSetup()
        songAdapter = SongAdapter()
        loadSongs()
        setupRv()
        checkUserPermissions()
    }

    private fun setupRv() = binding.rvSongList.apply {
        adapter = songAdapter
        layoutManager = LinearLayoutManager(requireContext())
        setHasFixedSize(true)
        songAdapter.submitList(songList)
        songList.clear()
    }


    private fun loadSongs() {
        val allSongsURI = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val selection = MediaStore.Audio.Media.IS_MUSIC + "!=0"
        val sortOrder = " ${MediaStore.Audio.Media.DISPLAY_NAME} ASC"

        val cursor = activity?.applicationContext?.contentResolver!!.query(
            allSongsURI, null, selection, null, sortOrder
        )

        if (cursor != null) {

            while (cursor.moveToNext()) {
                val songURI =
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA))
                val songAuthor =
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST))
                val songName =
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME))
                val songDuration =
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION))
                val songDurLong = songDuration.toLong()
                songList.add(
                    Song(
                        songName, songAuthor,
                        songURI, Constants.durationConverter(songDurLong)
                    )
                )
            }
            cursor.close()
        }
    }

    private fun permissionSetup() {
        val permission = ContextCompat.checkSelfPermission(
            requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE
        )

        if (permission != PackageManager.PERMISSION_GRANTED) {
            permissionsResultCallback.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        } else {
            loadSongs()
        }
    }

    private val permissionsResultCallback = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        if (it) toast("Permission has been granted by user")
        else toast("Permission denied")
    }

    private fun checkUserPermissions() {
        if (activity?.let {
                ActivityCompat.checkSelfPermission(
                    it,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                )
            }
            != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                101
            )
            return
        }
        loadSongs()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            101-> if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadSongs()
            } else {
                toast("Permission Denied, Add permission!!")
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
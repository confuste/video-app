package com.alexfuster.videoapp.ui.player

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.alexfuster.videoapp.databinding.FragmentPlayerBinding


class PlayerFragment : Fragment() {

    private var _binding: FragmentPlayerBinding? = null
    private val binding
        get() = _binding!!

    private val args: PlayerFragmentArgs by navArgs()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        _binding = FragmentPlayerBinding.inflate(inflater, container, false)

        return binding.root
    }


    override fun onResume() {
        super.onResume()
        with(binding) {
            videoview.setMediaController(MediaController(requireContext()));
            val uri: Uri = Uri.parse(args.playerFilePath)
            videoview.setVideoURI(uri)
            videoview.requestFocus()
            videoview.start()
        }
    }
}
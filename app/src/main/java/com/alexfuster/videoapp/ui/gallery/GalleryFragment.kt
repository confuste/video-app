package com.alexfuster.videoapp.ui.gallery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavDirections
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.alexfuster.videoapp.R
import com.alexfuster.videoapp.app.constants.FileConfig
import com.alexfuster.videoapp.app.constants.UIConfig
import com.alexfuster.videoapp.databinding.FragmentGalleryBinding
import com.alexfuster.videoapp.ui.gallery.recyclerview.ItemModel
import com.alexfuster.videoapp.ui.gallery.recyclerview.RecyclerViewAdapter
import com.alexfuster.videoapp.ui.main.MainActivity
import com.alexfuster.videoapp.ui.utils.toast
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint



@AndroidEntryPoint
class GalleryFragment : Fragment() {

    private  val  viewModel: GalleryViewModel by viewModels()


    private var _binding: FragmentGalleryBinding? = null
    private val binding
        get() = _binding!!


    private lateinit var adapter : RecyclerViewAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        _binding = FragmentGalleryBinding.inflate(inflater, container, false)
        activity?.window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
        (activity as MainActivity).supportActionBar?.show()

        subscribeViewModelObservers()
        initViewsObserver()

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


    private fun subscribeViewModelObservers() {
        viewModel.fileList.observe(viewLifecycleOwner, androidx.lifecycle.Observer { fileList ->
            setRecyclerViewObserver(fileList)
        })

        viewModel.fileDeleted.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                deleteFileObject ->
            deleteFileObserver(deleteFileObject.isDelete, deleteFileObject.position)
        })
    }

    private fun initViewsObserver() {
        viewModel.loadFiles(requireContext(), FileConfig.VIDEO_MIME_TYPE)
    }

    private fun setRecyclerViewObserver(videoList: List<ItemModel>) {

        binding.rvVideoList.layoutManager =
            GridLayoutManager(context, UIConfig.NUMBER_OF_COLUMNS_ON_GALLERY)

        adapter = RecyclerViewAdapter(videoList.toMutableList(),
            this::playListener, this::menuListener)
        binding.rvVideoList.adapter = adapter
        adapter.notifyDataSetChanged()

        showRecyclerView(adapter.itemCount)
        setToolBarTitle(adapter.itemCount)
    }

    private fun showRecyclerView(itemNumber: Int) {
        binding.rvVideoList.visibility = if(itemNumber > 0) View.VISIBLE else View.INVISIBLE
    }

    private fun setToolBarTitle(itemNumber: Int) {
        val toolBarTitle: String = requireContext().getString(R.string.gallery_item_number)
        val message: String = toolBarTitle.replace("#NUMBER#", itemNumber.toString())
        binding.toolbar.title = message
    }

    private fun deleteFileObserver (isFileDeleted: Boolean, position: Int) {
        val message= if(isFileDeleted){
            adapter.removeByPosition(position)
            R.string.gallery_file_removed
        } else {
            R.string.gallery_file_removed_fail
        }

        showRecyclerView(adapter.itemCount)
        setToolBarTitle(adapter.itemCount)
        requireContext().toast(message)

    }

    private fun playListener(item: ItemModel) {
        goToPlayer(item.videoPath)
    }

    private fun goToPlayer(videoPath: String) {

        val action: NavDirections = GalleryFragmentDirections
            .actionGalleryFragmentToPlayerFragment(videoPath)
        binding.rvVideoList.findNavController().navigate(action)
    }

    private fun removeFile(filePath: String, position: Int) {
        viewModel.removeFile(filePath, position)
    }

    private fun menuListener(position: Int, item: ItemModel, anchor: View) {

        val popup = PopupMenu(context, anchor)
        popup.menuInflater.inflate(R.menu.video_menu, popup.menu)
        popup.setOnMenuItemClickListener {

            when (it.itemId) {
                R.id.delete_video-> {
                    MaterialAlertDialogBuilder(context)
                        .setTitle(R.string.gallery_dialog_title)
                        .setMessage(R.string.gallery_dialog_message)
                        .setNegativeButton(R.string.common_cancel) { _, _ -> }
                        .setPositiveButton(R.string.common_accept) { _, _ ->
                            removeFile(item.videoPath, position)
                        }
                        .show()
                    true
                }
                else -> false
            }
        }
        popup.show()
    }


}
package com.alexfuster.videoapp.ui.gallery.recyclerview

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.alexfuster.videoapp.R
import com.alexfuster.videoapp.app.constants.UIConfig
import com.alexfuster.videoapp.databinding.CellGalleryViewItemBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target


class RecyclerViewAdapter(private var itemList: MutableList<ItemModel>,
                          private val playListener: (ItemModel) -> Unit,
                          private val menuListener: (Int, ItemModel, View) -> Unit) :  RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerViewAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.cell_gallery_view_item, parent, false)

        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: RecyclerViewAdapter.ViewHolder, position: Int) =
        holder.bind(itemList[position], playListener, menuListener, position)


    override fun getItemCount(): Int = itemList.size


    fun add(itemModel: ItemModel) {
        itemList.add(itemModel)
        notifyDataSetChanged()
    }

    fun removeLast() {
        itemList = itemList.dropLast(1).toMutableList()
        notifyDataSetChanged()
    }

    fun removeByPosition(position: Int) {
        itemList.removeAt(position)
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val binding = CellGalleryViewItemBinding.bind(view)
        private val pixels = calculateWidthPixels(view.context) / UIConfig.NUMBER_OF_COLUMNS_ON_GALLERY

        fun bind(itemModel: ItemModel,
                 playListener: (ItemModel) -> Unit,
                 menuListener: (Int, ItemModel, View) -> Unit,
                 position: Int) = with(binding) {

            ivPlay.visibility = View.INVISIBLE
            ivContextualMenu.visibility = View.INVISIBLE

            val thumbnailViewParams: ViewGroup.LayoutParams = binding.ivThumbnail.layoutParams
            thumbnailViewParams.width = pixels
            thumbnailViewParams.height = pixels
            ivThumbnail.layoutParams = thumbnailViewParams


            Glide.with(ivThumbnail.context)
                .load(itemModel.uri)
                //.override(pixels, pixels)
                //.centerCrop()
                .thumbnail(0.1f)
                .listener(object : RequestListener<Drawable>{
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean): Boolean { return false }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean): Boolean {

                        ivPlay.visibility = View.VISIBLE
                        ivContextualMenu.visibility = View.VISIBLE

                        return false
                    }
                })
                .into(ivThumbnail)


            ivContextualMenu.setOnClickListener { menuListener(position, itemModel, it) }
            ivPlay.setOnClickListener{ playListener(itemModel) }
        }


        private fun calculateWidthPixels(context: Context): Int {
            val displayMetrics: DisplayMetrics = context.resources.displayMetrics
            return displayMetrics.widthPixels
        }

    }

}



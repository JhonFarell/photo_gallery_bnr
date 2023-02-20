package com.kek.photo_gallery_bnr.main_screen

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.kek.photo_gallery_bnr.Constances.IF_EMPTY_URL
import com.kek.photo_gallery_bnr.R
import com.kek.photo_gallery_bnr.api.GalleryItem
import com.kek.photo_gallery_bnr.databinding.ListItemGalleryBinding

class PhotoViewHolder(
    private val binding: ListItemGalleryBinding
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(galleryItem: GalleryItem) {
        binding.itemImageView.load(galleryItem.url) {
            placeholder(R.drawable.bill_up_close)
        }
    }
}

class PhotoViewAdapter(
    private val galleryItems: List<GalleryItem>
) : RecyclerView.Adapter<PhotoViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemGalleryBinding.inflate(inflater, parent, false)
        return PhotoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val item = galleryItems[position]

        if (item.url.isNullOrBlank()){
            item.url = IF_EMPTY_URL
            holder.bind(item)
        } else {
        holder.bind(item)
        }
    }

    override fun getItemCount() = galleryItems.size
}
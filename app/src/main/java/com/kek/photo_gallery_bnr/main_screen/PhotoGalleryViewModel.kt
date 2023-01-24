package com.kek.photo_gallery_bnr.main_screen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kek.photo_gallery_bnr.api.GalleryItem
import com.kek.photo_gallery_bnr.api.PhotoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PhotoGalleryViewModel: ViewModel() {
    private val _galleryItems: MutableStateFlow<List<GalleryItem>> =
        MutableStateFlow(emptyList())
    val galleryItem: StateFlow<List<GalleryItem>>
    get() = _galleryItems.asStateFlow()

    init {
        viewModelScope.launch {
            try {
                val items = PhotoRepository().fetchPhotos()
                Log.d("Response", "Responce recived: $items")
                _galleryItems.value = items
            } catch (ex: java.lang.Exception) {
                Log.d ("No connection", "No connection")
            }
        }
    }
}
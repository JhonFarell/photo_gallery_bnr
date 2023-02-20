package com.kek.photo_gallery_bnr.main_screen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kek.photo_gallery_bnr.Constances.LOG_TAG_PHOTO_VIEW_MODEL
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
                val items = fetchGalleryItems("")
                _galleryItems.value = items
            } catch (ex: Exception) {
                Log.d ("No connection", "No connection")
            }
        }
    }

    fun setQuery(query: String) {
        viewModelScope.launch {
            _galleryItems.value = fetchGalleryItems(query)
            Log.d("VM","Query was set")
        }
    }

    private suspend fun fetchGalleryItems(query: String): List<GalleryItem> {

        return if (query.isNotEmpty()) {
            Log.d(LOG_TAG_PHOTO_VIEW_MODEL, "searchPhotos fun launched")
            PhotoRepository().searchPhotos(query)
        } else {
            Log.d(LOG_TAG_PHOTO_VIEW_MODEL, "fetchPhotos fun launched")
            PhotoRepository().fetchPhotos()
        }
    }
}
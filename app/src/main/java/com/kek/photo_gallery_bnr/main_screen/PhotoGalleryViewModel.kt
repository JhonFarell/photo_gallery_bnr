package com.kek.photo_gallery_bnr.main_screen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kek.photo_gallery_bnr.api.GalleryItem
import com.kek.photo_gallery_bnr.api.PhotoRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class PhotoGalleryViewModel: ViewModel() {
    private val _uiState: MutableStateFlow<PhotoGalleryUiState> =
        MutableStateFlow(PhotoGalleryUiState())
    val uiState: StateFlow<PhotoGalleryUiState>
        get() = _uiState.asStateFlow()

    private val _isLoading: MutableStateFlow<Boolean> =
        MutableStateFlow(false)
    val isLoading: StateFlow<Boolean>
        get() = _isLoading.asStateFlow()

    private val photoRepository = PhotoRepository()
    private val preferenceRepository = PreferenceRepository.get()

    init {
        viewModelScope.launch {
            preferenceRepository.storedQuery.collectLatest {storedQuery ->
                try {
                    val items = fetchGalleryItems(storedQuery)
                    _uiState.update {oldState ->
                        oldState.copy(
                            images = items,
                            searchState = storedQuery
                        )
                    }
                } catch (ex: Exception) {
                    Log.d("No connection", "No connection")
                }
            }
        }
    }
        fun setQuery(query: String) {
            viewModelScope.launch {
                preferenceRepository.setStoredQuery(query)
            }
        }

        fun toogleIsPoling(){
            viewModelScope.launch {
                preferenceRepository.setPoling(!uiState.value.isPoling)
            }
        }

        private suspend fun fetchGalleryItems(query: String): List<GalleryItem> {
            _isLoading.value = true

            val response = if (query.isNotEmpty()) {
                photoRepository.searchPhotos(query)
            } else {
                photoRepository.fetchPhotos()
            }

            _isLoading.value = false

            return response
        }

}

data class PhotoGalleryUiState (
    val images: List<GalleryItem> = listOf(),
    val searchState: String = "",
    val isPoling: Boolean = false
        )
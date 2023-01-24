package com.kek.photo_gallery_bnr.api

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class FlickrResponse(
    val photos: PhotoResponse
)

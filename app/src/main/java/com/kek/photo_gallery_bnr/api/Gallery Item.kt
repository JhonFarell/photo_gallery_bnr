package com.kek.photo_gallery_bnr.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass (generateAdapter = true)
data class GalleryItem(
    val title: String,
    val id: String,
    @Json (name = "url_s") var url: String?
)

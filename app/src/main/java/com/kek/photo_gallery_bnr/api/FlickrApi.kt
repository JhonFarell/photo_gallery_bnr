package com.kek.photo_gallery_bnr.api

import com.kek.photo_gallery_bnr.api.model.FlickrResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface FlickrApi {
    @GET("services/rest/?method=flickr.interestingness.getList")
    suspend fun fetchPhotos(): FlickrResponse

    @GET("services/rest?method=flickr.photos.search")
    suspend fun searchPhotos(
        @Query("text") query: String
    ): FlickrResponse


}
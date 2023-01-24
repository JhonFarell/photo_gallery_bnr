package com.kek.photo_gallery_bnr.api

import retrofit2.http.GET

private const val apiKey = "cd9aa56a3f01185560065032fbe25228"

interface FlickrApi {
    @GET("services/rest/?method=flickr.interestingness.getList" +
            "&api_key=$apiKey" +
            "&format=json" +
            "&nojsoncallback=1" +
            "&extras=url_s")
    suspend fun fetchPhotos(): FlickrResponse


}
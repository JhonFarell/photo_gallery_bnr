package com.kek.photo_gallery_bnr.api

import com.kek.photo_gallery_bnr.utils.Constances.API_KEY
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class PhotoInterceptor: Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        val newUrl = originalRequest.url.newBuilder()
            .addQueryParameter("api_key", API_KEY)
            .addQueryParameter("format","json")
            .addQueryParameter("nojsoncallback","1")
            .addQueryParameter("extras", "url_s")
            .addQueryParameter("safesearch", "0")
            .build()

        val newRequest: Request = originalRequest.newBuilder()
            .url(newUrl)
            .build()

        return chain.proceed(newRequest)
    }

}
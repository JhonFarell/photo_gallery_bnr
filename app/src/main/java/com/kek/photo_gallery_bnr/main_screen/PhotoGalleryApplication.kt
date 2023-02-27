package com.kek.photo_gallery_bnr.main_screen

import android.app.Application

class PhotoGalleryApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        PreferenceRepository.initialize(this)
    }
}
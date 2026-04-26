package com.example.newsapp

import android.app.Application
import com.google.firebase.FirebaseApp

class NewsApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}
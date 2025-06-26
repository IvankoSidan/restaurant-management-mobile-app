package com.example.myfirstapp.App

import android.app.Application
import com.example.myfirstapp.DI.appModule
import com.example.myfirstapp.DI.networkModule
import org.koin.core.context.startKoin
import org.koin.android.ext.koin.androidContext



class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MyApplication)
            modules(appModule, networkModule)
        }
    }
}

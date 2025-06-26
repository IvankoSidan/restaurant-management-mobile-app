package com.example.myfirstapp.Utils

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.module.AppGlideModule
import java.io.InputStream

@GlideModule
class MyAppGlideModule : AppGlideModule() {
    override fun isManifestParsingEnabled(): Boolean = false

    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        val client = UnsafeOkHttpClient.getUnsafeOkHttpClient()
        val factory = OkHttpUrlLoader.Factory(client)
        val modelClass = GlideUrl::class.java
        val dataClass = InputStream::class.java
        registry.replace(modelClass, dataClass, factory)
    }
}

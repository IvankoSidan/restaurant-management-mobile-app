package com.example.myfirstapp.Impl

import android.content.Context
import com.example.myfirstapp.Interfaces.StringProvider

class StringProviderImpl(private val context: Context) : StringProvider {
    override fun getStringResource(resId: Int): String {
        return context.getString(resId)
    }
}

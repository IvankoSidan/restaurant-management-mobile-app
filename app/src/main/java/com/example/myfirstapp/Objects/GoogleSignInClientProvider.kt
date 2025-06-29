package com.example.myfirstapp.Objects

import androidx.fragment.app.FragmentActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

object GoogleSignInClientProvider {
    private const val CLIENT_ID = "996553786812-uflh419kc8oe8lmq5ai0tqdlfae1kmra.apps.googleusercontent.com"

    fun getClient(activity: FragmentActivity): GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(CLIENT_ID)
            .requestEmail()
            .build()
        return GoogleSignIn.getClient(activity, gso)
    }
}

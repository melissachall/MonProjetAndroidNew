package com.travel.buddy

import ui.app.App
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import data.AuthService
import data.getAuthRepository
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    companion object {
        const val GOOGLE_SIGN_IN_REQUEST_CODE = 555
        const val GOOGLE_CLIENT_ID = "465940245644-sb0jk308em524l64igdpdruolbhk11fg.apps.googleusercontent.com" // <= Mets ici ton clientId Firebase
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            AuthService.setActivity(this)
            setContent { App() }
        } catch (e: Exception) {
            e.printStackTrace() // logcat
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GOOGLE_SIGN_IN_REQUEST_CODE) {
            if (data != null) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                try {
                    val account = task.getResult(ApiException::class.java)
                    val idToken = account.idToken
                    if (idToken != null) {

                        CoroutineScope(Dispatchers.Main).launch {
                            val repo = getAuthRepository()
                            val result = repo.signInWithGoogleIdToken(idToken)
                            // Ici tu peux gérer la navigation ou l'affichage d'un message selon le succès
                            // (Par exemple, via une callback, un ViewModel, ou un StateFlow)
                        }
                    } else {
                        // idToken est null, gérer l'erreur ici (snackbar, etc)
                    }
                } catch (e: ApiException) {
                    // Erreur Google Sign-In, gérer ici (snackbar, etc)
                }
            }
        }
    }
}
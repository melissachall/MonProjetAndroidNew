package data

import android.app.Activity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

actual object AuthService {

    private var currentActivity: Activity? = null

    actual fun setActivity(activity: Any) {
        currentActivity = activity as? Activity
            ?: throw IllegalArgumentException("Invalid activity type")
    }

    actual fun getCurrentActivity(): Any? = currentActivity

    // Méthode pour lancer Google Sign-In
    actual fun launchGoogleSignIn(
        clientId: String,
        requestCode: Int // Pas de valeur par défaut ici !
    ) {
        val activity = currentActivity
            ?: throw IllegalStateException("Activity is not set")
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(clientId)
            .requestEmail()
            .build()
        val googleSignInClient = GoogleSignIn.getClient(activity, gso)
        val signInIntent = googleSignInClient.signInIntent
        activity.startActivityForResult(signInIntent, requestCode)
    }
}
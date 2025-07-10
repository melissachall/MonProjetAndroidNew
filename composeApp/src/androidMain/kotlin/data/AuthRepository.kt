package data

import android.app.Activity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit

actual class AuthRepository {

    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    actual suspend fun signInWithEmail(email: String, password: String): Result<Unit> =
        runCatching { auth.signInWithEmailAndPassword(email, password).await() }.map { Unit }

    actual suspend fun signUpWithEmail(email: String, password: String): Result<String> =
        runCatching {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            result.user?.uid ?: throw IllegalStateException("User ID not found")
        }

    actual suspend fun sendPasswordResetEmail(email: String): Result<Unit> =
        runCatching { auth.sendPasswordResetEmail(email).await() }.map { Unit }

    actual suspend fun confirmPasswordReset(code: String, newPassword: String): Result<Unit> =
        runCatching { auth.confirmPasswordReset(code, newPassword).await() }.map { Unit }

    actual fun sendVerificationCode(
        phoneNumber: String,
        onVerificationCompleted: (credential: Any) -> Unit,
        onVerificationFailed: (Exception) -> Unit,
        onCodeSent: (verificationId: String) -> Unit
    ) {
        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                onVerificationCompleted(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                onVerificationFailed(e)
            }

            override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                onCodeSent(verificationId)
            }
        }

        val activity = AuthService.getCurrentActivity() as? Activity
            ?: throw IllegalStateException("Activity not set. Call AuthService.setActivity()")

        val options = PhoneAuthOptions.newBuilder(FirebaseAuth.getInstance())
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(callbacks)
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    actual suspend fun signInWithPhoneAuthCredential(credential: Any): Result<Unit> =
        runCatching {
            val realCred = credential as? PhoneAuthCredential
                ?: throw IllegalArgumentException("Invalid credential type")
            auth.signInWithCredential(realCred).await()
        }.map { Unit }

    actual fun signOut() {
        auth.signOut()
    }

    actual fun getCurrentUserEmail(): String? = auth.currentUser?.email
}

actual fun getAuthRepository(): AuthRepository = AuthRepository()

actual suspend fun checkIfUserExists(phoneNumber: String): Boolean {
    val firestore = FirebaseFirestore.getInstance()
    val result = firestore.collection("users")
        .whereEqualTo("phoneNumber", phoneNumber)
        .get()
        .await()
    return !result.isEmpty
}
package data

expect class AuthRepository {

    suspend fun signInWithEmail(email: String, password: String): Result<Unit>
    suspend fun signUpWithEmail(email: String, password: String): Result<String>
    suspend fun sendPasswordResetEmail(email: String): Result<Unit>
    suspend fun confirmPasswordReset(code: String, newPassword: String): Result<Unit>

    fun sendVerificationCode(
        phoneNumber: String,
        onVerificationCompleted: (credential: Any) -> Unit,
        onVerificationFailed: (Exception) -> Unit,
        onCodeSent: (verificationId: String) -> Unit
    )

    suspend fun signInWithPhoneAuthCredential(credential: Any): Result<Unit>
    fun signOut()
    fun getCurrentUserEmail(): String?
}

expect fun getAuthRepository(): AuthRepository
expect suspend fun checkIfUserExists(phoneNumber: String): Boolean
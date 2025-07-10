package data

actual class AuthRepository {

    actual suspend fun signInWithEmail(email: String, password: String): Result<Unit> =
        Result.failure(NotImplementedError("Not supported on Desktop"))

    actual suspend fun signUpWithEmail(email: String, password: String): Result<Unit> =
        Result.failure(NotImplementedError("Not supported on Desktop"))

    actual suspend fun sendPasswordResetEmail(email: String): Result<Unit> =
        Result.failure(NotImplementedError("Not supported on Desktop"))

    actual suspend fun confirmPasswordReset(code: String, newPassword: String): Result<Unit> =
        Result.failure(NotImplementedError("Not supported on Desktop"))

    actual fun sendVerificationCode(phoneNumber: String, callbacks: Any) {
        throw NotImplementedError("Not supported on Desktop")
    }

    actual suspend fun signInWithPhoneAuthCredential(credential: Any): Result<Unit> =
        Result.failure(NotImplementedError("Not supported on Desktop"))

    actual fun signOut() {}

    actual fun getCurrentUserEmail(): String? = null
}

actual fun getAuthRepository(): AuthRepository = AuthRepository()

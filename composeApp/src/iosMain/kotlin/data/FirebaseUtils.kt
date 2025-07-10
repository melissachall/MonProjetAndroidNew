package data

actual object FirebaseUtils {

    actual fun createUserInFirestore(
        uid: String,
        firstName: String,
        lastName: String,
        phoneNumber: String,
        email: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        // Stub ou TODO
        println("createUserInFirestore is not implemented on this platform.")
        onFailure(Exception("Not implemented on this platform"))
    }

    actual fun sendVerificationCode(
        phoneNumber: String,
        callbacks: Any,
        onError: (Exception) -> Unit
    ) {
        // Stub ou TODO
        println("sendVerificationCode is not implemented on this platform.")
        onError(Exception("Not implemented on this platform"))
    }
}
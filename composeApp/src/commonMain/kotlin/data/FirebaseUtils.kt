package data

expect object FirebaseUtils {

    fun createUserInFirestore(
        uid: String,
        firstName: String,
        lastName: String,
        phoneNumber: String,
        email: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    )
}
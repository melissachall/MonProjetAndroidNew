package data

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore

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
        val firestore = FirebaseFirestore.getInstance()
        val userData = hashMapOf(
            "uid" to uid,
            "firstName" to firstName,
            "lastName" to lastName,
            "phoneNumber" to "+213$phoneNumber",
            "email" to email,
            "photoUrl" to "",
            "bio" to "",
            "birthDate" to "",
            "nationality" to "Algérienne",
            "createdAt" to Timestamp.now()
        )

        firestore.collection("users")
            .document(uid)
            .set(userData)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure(e) }
    }
}
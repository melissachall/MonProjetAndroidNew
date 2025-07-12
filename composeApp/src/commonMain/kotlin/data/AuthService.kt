package data

expect object AuthService {
    fun setActivity(activity: Any)
    fun getCurrentActivity(): Any?
    fun launchGoogleSignIn(clientId: String, requestCode: Int = 555)
}
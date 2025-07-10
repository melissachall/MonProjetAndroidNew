package data

expect object AuthService {
    fun setActivity(activity: Any)
    fun getCurrentActivity(): Any?
}
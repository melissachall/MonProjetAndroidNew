package data

import android.app.Activity

actual object AuthService {

    private var currentActivity: Activity? = null

    actual fun setActivity(activity: Any) {
        currentActivity = activity as? Activity
            ?: throw IllegalArgumentException("Invalid activity type")
        this.currentActivity = activity
    }

    actual fun getCurrentActivity(): Any? = currentActivity
}
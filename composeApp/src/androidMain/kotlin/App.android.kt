package ui.app

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext


@Composable
actual fun openUrl(url: String?) {
    url?.let {
        val context = LocalContext.current
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(it))
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    }
}

actual fun ByteArray.toComposeImageBitmap(): ImageBitmap {
    val bitmap = BitmapFactory.decodeByteArray(this, 0, this.size)
    return bitmap.asImageBitmap()
}
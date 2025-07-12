package data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.InetSocketAddress
import java.net.Socket

/**
 * Utilitaire multiplateforme pour tester la connexion internet.
 * Retourne true si le réseau est accessible.
 */
suspend fun isNetworkAvailable(): Boolean = withContext(Dispatchers.IO) {
    try {
        Socket().use { socket ->
            socket.connect(InetSocketAddress("8.8.8.8", 53), 1500)
            true
        }
    } catch (e: Exception) {
        false
    }
}
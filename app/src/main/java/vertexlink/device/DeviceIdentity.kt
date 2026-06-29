package vertexlink.device

import android.content.Context
import androidx.core.content.edit
import java.util.UUID

class DeviceIdentity(context: Context) {

  private val prefs = context.getSharedPreferences("vertexlink", Context.MODE_PRIVATE)

  fun getId(): String {
    val existing = prefs.getString("device_id", null)

    if (existing != null) {
      return existing
    }

    val newId = UUID.randomUUID().toString()
    prefs.edit { putString("device_id", newId) }

    return newId
  }
}
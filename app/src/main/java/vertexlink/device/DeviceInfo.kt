package vertexlink.device

import android.content.Context
import android.os.Build
import android.provider.Settings
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeviceInfo @Inject constructor(@ApplicationContext private val context: Context) {
  fun getDeviceName(): String {
    var deviceName = Settings.Global.getString(context.contentResolver, Settings.Global.DEVICE_NAME)

    if (deviceName.isNullOrBlank()) {
      deviceName = Settings.Secure.getString(context.contentResolver, "bluetooth_name")
    }

    if (deviceName.isNullOrBlank()) {
      val manufacturer = Build.MANUFACTURER
      val model = Build.MODEL

      deviceName = if (model.lowercase().startsWith(manufacturer.lowercase())) {
        model
      } else {
        "$manufacturer $model"
      }
    }

    return deviceName
  }
}
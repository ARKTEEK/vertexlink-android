package vertexlink.store

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import vertexlink.model.KeyCodes
import vertexlink.model.Macro
import java.util.UUID
import androidx.core.content.edit

private const val PREFS_NAME = "vertexlink_macros"
private const val KEY_MACROS = "macros"

class MacroStore(context: Context) {
  private val prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

  fun getAll(): List<Macro> {
    val raw = prefs.getString(KEY_MACROS, null) ?: return defaultMacros().also { save(it) }

    return try {
      val array = JSONArray(raw)

      (0 until array.length()).map { i ->
        val obj = array.getJSONObject(i)
        val codes = obj.getJSONArray("keyCodes")

        Macro(
          id = obj.getString("id"),
          name = obj.getString("name"),
          keyCodes = (0 until codes.length()).map { codes.getInt(it) }
        )
      }
    } catch (e: Exception) {
      defaultMacros()
    }
  }

  fun add(macro: Macro) {
    save(getAll() + macro)
  }

  fun remove(macroId: String) {
    save(getAll().filterNot { it.id == macroId })
  }

  private fun save(macros: List<Macro>) {
    val array = JSONArray()

    macros.forEach { macro ->
      val obj = JSONObject()
      obj.put("id", macro.id)
      obj.put("name", macro.name)
      obj.put("keyCodes", JSONArray(macro.keyCodes))
      array.put(obj)
    }

    prefs.edit { putString(KEY_MACROS, array.toString()) }
  }

  private fun defaultMacros(): List<Macro> = listOf(
    Macro(UUID.randomUUID().toString(), "Copy", listOf(KeyCodes.VK_CONTROL, KeyCodes.vkForLetter('C'))),
    Macro(UUID.randomUUID().toString(), "Paste", listOf(KeyCodes.VK_CONTROL, KeyCodes.vkForLetter('V'))),
    Macro(UUID.randomUUID().toString(), "Cut", listOf(KeyCodes.VK_CONTROL, KeyCodes.vkForLetter('X'))),
    Macro(UUID.randomUUID().toString(), "Undo", listOf(KeyCodes.VK_CONTROL, KeyCodes.vkForLetter('Z'))),
    Macro(UUID.randomUUID().toString(), "Select All", listOf(KeyCodes.VK_CONTROL, KeyCodes.vkForLetter('A'))),
  )
}
package vertexlink.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.vertexlink.ui.theme.VertexColors
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import vertexlink.controller.AudioController
import vertexlink.controller.ClipboardController
import vertexlink.controller.KeyboardController
import vertexlink.controller.MouseController
import vertexlink.model.ClipboardEntry
import vertexlink.model.Macro
import vertexlink.ui.components.common.ImmersiveLandscapeEffect
import vertexlink.ui.components.controlpanel.ControlRail
import vertexlink.ui.components.controlpanel.popup.GesturesInfoPopup
import vertexlink.ui.components.controlpanel.TouchpadArea
import vertexlink.ui.components.controlpanel.popup.ClipboardPopup
import vertexlink.ui.components.controlpanel.popup.VolumePopup

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ControlPanel(
  onDisconnect: () -> Unit,
  mouseController: MouseController,
  keyboardController: KeyboardController,
  audioController: AudioController,
  clipboardController: ClipboardController,
  macros: List<Macro>,
  onAddMacro: (Macro) -> Unit,
  onDeleteMacro: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  var showKeyboard by remember { mutableStateOf(false) }
  var showVolumePopup by remember { mutableStateOf(false) }
  var showInfoPopup by remember { mutableStateOf(false) }
  var showClipboardPopup by remember { mutableStateOf(false) }
  var volume by remember { mutableIntStateOf(50) }
  var isMuted by remember { mutableStateOf(false) }
  var clipboardText by remember { mutableStateOf("") }
  var desktopHistory by remember { mutableStateOf<List<ClipboardEntry>>(emptyList()) }
  var isMenuOpen by remember { mutableStateOf(false) }

  val coroutineScope = rememberCoroutineScope()
  val context = LocalContext.current
  val isImeVisible = WindowInsets.isImeVisible
  val phoneHistory by clipboardController.localHistory.collectAsState()

  fun showToast(message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
  }

  suspend fun refreshDesktopHistory(fillEditor: Boolean) {
    val entries = clipboardController.fetchDesktopHistory() ?: return

    desktopHistory = entries

    if (fillEditor) {
      entries.firstOrNull()?.let { newest -> clipboardText = newest.text }
    }
  }

  fun pushToDesktop(text: String, append: Boolean) {
    if (text.isBlank()) {
      return
    }

    if (append) {
      clipboardController.appendToDesktop(text)
    } else {
      clipboardController.sendToDesktop(text)
    }

    showToast(if (append) "Appended to PC clipboard" else "Sent to PC clipboard")

    coroutineScope.launch {
      delay(700)
      refreshDesktopHistory(fillEditor = false)
    }
  }

  fun copyToPhone(text: String) {
    if (text.isBlank()) {
      return
    }

    clipboardController.writeLocalClipboard(text)
    showToast("Copied to phone clipboard")
  }

  LaunchedEffect(showVolumePopup) {
    if (showVolumePopup) {
      val audioState = audioController.fetchState()

      if (audioState != null) {
        volume = audioState.volume
        isMuted = audioState.isMuted
      }
    }
  }

  LaunchedEffect(showClipboardPopup) {
    if (showClipboardPopup) {
      clipboardController.readLocalClipboard()
      refreshDesktopHistory(fillEditor = true)
    }
  }

  ImmersiveLandscapeEffect()

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(VertexColors.BgSurfaceMid)
      .windowInsetsPadding(WindowInsets.displayCutout)
      .imePadding()
      .padding(16.dp)
  ) {
    TouchpadArea(
      mouseController = mouseController
    )

    Row(
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      modifier = Modifier
        .fillMaxSize()
        .padding(8.dp)
    ) {
      Box(
        modifier = Modifier
          .weight(1f)
          .fillMaxHeight()
      ) {
        if (showKeyboard) {
          KeyboardPanel(
            keyboardController = keyboardController,
            macros = macros,
            onAddMacro = onAddMacro,
            onDeleteMacro = onDeleteMacro,
            onClose = { showKeyboard = false },
            modifier = Modifier
              .align(Alignment.BottomStart)
              .fillMaxWidth()
          )
        }

        if (showVolumePopup) {
          VolumePopup(
            volume = volume,
            isMuted = isMuted,
            onVolumeChange = { newVolume ->
              volume = newVolume
              audioController.setVolume(newVolume)
            },
            onMuteChange = { newMuted ->
              isMuted = newMuted
              audioController.setMuted(newMuted)
            },
            modifier = Modifier.align(Alignment.TopEnd)
          )
        }

        if (showClipboardPopup) {
          ClipboardPopup(
            text = clipboardText,
            onTextChange = { clipboardText = it },
            desktopHistory = desktopHistory,
            phoneHistory = phoneHistory,
            onRefresh = {
              coroutineScope.launch {
                clipboardController.readLocalClipboard()
                refreshDesktopHistory(fillEditor = false)
              }
            },
            onClearDesktopHistory = {
              clipboardController.clearDesktopHistory()
              desktopHistory = emptyList()
            },
            onClearPhoneHistory = { clipboardController.clearLocalHistory() },
            onCopyEntryToPhone = { entry -> copyToPhone(entry.text) },
            onSendEntryToDesktop = { entry -> pushToDesktop(entry.text, append = false) },
            onSendToDesktop = { pushToDesktop(clipboardText, append = false) },
            onAppendToDesktop = { pushToDesktop(clipboardText, append = true) },
            onCopyToPhone = { copyToPhone(clipboardText) },
            onPasteFromPhone = {
              clipboardController.readLocalClipboard()?.let { localText -> clipboardText = localText }
            },
            modifier = Modifier.align(Alignment.TopEnd)
          )
        }

        if (showInfoPopup) {
          GesturesInfoPopup(
            modifier = Modifier.align(Alignment.TopEnd)
          )
        }
      }

      if (!isImeVisible) {
        ControlRail(
          isMenuOpen = isMenuOpen,
          onToggleMenu = { isMenuOpen = !isMenuOpen },
          onDisconnect = onDisconnect,
          onToggleKeyboard = {
            showKeyboard = !showKeyboard
            if (showKeyboard) {
              showVolumePopup = false
              showInfoPopup = false
            }
          },
          onToggleClipboard = {
            showClipboardPopup = !showClipboardPopup
            if (showClipboardPopup) {
              showKeyboard = false
              showVolumePopup = false
              showInfoPopup = false
            }
          },
          onToggleVolume = {
            showVolumePopup = !showVolumePopup
            if (showVolumePopup) {
              showKeyboard = false
              showInfoPopup = false
            }
          },
          onToggleInfo = {
            showInfoPopup = !showInfoPopup
            if (showInfoPopup) {
              showKeyboard = false
              showVolumePopup = false
            }
          }
        )
      }
    }
  }
}
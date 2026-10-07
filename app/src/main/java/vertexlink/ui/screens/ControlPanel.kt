package vertexlink.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import com.vertexlink.ui.theme.VertexBrushes
import kotlinx.coroutines.delay
import vertexlink.controller.AudioController
import vertexlink.controller.ClipboardController
import vertexlink.controller.KeyboardController
import vertexlink.controller.MouseController
import vertexlink.model.ClipboardEntry
import vertexlink.model.Macro
import vertexlink.ui.components.common.ImmersiveLandscapeEffect
import vertexlink.ui.components.common.KeepScreenOnEffect
import vertexlink.ui.components.controlpanel.ControlRail
import vertexlink.ui.components.controlpanel.ControlSection
import vertexlink.ui.components.controlpanel.TouchpadArea
import vertexlink.ui.components.controlpanel.popup.ClipboardPopup
import vertexlink.ui.components.controlpanel.popup.GesturesInfoPopup
import vertexlink.ui.components.controlpanel.popup.VolumePopup
import kotlin.time.Duration.Companion.milliseconds

private const val VOLUME_AUTO_CLOSE_MS = 5_000L
private const val CLIPBOARD_POLL_MS = 2_000L

@Composable
fun ControlPanel(
  keepScreenOn: Boolean,
  onDisconnect: () -> Unit,
  mouseController: MouseController,
  keyboardController: KeyboardController,
  audioController: AudioController,
  clipboardController: ClipboardController,
  macros: List<Macro>,
  onAddMacro: (Macro) -> Unit,
  onDeleteMacro: (String) -> Unit,
  touchpadSensitivity: Float = 1f,
  modifier: Modifier = Modifier
) {
  KeepScreenOnEffect(enabled = keepScreenOn)

  var activeSection by remember { mutableStateOf<ControlSection?>(null) }
  var volume by remember { mutableIntStateOf(50) }
  var isMuted by remember { mutableStateOf(false) }
  var desktopHistory by remember { mutableStateOf<List<ClipboardEntry>>(emptyList()) }
  var clipboardDraft by remember { mutableStateOf("") }

  val context = LocalContext.current
  val softwareKeyboard = LocalSoftwareKeyboardController.current
  val phoneHistory by clipboardController.localHistory.collectAsState()

  fun showToast(message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
  }

  fun openSection(next: ControlSection?) {
    if (activeSection == ControlSection.Keyboard && next != ControlSection.Keyboard) {
      softwareKeyboard?.hide()
    }

    activeSection = next
  }

  fun toggleSection(section: ControlSection) {
    openSection(if (activeSection == section) null else section)
  }

  LaunchedEffect(activeSection) {
    when (activeSection) {
      ControlSection.Volume -> {
        val audioState = audioController.fetchState()

        if (audioState != null) {
          volume = audioState.volume
          isMuted = audioState.isMuted
        }
      }

      ControlSection.Clipboard -> {
        clipboardController.readLocalClipboard()

        while (true) {
          clipboardController.fetchDesktopHistory()?.let { entries -> desktopHistory = entries }

          delay(CLIPBOARD_POLL_MS.milliseconds)
        }
      }

      else -> Unit
    }
  }

  LaunchedEffect(activeSection, volume, isMuted) {
    if (activeSection == ControlSection.Volume) {
      delay(VOLUME_AUTO_CLOSE_MS.milliseconds)
      activeSection = null
    }
  }

  ImmersiveLandscapeEffect()

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(VertexBrushes.Control)
      .windowInsetsPadding(WindowInsets.displayCutout)
      .imePadding()
      .padding(16.dp)
  ) {
    TouchpadArea(
      mouseController = mouseController,
      sensitivity = touchpadSensitivity
    )

    Row(
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier
        .fillMaxSize()
        .padding(8.dp)
    ) {
      Box(
        modifier = Modifier
          .weight(1f)
          .fillMaxHeight()
      ) {
        if (activeSection == ControlSection.Keyboard) {
          KeyboardPanel(
            keyboardController = keyboardController,
            macros = macros,
            onAddMacro = onAddMacro,
            onDeleteMacro = onDeleteMacro,
            onClose = { openSection(null) },
            modifier = Modifier
              .align(Alignment.BottomStart)
              .fillMaxWidth()
          )
        }
      }

      when (activeSection) {
        ControlSection.Clipboard -> {
          ClipboardPopup(
            draft = clipboardDraft,
            onDraftChange = { clipboardDraft = it },
            desktopHistory = desktopHistory,
            phoneHistory = phoneHistory,
            onCopyToPhone = { entry ->
              clipboardController.writeLocalClipboard(entry.text)
              showToast("Copied to phone")
            },
            onSendToDesktop = { entry ->
              clipboardController.sendToDesktop(entry.text)
              showToast("Sent to PC")
            },
            onSaveDraftToPhone = {
              clipboardController.writeLocalClipboard(clipboardDraft)
              clipboardDraft = ""
              showToast("Saved to phone clipboard")
            },
            onSaveDraftToDesktop = {
              clipboardController.sendToDesktop(clipboardDraft)
              clipboardDraft = ""
              showToast("Saved to PC clipboard")
            },
            onClearDesktopHistory = {
              clipboardController.clearDesktopHistory()
              desktopHistory = emptyList()
            },
            onClearPhoneHistory = { clipboardController.clearLocalHistory() }
          )
        }

        ControlSection.Volume -> {
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
            }
          )
        }

        ControlSection.Info -> {
          GesturesInfoPopup()
        }

        else -> Unit
      }

      ControlRail(
        activeSection = activeSection,
        isMuted = isMuted,
        onToggleSection = { section -> toggleSection(section) },
        onDisconnect = onDisconnect,
        modifier = Modifier.align(Alignment.CenterVertically)
      )
    }
  }
}
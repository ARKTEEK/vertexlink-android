package vertexlink.ui.screens

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vertexlink.ui.theme.VertexColors
import vertexlink.controller.KeyboardController
import vertexlink.controller.MouseController
import vertexlink.model.Macro
import vertexlink.ui.components.common.ImmersiveLandscapeEffect
import vertexlink.ui.components.controlpanel.ControlRail
import vertexlink.ui.components.controlpanel.popup.GesturesInfoPopup
import vertexlink.ui.components.controlpanel.TouchpadArea
import vertexlink.ui.components.controlpanel.popup.VolumePopup

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ControlPanel(
  onDisconnect: () -> Unit,
  mouseController: MouseController,
  keyboardController: KeyboardController,
  macros: List<Macro>,
  onAddMacro: (Macro) -> Unit,
  onDeleteMacro: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  var showKeyboard by remember { mutableStateOf(false) }
  var showVolumePopup by remember { mutableStateOf(false) }
  var showInfoPopup by remember { mutableStateOf(false) }
  var volume by remember { mutableFloatStateOf(50f) }
  var isMenuOpen by remember { mutableStateOf(false) }

  val isImeVisible = WindowInsets.isImeVisible

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
            onVolumeChange = { volume = it },
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
          onSyncClipboard = { },
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
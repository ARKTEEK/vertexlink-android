package vertexlink.ui.components.controlpanel.popup

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.PlaylistAdd
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.outlined.Computer
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.ContentPaste
import androidx.compose.material.icons.outlined.DeleteSweep
import androidx.compose.material.icons.outlined.PhoneAndroid
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vertexlink.ui.theme.VertexColors
import vertexlink.model.ClipboardEntry
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private enum class HistoryTab(val label: String, val icon: ImageVector) {
  DESKTOP("PC", Icons.Outlined.Computer),
  PHONE("Phone", Icons.Outlined.PhoneAndroid)
}

@Composable
fun ClipboardPopup(
  text: String,
  onTextChange: (String) -> Unit,
  desktopHistory: List<ClipboardEntry>,
  phoneHistory: List<ClipboardEntry>,
  onRefresh: () -> Unit,
  onClearDesktopHistory: () -> Unit,
  onClearPhoneHistory: () -> Unit,
  onCopyEntryToPhone: (ClipboardEntry) -> Unit,
  onSendEntryToDesktop: (ClipboardEntry) -> Unit,
  onSendToDesktop: () -> Unit,
  onAppendToDesktop: () -> Unit,
  onCopyToPhone: () -> Unit,
  onPasteFromPhone: () -> Unit,
  modifier: Modifier = Modifier
) {
  var selectedTab by remember { mutableStateOf(HistoryTab.DESKTOP) }

  Row(
    horizontalArrangement = Arrangement.spacedBy(12.dp),
    modifier = modifier
      .clip(RoundedCornerShape(16.dp))
      .background(VertexColors.BgSurfaceHigh.copy(alpha = 0.95f))
      .border(1.dp, VertexColors.BorderSubtle, RoundedCornerShape(16.dp))
      .padding(12.dp)
  ) {
    Column(
      verticalArrangement = Arrangement.spacedBy(8.dp),
      modifier = Modifier.width(200.dp)
    ) {
      Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
          .fillMaxWidth()
          .height(32.dp)
      ) {
        HistoryTab.entries.forEach { tab ->
          TabButton(
            tab = tab,
            selected = tab == selectedTab,
            onClick = { selectedTab = tab },
            modifier = Modifier.weight(1f)
          )
        }

        SmallIconButton(
          icon = Icons.Outlined.Refresh,
          contentDescription = "Refresh history",
          onClick = onRefresh
        )

        SmallIconButton(
          icon = Icons.Outlined.DeleteSweep,
          contentDescription = "Clear this history",
          onClick = if (selectedTab == HistoryTab.DESKTOP) onClearDesktopHistory else onClearPhoneHistory
        )
      }

      val isDesktopTab = selectedTab == HistoryTab.DESKTOP
      val entries = if (isDesktopTab) desktopHistory else phoneHistory

      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(144.dp)
          .clip(RoundedCornerShape(8.dp))
          .background(VertexColors.BgSurfaceLow)
          .border(1.dp, VertexColors.BorderSubtle, RoundedCornerShape(8.dp))
      ) {
        if (entries.isEmpty()) {
          Text(
            text = if (isDesktopTab) {
              "Nothing copied on the PC yet"
            } else {
              "Nothing copied on the phone yet"
            },
            fontSize = 11.sp,
            color = VertexColors.TextPrimary.copy(alpha = 0.5f),
            modifier = Modifier
              .align(Alignment.Center)
              .padding(12.dp)
          )
        } else {
          LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(entries, key = { it.id }) { entry ->
              HistoryRow(
                entry = entry,
                isDesktop = isDesktopTab,
                onSelect = { onTextChange(entry.text) },
                onTransfer = {
                  if (isDesktopTab) onCopyEntryToPhone(entry) else onSendEntryToDesktop(entry)
                }
              )
            }
          }
        }
      }
    }

    Column(
      verticalArrangement = Arrangement.spacedBy(8.dp),
      modifier = Modifier.width(200.dp)
    ) {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(80.dp)
          .clip(RoundedCornerShape(8.dp))
          .background(VertexColors.BgSurfaceLow)
          .border(1.dp, VertexColors.BorderSubtle, RoundedCornerShape(8.dp))
          .padding(8.dp)
      ) {
        BasicTextField(
          value = text,
          onValueChange = onTextChange,
          textStyle = TextStyle(color = VertexColors.TextPrimary, fontSize = 13.sp),
          cursorBrush = SolidColor(VertexColors.AccentPrimary),
          modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
        )
      }

      Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        ActionChip(
          icon = Icons.AutoMirrored.Outlined.Send,
          label = "Send to PC",
          onClick = onSendToDesktop,
          modifier = Modifier.weight(1f)
        )

        ActionChip(
          icon = Icons.AutoMirrored.Outlined.PlaylistAdd,
          label = "Append to PC",
          onClick = onAppendToDesktop,
          modifier = Modifier.weight(1f)
        )
      }

      Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        ActionChip(
          icon = Icons.Outlined.ContentCopy,
          label = "Copy to phone",
          onClick = onCopyToPhone,
          modifier = Modifier.weight(1f)
        )

        ActionChip(
          icon = Icons.Outlined.ContentPaste,
          label = "Paste from phone",
          onClick = onPasteFromPhone,
          modifier = Modifier.weight(1f)
        )
      }
    }
  }
}

@Composable
private fun TabButton(
  tab: HistoryTab,
  selected: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val shape = RoundedCornerShape(8.dp)

  Row(
    horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally),
    verticalAlignment = Alignment.CenterVertically,
    modifier = modifier
      .fillMaxSize()
      .clip(shape)
      .background(if (selected) VertexColors.AccentPrimary.copy(alpha = 0.2f) else VertexColors.BgSurfaceLow)
      .border(1.dp, if (selected) VertexColors.AccentPrimary else VertexColors.BorderSubtle, shape)
      .clickable(onClick = onClick)
      .padding(horizontal = 4.dp)
  ) {
    Icon(
      imageVector = tab.icon,
      contentDescription = null,
      tint = if (selected) VertexColors.AccentPrimary else VertexColors.TextPrimary.copy(alpha = 0.7f),
      modifier = Modifier.size(14.dp)
    )

    Text(
      text = tab.label,
      fontSize = 11.sp,
      maxLines = 1,
      color = VertexColors.TextPrimary
    )
  }
}

@Composable
private fun SmallIconButton(
  icon: ImageVector,
  contentDescription: String,
  onClick: () -> Unit
) {
  Box(
    contentAlignment = Alignment.Center,
    modifier = Modifier
      .size(32.dp)
      .clip(RoundedCornerShape(8.dp))
      .clickable(onClick = onClick)
  ) {
    Icon(
      imageVector = icon,
      contentDescription = contentDescription,
      tint = VertexColors.TextPrimary.copy(alpha = 0.8f),
      modifier = Modifier.size(18.dp)
    )
  }
}

@Composable
private fun HistoryRow(
  entry: ClipboardEntry,
  isDesktop: Boolean,
  onSelect: () -> Unit,
  onTransfer: () -> Unit
) {
  val time = remember(entry.timestamp) {
    SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(entry.timestamp))
  }

  Row(
    horizontalArrangement = Arrangement.spacedBy(6.dp),
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier
      .fillMaxWidth()
      .clickable(onClick = onSelect)
      .padding(horizontal = 8.dp, vertical = 6.dp)
  ) {
    Column(modifier = Modifier.weight(1f)) {
      Text(
        text = entry.text.replace('\n', ' ').trim(),
        fontSize = 12.sp,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis,
        color = VertexColors.TextPrimary
      )

      Text(
        text = time,
        fontSize = 10.sp,
        color = VertexColors.TextPrimary.copy(alpha = 0.5f)
      )
    }

    Row(
      horizontalArrangement = Arrangement.spacedBy(3.dp),
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier
        .clip(RoundedCornerShape(12.dp))
        .background(VertexColors.AccentPrimary.copy(alpha = 0.15f))
        .clickable(onClick = onTransfer)
        .padding(horizontal = 6.dp, vertical = 4.dp)
    ) {
      Icon(
        imageVector = if (isDesktop) Icons.Outlined.PhoneAndroid else Icons.Outlined.Computer,
        contentDescription = if (isDesktop) "Copy to phone" else "Send to PC",
        tint = VertexColors.AccentPrimary,
        modifier = Modifier.size(14.dp)
      )

      Text(
        text = if (isDesktop) "To phone" else "To PC",
        fontSize = 10.sp,
        maxLines = 1,
        color = VertexColors.AccentPrimary
      )
    }
  }
}

@Composable
private fun ActionChip(
  icon: ImageVector,
  label: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val shape = RoundedCornerShape(10.dp)

  Row(
    horizontalArrangement = Arrangement.spacedBy(4.dp),
    verticalAlignment = Alignment.CenterVertically,
    modifier = modifier
      .height(44.dp)
      .clip(shape)
      .background(VertexColors.BgSurfaceLow)
      .border(1.dp, VertexColors.BorderSubtle, shape)
      .clickable(onClick = onClick)
      .padding(horizontal = 6.dp)
  ) {
    Icon(
      imageVector = icon,
      contentDescription = null,
      tint = VertexColors.AccentPrimary,
      modifier = Modifier.size(16.dp)
    )

    Text(
      text = label,
      fontSize = 11.sp,
      lineHeight = 12.sp,
      maxLines = 2,
      color = VertexColors.TextPrimary
    )
  }
}
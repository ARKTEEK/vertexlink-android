package vertexlink.ui.components.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.vertexlink.ui.theme.MonoLabelStyle
import com.vertexlink.ui.theme.VertexColors
import vertexlink.ui.components.common.CaptionStrip

@Composable
fun SettingsCard(
  title: String,
  modifier: Modifier = Modifier,
  content: @Composable ColumnScope.() -> Unit
) {
  Column(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(18.dp))
      .background(VertexColors.Card)
  ) {
    CaptionStrip(title = title)

    content()
  }
}

@Composable
fun SettingsDivider() {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 14.dp)
      .height(1.dp)
      .background(VertexColors.BorderSubtle)
  )
}

@Composable
fun SettingsSwitchRow(
  title: String,
  subtitle: String,
  checked: Boolean,
  onCheckedChange: (Boolean) -> Unit,
  modifier: Modifier = Modifier
) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = modifier
      .fillMaxWidth()
      .clickable { onCheckedChange(!checked) }
      .padding(horizontal = 14.dp, vertical = 12.dp)
  ) {
    SettingsText(title = title, subtitle = subtitle, modifier = Modifier.weight(1f))

    Spacer(modifier = Modifier.width(12.dp))

    Switch(
      checked = checked,
      onCheckedChange = onCheckedChange,
      colors = SwitchDefaults.colors(
        checkedThumbColor = Color.White,
        checkedTrackColor = VertexColors.Magenta,
        checkedBorderColor = VertexColors.Magenta,
        uncheckedThumbColor = VertexColors.TextMuted,
        uncheckedTrackColor = VertexColors.BgSurfaceHigh,
        uncheckedBorderColor = VertexColors.BorderStrong
      )
    )
  }
}

@Composable
fun SettingsValueRow(
  title: String,
  subtitle: String,
  value: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = modifier
      .fillMaxWidth()
      .clickable(onClick = onClick)
      .padding(horizontal = 14.dp, vertical = 12.dp)
  ) {
    Column(modifier = Modifier.weight(1f)) {
      SettingsText(title = title, subtitle = subtitle)

      Spacer(modifier = Modifier.height(6.dp))

      Text(
        text = value,
        style = MonoLabelStyle,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )
    }

    Spacer(modifier = Modifier.width(12.dp))

    Icon(
      imageVector = Icons.Outlined.Edit,
      contentDescription = "Edit $title",
      tint = VertexColors.Magenta,
      modifier = Modifier.size(20.dp)
    )
  }
}

@Composable
fun SettingsSliderRow(
  title: String,
  subtitle: String,
  value: Float,
  valueLabel: String,
  valueRange: ClosedFloatingPointRange<Float>,
  steps: Int,
  onValueChange: (Float) -> Unit,
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 14.dp, vertical = 12.dp)
  ) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      SettingsText(title = title, subtitle = subtitle, modifier = Modifier.weight(1f))

      Spacer(modifier = Modifier.width(12.dp))

      Text(text = valueLabel, style = MonoLabelStyle)
    }

    Slider(
      value = value,
      onValueChange = onValueChange,
      valueRange = valueRange,
      steps = steps,
      colors = SliderDefaults.colors(
        thumbColor = VertexColors.Magenta,
        activeTrackColor = VertexColors.Magenta,
        inactiveTrackColor = VertexColors.BlushDeep,
        activeTickColor = Color.Transparent,
        inactiveTickColor = Color.Transparent
      )
    )
  }
}

@Composable
private fun SettingsText(
  title: String,
  subtitle: String,
  modifier: Modifier = Modifier
) {
  Column(modifier = modifier) {
    Text(
      text = title,
      style = MaterialTheme.typography.bodyMedium,
      color = VertexColors.TextPrimary
    )

    Spacer(modifier = Modifier.height(2.dp))

    Text(
      text = subtitle,
      style = MaterialTheme.typography.bodySmall,
      color = VertexColors.TextSecondary
    )
  }
}

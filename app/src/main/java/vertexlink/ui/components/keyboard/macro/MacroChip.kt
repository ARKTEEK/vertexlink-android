package vertexlink.ui.components.keyboard.macro

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vertexlink.ui.theme.VertexColors
import vertexlink.model.Macro

@Composable
fun MacroChip(
  macro: Macro,
  onRun: () -> Unit,
  onDelete: () -> Unit
) {
  var showDelete by remember { mutableStateOf(false) }

  Box {
    OutlinedButton(
      onClick = onRun,
      modifier = Modifier.fillMaxWidth()
    ) {
      Text(text = macro.name, maxLines = 1, style = MaterialTheme.typography.labelMedium)
    }

    IconButton(
      onClick = { showDelete = true },
      modifier = Modifier
        .align(Alignment.TopEnd)
        .size(18.dp)
    ) {
      Icon(
        imageVector = Icons.Outlined.Delete,
        contentDescription = "Delete macro",
        tint = VertexColors.TextSecondary,
        modifier = Modifier.size(12.dp)
      )
    }
  }

  if (showDelete) {
    AlertDialog(
      onDismissRequest = { showDelete = false },
      title = { Text("Delete \"${macro.name}\"?") },
      confirmButton = {
        TextButton(onClick = {
          onDelete()
          showDelete = false
        }) { Text("Delete") }
      },
      dismissButton = {
        TextButton(onClick = { showDelete = false }) { Text("Cancel") }
      }
    )
  }
}
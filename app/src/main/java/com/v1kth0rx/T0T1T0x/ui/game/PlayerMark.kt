package com.v1kth0rx.T0T1T0x.ui.game

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.v1kth0rx.T0T1T0x.data.IconStyle
import com.v1kth0rx.T0T1T0x.domain.Player

@Composable
fun PlayerMark(
    player: Player?,
    style: IconStyle = IconStyle.CLASSIC,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary
) {
    if (player == null) return

    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        when (style) {
            IconStyle.CLASSIC -> ClassicMark(player, color)
            IconStyle.NUMBERS -> TextMark(if (player == Player.X) "1" else "0", color)
            IconStyle.SHAPES -> ShapesMark(player, color)
            IconStyle.SIGNS -> TextMark(if (player == Player.X) "+" else "−", color)
        }
    }
}

@Composable
private fun ClassicMark(player: Player, color: Color) {
    Canvas(modifier = Modifier.fillMaxSize(0.65f)) {
        val strokeWidth = size.width * 0.15f
        if (player == Player.X) {
            drawLine(color, Offset(0f, 0f), Offset(size.width, size.height), strokeWidth, StrokeCap.Round)
            drawLine(color, Offset(size.width, 0f), Offset(0f, size.height), strokeWidth, StrokeCap.Round)
        } else {
            drawCircle(color, radius = size.width / 2f, style = Stroke(width = strokeWidth))
        }
    }
}

@Composable
private fun ShapesMark(player: Player, color: Color) {
    Canvas(modifier = Modifier.fillMaxSize(0.65f)) {
        val strokeWidth = size.width * 0.15f
        if (player == Player.X) {
            // Cuadrado
            drawRect(
                color = color,
                topLeft = Offset.Zero,
                size = size,
                style = Stroke(width = strokeWidth, join = StrokeJoin.Round)
            )
        } else {
            // Triángulo
            val path = Path().apply {
                moveTo(size.width / 2f, 0f)
                lineTo(size.width, size.height)
                lineTo(0f, size.height)
                close()
            }
            drawPath(path, color, style = Stroke(width = strokeWidth, join = StrokeJoin.Round))
        }
    }
}

@Composable
private fun TextMark(text: String, color: Color) {
    Text(
        text = text,
        style = MaterialTheme.typography.displayMedium,
        color = color,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center
    )
}

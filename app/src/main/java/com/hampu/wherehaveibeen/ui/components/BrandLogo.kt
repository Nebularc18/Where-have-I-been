package com.hampu.wherehaveibeen.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.unit.dp

@Composable
fun BrandLogo(
    modifier: Modifier = Modifier
) {
    val outlineColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.08f)

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(22.dp),
        color = Color(0xFFF8FAF7),
        shadowElevation = 10.dp,
        tonalElevation = 2.dp
    ) {
        Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
            Canvas(
                modifier = Modifier
                    .width(184.dp)
                    .height(96.dp)
            ) {
                drawRoundRect(
                    color = Color.White,
                    cornerRadius = CornerRadius(20f, 20f)
                )

                val landBrush = Brush.linearGradient(
                    colors = listOf(Color(0xFF94DB39), Color(0xFF56A91F)),
                    start = Offset(0f, 0f),
                    end = Offset(size.width, size.height)
                )

                val shadowColor = Color(0x33000000)
                val greenland = Path().apply {
                    moveTo(size.width * 0.30f, size.height * 0.10f)
                    cubicTo(size.width * 0.34f, size.height * 0.02f, size.width * 0.42f, size.height * 0.02f, size.width * 0.45f, size.height * 0.10f)
                    cubicTo(size.width * 0.46f, size.height * 0.18f, size.width * 0.42f, size.height * 0.24f, size.width * 0.36f, size.height * 0.22f)
                    cubicTo(size.width * 0.31f, size.height * 0.20f, size.width * 0.27f, size.height * 0.17f, size.width * 0.30f, size.height * 0.10f)
                    close()
                }
                val americas = Path().apply {
                    moveTo(size.width * 0.02f, size.height * 0.34f)
                    cubicTo(size.width * 0.08f, size.height * 0.22f, size.width * 0.18f, size.height * 0.22f, size.width * 0.24f, size.height * 0.31f)
                    cubicTo(size.width * 0.27f, size.height * 0.37f, size.width * 0.30f, size.height * 0.45f, size.width * 0.26f, size.height * 0.50f)
                    cubicTo(size.width * 0.23f, size.height * 0.56f, size.width * 0.23f, size.height * 0.66f, size.width * 0.20f, size.height * 0.72f)
                    cubicTo(size.width * 0.18f, size.height * 0.78f, size.width * 0.19f, size.height * 0.90f, size.width * 0.14f, size.height * 0.95f)
                    cubicTo(size.width * 0.11f, size.height * 0.92f, size.width * 0.12f, size.height * 0.80f, size.width * 0.10f, size.height * 0.73f)
                    cubicTo(size.width * 0.08f, size.height * 0.66f, size.width * 0.05f, size.height * 0.60f, size.width * 0.05f, size.height * 0.52f)
                    cubicTo(size.width * 0.04f, size.height * 0.45f, size.width * 0.00f, size.height * 0.42f, size.width * 0.02f, size.height * 0.34f)
                    close()
                }
                val europeAsia = Path().apply {
                    moveTo(size.width * 0.55f, size.height * 0.29f)
                    cubicTo(size.width * 0.60f, size.height * 0.18f, size.width * 0.73f, size.height * 0.16f, size.width * 0.88f, size.height * 0.24f)
                    cubicTo(size.width * 0.97f, size.height * 0.28f, size.width * 1.00f, size.height * 0.38f, size.width * 0.95f, size.height * 0.44f)
                    cubicTo(size.width * 0.89f, size.height * 0.48f, size.width * 0.86f, size.height * 0.55f, size.width * 0.80f, size.height * 0.56f)
                    cubicTo(size.width * 0.76f, size.height * 0.54f, size.width * 0.72f, size.height * 0.58f, size.width * 0.67f, size.height * 0.52f)
                    cubicTo(size.width * 0.63f, size.height * 0.47f, size.width * 0.57f, size.height * 0.44f, size.width * 0.55f, size.height * 0.29f)
                    close()
                }
                val africa = Path().apply {
                    moveTo(size.width * 0.54f, size.height * 0.42f)
                    cubicTo(size.width * 0.60f, size.height * 0.40f, size.width * 0.66f, size.height * 0.47f, size.width * 0.66f, size.height * 0.57f)
                    cubicTo(size.width * 0.66f, size.height * 0.68f, size.width * 0.63f, size.height * 0.84f, size.width * 0.58f, size.height * 0.85f)
                    cubicTo(size.width * 0.53f, size.height * 0.84f, size.width * 0.50f, size.height * 0.70f, size.width * 0.50f, size.height * 0.58f)
                    cubicTo(size.width * 0.50f, size.height * 0.49f, size.width * 0.50f, size.height * 0.44f, size.width * 0.54f, size.height * 0.42f)
                    close()
                }
                val australia = Path().apply {
                    moveTo(size.width * 0.82f, size.height * 0.76f)
                    cubicTo(size.width * 0.86f, size.height * 0.70f, size.width * 0.94f, size.height * 0.72f, size.width * 0.95f, size.height * 0.80f)
                    cubicTo(size.width * 0.92f, size.height * 0.86f, size.width * 0.84f, size.height * 0.87f, size.width * 0.82f, size.height * 0.76f)
                    close()
                }
                val islands = listOf(
                    Rect(size.width * 0.47f, size.height * 0.20f, size.width * 0.50f, size.height * 0.25f),
                    Rect(size.width * 0.69f, size.height * 0.11f, size.width * 0.72f, size.height * 0.15f),
                    Rect(size.width * 0.90f, size.height * 0.18f, size.width * 0.93f, size.height * 0.22f)
                )

                listOf(greenland, americas, europeAsia, africa, australia).forEach { path ->
                    val shadowPath = Path().apply {
                        addPath(path, offset = Offset(4f, 5f))
                    }
                    drawPath(shadowPath, color = shadowColor, style = Fill)
                    drawPath(path, brush = landBrush, style = Fill)
                }

                islands.forEach { island ->
                    drawRoundRect(
                        color = shadowColor,
                        topLeft = island.topLeft + Offset(3f, 4f),
                        size = island.size,
                        cornerRadius = CornerRadius(8f, 8f)
                    )
                    drawRoundRect(
                        brush = landBrush,
                        topLeft = island.topLeft,
                        size = island.size,
                        cornerRadius = CornerRadius(8f, 8f)
                    )
                }

                drawRoundRect(
                    color = outlineColor,
                    cornerRadius = CornerRadius(20f, 20f),
                    style = Fill
                )
            }
        }
    }
}

package com.hampu.wherehaveibeen.ui.map

import android.graphics.Rect
import android.graphics.Region
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.graphics.asAndroidPath
import com.eltonkola.bota.Country
import com.eltonkola.bota.WorldMapPaths
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.min

private data class HitTestPath(
    val path: Path,
    val region: Region
)

private data class RenderableCountry(
    val id: String,
    val name: String,
    val renderablePaths: List<HitTestPath>
)

@Composable
fun InteractiveWorldMap(
    modifier: Modifier = Modifier,
    countryColors: Map<String, Color>,
    defaultColor: Color,
    strokeColor: Color,
    interactive: Boolean = true,
    onCountryClick: (Country) -> Unit
) {
    val currentOnCountryClick by rememberUpdatedState(onCountryClick)
    val renderableCountries = remember {
        val countries = WorldMapPaths.data.map { countryPath ->
            RenderableCountry(
                id = countryPath.id,
                name = countryPath.name,
                renderablePaths = countryPath.paths.map { pathData ->
                    createHitTestPath(PathParser().parsePathString(pathData).toPath())
                }
            )
        }
        if (countries.any { it.id == ANTARCTICA_ID }) {
            countries
        } else {
            countries + RenderableCountry(
                id = ANTARCTICA_ID,
                name = "Antarctica",
                renderablePaths = listOf(createHitTestPath(PathParser().parsePathString(ANTARCTICA_PATH_DATA).toPath()))
            )
        }
    }
    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    var canvasSize by remember { mutableStateOf(IntSize.Zero) }

    val svgWidth = 2000f
    val svgHeight = 857f

    LaunchedEffect(interactive) {
        if (!interactive) {
            scale = 1f
            offset = Offset.Zero
        }
    }

    Box(
        modifier = if (interactive) {
            modifier
                .clipToBounds()
                .onSizeChanged { canvasSize = it }
                .pointerInput(Unit) {
                    detectTransformGestures { centroid, pan, zoom, _ ->
                        val newScale = (scale * zoom).coerceIn(0.8f, 8f)
                        val effectivePan = if (abs(newScale - scale) < 0.001f && zoom > 1f) {
                            Offset.Zero
                        } else {
                            pan
                        }
                        val pivotX = centroid.x - canvasSize.width / 2f
                        val pivotY = centroid.y - canvasSize.height / 2f
                        val newOffset = Offset(
                            x = offset.x * zoom - pivotX * (zoom - 1f) + effectivePan.x,
                            y = offset.y * zoom - pivotY * (zoom - 1f) + effectivePan.y
                        )
                        val contentWidth = canvasSize.width * newScale
                        val contentHeight = canvasSize.height * newScale
                        val maxOffsetX = (contentWidth - canvasSize.width).coerceAtLeast(0f) / 2f
                        val maxOffsetY = (contentHeight - canvasSize.height).coerceAtLeast(0f) / 2f
                        offset = Offset(
                            x = newOffset.x.coerceIn(-maxOffsetX, maxOffsetX),
                            y = newOffset.y.coerceIn(-maxOffsetY, maxOffsetY)
                        )
                        scale = newScale
                    }
                }
        } else {
            modifier
        }
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    translationX = offset.x,
                    translationY = offset.y
                )
                .pointerInput(Unit) {
                    detectTapGestures { tapOffset ->
                        if (canvasSize == IntSize.Zero) return@detectTapGestures
                        val fitScale = min(canvasSize.width / svgWidth, canvasSize.height / svgHeight)
                        val scaledSvgWidth = svgWidth * fitScale
                        val scaledSvgHeight = svgHeight * fitScale
                        val paddingX = (canvasSize.width - scaledSvgWidth) / 2f
                        val paddingY = (canvasSize.height - scaledSvgHeight) / 2f
                        val untransformedTap = Offset(
                            x = (tapOffset.x - offset.x) / scale,
                            y = (tapOffset.y - offset.y) / scale
                        )
                        val svgPoint = Offset(
                            x = (untransformedTap.x - paddingX) / fitScale,
                            y = (untransformedTap.y - paddingY) / fitScale
                        )
                        val clickedCountry = renderableCountries.asReversed().firstOrNull { country ->
                            country.renderablePaths.any { hitTestPath ->
                                hitTestPath.region.contains(svgPoint.x.toInt(), svgPoint.y.toInt())
                            }
                        }
                        clickedCountry?.let { currentOnCountryClick(Country(id = it.id, name = it.name)) }
                    }
                }
        ) {
            val fitScale = min(size.width / svgWidth, size.height / svgHeight)
            val scaledSvgWidth = svgWidth * fitScale
            val scaledSvgHeight = svgHeight * fitScale
            val paddingX = (size.width - scaledSvgWidth) / 2f
            val paddingY = (size.height - scaledSvgHeight) / 2f

            withTransform({
                translate(left = paddingX, top = paddingY)
                scale(fitScale, fitScale, pivot = Offset.Zero)
            }) {
                renderableCountries.forEach { country ->
                    val fillColor = countryColors[country.id] ?: defaultColor
                    country.renderablePaths.forEach { hitTestPath ->
                        drawPath(path = hitTestPath.path, color = fillColor, style = Fill)
                        drawPath(path = hitTestPath.path, color = strokeColor, style = Stroke(width = 0.6f / fitScale))
                    }
                }
            }
        }
    }
}

private fun createHitTestPath(path: Path): HitTestPath {
    val bounds = path.getBounds()
    val clipBounds = Rect(
        floor(bounds.left).toInt(),
        floor(bounds.top).toInt(),
        ceil(bounds.right).toInt().coerceAtLeast(floor(bounds.left).toInt() + 1),
        ceil(bounds.bottom).toInt().coerceAtLeast(floor(bounds.top).toInt() + 1)
    )
    val region = Region().apply {
        setPath(path.asAndroidPath(), Region(clipBounds))
    }
    return HitTestPath(path = path, region = region)
}

private const val ANTARCTICA_ID = "AQ"

private const val ANTARCTICA_PATH_DATA =
    "M 520 752 L 575 732 L 648 739 L 726 720 L 812 731 L 906 710 L 996 723 " +
        "L 1092 711 L 1176 729 L 1268 718 L 1352 736 L 1420 728 L 1482 744 " +
        "L 1454 776 L 1368 793 L 1284 787 L 1184 804 L 1090 792 L 996 808 " +
        "L 902 791 L 818 803 L 734 786 L 656 794 L 582 781 L 532 766 Z"

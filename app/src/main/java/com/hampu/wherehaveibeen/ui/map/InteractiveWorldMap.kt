package com.hampu.wherehaveibeen.ui.map

import android.graphics.Rect
import android.graphics.Region
import android.graphics.RectF
import android.graphics.Region
import android.graphics.Matrix as AndroidMatrix
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculateCentroid
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateZoom
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
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.graphics.asAndroidPath
import com.eltonkola.bota.Country
import com.eltonkola.bota.WorldMapPaths
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

private data class HitTestPath(
    val path: Path,
    val region: Region
)

private data class RenderableCountry(
    val id: String,
    val name: String,
    val renderablePaths: List<HitTestPath>
    val renderablePaths: List<Path>,
    val hitRegions: List<Region>,
    val pathBounds: List<RectF>
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
            val renderablePaths = countryPath.paths.map { PathParser().parsePathString(it).toPath() }
            RenderableCountry(
                id = countryPath.id,
                name = countryPath.name,
                renderablePaths = countryPath.paths.map { pathData ->
                    createHitTestPath(PathParser().parsePathString(pathData).toPath())
                }
                renderablePaths = renderablePaths,
                hitRegions = renderablePaths.map(::createRegionForPath),
                pathBounds = renderablePaths.map(::computeBoundsForPath)
            )
        }
        if (countries.any { it.id == ANTARCTICA_ID }) {
            countries
        } else {
            val antarcticaPath = createAntarcticaPath()
            countries + RenderableCountry(
                id = ANTARCTICA_ID,
                name = "Antarctica",
                renderablePaths = listOf(createHitTestPath(PathParser().parsePathString(ANTARCTICA_PATH_DATA).toPath()))
                renderablePaths = listOf(antarcticaPath),
                hitRegions = listOf(createRegionForPath(antarcticaPath)),
                pathBounds = listOf(computeBoundsForPath(antarcticaPath))
            )
        }
    }
    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    var canvasSize by remember { mutableStateOf(IntSize.Zero) }

    val svgWidth = 2000f
    val svgHeight = 1040f

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
                .pointerInput(renderableCountries, interactive, canvasSize) {
                    awaitEachGesture {
                        val firstDown = awaitFirstDown(requireUnconsumed = false)
                        var gestureScale = scale
                        var gestureOffset = offset
                        var maxPointerCount = 1
                        var maxMoveDistance = 0f
                        var lastPrimaryChange: PointerInputChange = firstDown

                        do {
                            val event = awaitPointerEvent()
                            val pressedChanges = event.changes.filter { it.pressed }
                            if (pressedChanges.isEmpty()) break

                            maxPointerCount = max(maxPointerCount, pressedChanges.size)
                            val primaryChange = pressedChanges.firstOrNull { it.id == firstDown.id }
                                ?: pressedChanges.first()
                            lastPrimaryChange = primaryChange
                            maxMoveDistance = max(
                                maxMoveDistance,
                                (primaryChange.position - firstDown.position).getDistance()
                            )

                            if (pressedChanges.size > 1) {
                                val zoomChange = event.calculateZoom()
                                val panChange = event.calculatePan()
                                val centroid = event.calculateCentroid(useCurrent = true)
                                val newScale = (gestureScale * zoomChange).coerceIn(0.8f, 8f)
                                val effectivePan = if (abs(newScale - gestureScale) < 0.001f && zoomChange > 1f) {
                                    Offset.Zero
                                } else {
                                    panChange
                                }
                                gestureOffset = transformedOffset(
                                    currentOffset = gestureOffset,
                                    currentScale = gestureScale,
                                    newScale = newScale,
                                    centroid = centroid,
                                    pan = effectivePan,
                                    canvasSize = canvasSize
                                )
                                gestureScale = newScale
                                scale = gestureScale
                                offset = gestureOffset
                                pressedChanges.forEach { it.consume() }
                            } else if (gestureScale > 1f && maxMoveDistance > TapSlopPx) {
                                val panChange = primaryChange.positionChange()
                                if (panChange != Offset.Zero) {
                                    gestureOffset = clampOffset(
                                        newOffset = gestureOffset + panChange,
                                        scale = gestureScale,
                                        canvasSize = canvasSize
                                    )
                                    offset = gestureOffset
                                    primaryChange.consume()
                                }
                            }
                        } while (true)

                        if (maxPointerCount == 1 && maxMoveDistance <= TapSlopPx) {
                            val clickedCountry = findCountryForTap(
                                tapOffset = lastPrimaryChange.position,
                                canvasSize = canvasSize,
                                scale = scale,
                                offset = offset,
                                svgWidth = svgWidth,
                                svgHeight = svgHeight,
                                countries = renderableCountries
                            )
                            clickedCountry?.let { currentOnCountryClick(Country(id = it.id, name = it.name)) }
                        }
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
                    translationY = offset.y,
                    transformOrigin = TransformOrigin.Center
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
private fun findCountryForTap(
    tapOffset: Offset,
    canvasSize: IntSize,
    scale: Float,
    offset: Offset,
    svgWidth: Float,
    svgHeight: Float,
    countries: List<RenderableCountry>
): RenderableCountry? {
    if (canvasSize == IntSize.Zero) return null

    val fitScale = min(canvasSize.width / svgWidth, canvasSize.height / svgHeight)
    val scaledSvgWidth = svgWidth * fitScale
    val scaledSvgHeight = svgHeight * fitScale
    val paddingX = (canvasSize.width - scaledSvgWidth) / 2f
    val paddingY = (canvasSize.height - scaledSvgHeight) / 2f
    val layerCenter = Offset(
        x = canvasSize.width / 2f,
        y = canvasSize.height / 2f
    )
    val untransformedTap = Offset(
        x = layerCenter.x + ((tapOffset.x - offset.x) - layerCenter.x) / scale,
        y = layerCenter.y + ((tapOffset.y - offset.y) - layerCenter.y) / scale
    )
    val svgPoint = Offset(
        x = (untransformedTap.x - paddingX) / fitScale,
        y = (untransformedTap.y - paddingY) / fitScale
    )
    val fallbackRadius = 24f / (fitScale * scale).coerceAtLeast(0.01f)
    return findCountryAtPoint(
        countries = countries,
        point = svgPoint,
        fallbackRadius = fallbackRadius
    )
}

private fun transformedOffset(
    currentOffset: Offset,
    currentScale: Float,
    newScale: Float,
    centroid: Offset,
    pan: Offset,
    canvasSize: IntSize
): Offset {
    val pivotX = centroid.x - canvasSize.width / 2f
    val pivotY = centroid.y - canvasSize.height / 2f
    val newOffset = Offset(
        x = currentOffset.x * (newScale / currentScale) - pivotX * ((newScale / currentScale) - 1f) + pan.x,
        y = currentOffset.y * (newScale / currentScale) - pivotY * ((newScale / currentScale) - 1f) + pan.y
    )
    return clampOffset(newOffset, newScale, canvasSize)
}

private fun clampOffset(
    newOffset: Offset,
    scale: Float,
    canvasSize: IntSize
): Offset {
    val contentWidth = canvasSize.width * scale
    val contentHeight = canvasSize.height * scale
    val maxOffsetX = (contentWidth - canvasSize.width).coerceAtLeast(0f) / 2f
    val maxOffsetY = (contentHeight - canvasSize.height).coerceAtLeast(0f) / 2f
    return Offset(
        x = newOffset.x.coerceIn(-maxOffsetX, maxOffsetX),
        y = newOffset.y.coerceIn(-maxOffsetY, maxOffsetY)
    )
}

private fun findCountryAtPoint(
    countries: List<RenderableCountry>,
    point: Offset,
    fallbackRadius: Float
): RenderableCountry? {
    val exactMatch = countries.asReversed().firstOrNull { country ->
        country.hitRegions.any { region ->
            region.contains(point.x.roundToInt(), point.y.roundToInt())
        }
    }

    if (exactMatch != null) return exactMatch

    return countries.asReversed()
        .mapNotNull { country ->
            val distance = country.pathBounds.minOfOrNull { bounds ->
                distanceToRect(point, bounds)
            } ?: return@mapNotNull null
            if (distance <= fallbackRadius) {
                country to distance
            } else {
                null
            }
        }
        .minByOrNull { it.second }
        ?.first
}

private fun createRegionForPath(path: Path): Region {
    val bounds = computeBoundsForPath(path)
    val padding = 12
    val clip = Region(
        floor(bounds.left).toInt() - padding,
        floor(bounds.top).toInt() - padding,
        ceil(bounds.right).toInt() + padding,
        ceil(bounds.bottom).toInt() + padding
    )
    return Region().apply {
        setPath(path.asAndroidPath(), clip)
    }
}

private fun createAntarcticaPath(): Path {
    return PathParser().parsePathString(ANTARCTICA_PATH_DATA).toPath().also { path ->
        val bounds = computeBoundsForPath(path)
        val matrix = AndroidMatrix().apply {
            setScale(1f, 0.62f, bounds.centerX(), bounds.centerY())
            postTranslate(0f, 132f)
        }
        path.asAndroidPath().transform(matrix)
    }
}

@Suppress("DEPRECATION")
private fun computeBoundsForPath(path: Path): RectF {
    return RectF().also { rect ->
        path.asAndroidPath().computeBounds(rect, true)
    }
}

private fun distanceToRect(point: Offset, rect: RectF): Float {
    val dx = when {
        point.x < rect.left -> rect.left - point.x
        point.x > rect.right -> point.x - rect.right
        else -> 0f
    }
    val dy = when {
        point.y < rect.top -> rect.top - point.y
        point.y > rect.bottom -> point.y - rect.bottom
        else -> 0f
    }
    return kotlin.math.sqrt(dx * dx + dy * dy)
}

private const val ANTARCTICA_ID = "AQ"
private const val TapSlopPx = 12f

private const val ANTARCTICA_PATH_DATA =
    "M 712.6 819.2 L 743.0 818.6 L 755.1 820.1 L 762.2 818.0 L 778.4 814.5 L 799.8 813.8 " +
        "L 828.0 811.2 L 874.4 809.1 L 834.5 806.1 L 810.1 805.2 L 801.2 800.6 L 808.7 797.6 " +
        "L 829.8 794.3 L 850.8 791.0 L 877.4 788.8 L 894.6 788.0 L 897.0 785.8 L 900.2 784.0 " +
        "L 914.8 782.2 L 918.9 780.1 L 908.1 778.3 L 922.7 776.6 L 927.8 774.8 L 937.2 772.5 " +
        "L 939.3 770.7 L 940.9 768.8 L 949.2 768.4 L 954.7 769.3 L 956.4 765.7 L 966.0 768.3 " +
        "L 979.9 768.8 L 987.6 768.4 L 995.5 768.6 L 1008.2 767.0 L 1021.2 765.6 L 1029.7 764.9 " +
        "L 1036.9 764.6 L 1042.3 764.1 L 1048.2 763.8 L 1053.7 763.1 L 1066.7 765.2 L 1069.8 762.0 " +
        "L 1081.0 763.2 L 1092.1 762.4 L 1102.8 764.2 L 1106.3 764.1 L 1116.7 765.9 L 1124.7 764.1 " +
        "L 1129.4 765.7 L 1136.3 765.8 L 1148.9 766.4 L 1153.4 765.4 L 1163.6 763.6 L 1179.7 762.1 " +
        "L 1183.3 759.9 L 1190.6 755.5 L 1195.6 758.4 L 1202.8 760.0 L 1209.9 760.5 L 1215.5 762.7 " +
        "L 1221.0 759.9 L 1221.4 756.2 L 1236.2 753.9 L 1248.9 752.1 L 1257.3 750.9 L 1261.0 748.9 " +
        "L 1262.3 750.7 L 1269.1 752.2 L 1274.3 748.6 L 1274.0 747.7 L 1281.8 748.6 L 1280.6 747.0 " +
        "L 1283.9 743.9 L 1312.2 744.2 L 1313.9 746.0 L 1313.9 746.8 L 1311.9 749.4 L 1322.0 747.8 " +
        "L 1326.4 749.1 L 1331.5 749.4 L 1340.4 750.3 L 1371.6 751.2 L 1388.9 754.1 L 1385.0 756.3 " +
        "L 1382.1 759.0 L 1378.6 760.5 L 1376.2 763.4 L 1384.8 764.0 L 1376.9 769.0 L 1374.7 771.4 " +
        "L 1372.6 774.8 L 1372.3 777.4 L 1378.0 774.3 L 1388.1 772.5 L 1391.5 770.5 L 1395.6 768.5 " +
        "L 1398.9 766.0 L 1403.9 764.0 L 1406.2 761.7 L 1412.5 760.6 L 1418.9 761.4 L 1422.1 759.3 " +
        "L 1428.1 758.0 L 1435.8 755.7 L 1452.8 751.2 L 1462.5 750.3 L 1473.2 748.3 L 1487.9 746.7 " +
        "L 1501.5 746.5 L 1517.7 745.5 L 1527.3 745.0 L 1534.6 745.6 L 1547.5 745.4 L 1550.5 746.2 " +
        "L 1556.5 744.9 L 1563.6 743.1 L 1576.3 742.6 L 1599.3 745.7 L 1603.9 747.1 L 1614.2 746.0 " +
        "L 1616.3 743.0 L 1635.6 743.9 L 1642.8 746.2 L 1637.1 749.1 L 1647.2 748.1 L 1657.1 748.2 " +
        "L 1664.1 748.2 L 1669.4 748.6 L 1682.7 746.4 L 1691.4 746.2 L 1698.4 744.6 L 1703.2 744.8 " +
        "L 1705.4 746.6 L 1707.8 747.8 L 1717.9 747.7 L 1722.8 744.1 L 1736.7 743.6 L 1745.4 745.2 " +
        "L 1753.6 744.2 L 1759.1 744.9 L 1770.2 745.2 L 1778.1 746.3 L 1790.8 747.4 L 1802.8 747.6 " +
        "L 1800.5 749.5 L 1801.1 752.0 L 1811.5 750.3 L 1814.9 752.0 L 1817.2 753.9 L 1823.7 754.2 " +
        "L 1832.4 754.3 L 1841.1 756.5 L 1848.7 755.9 L 1853.8 754.0 L 1858.1 755.2 L 1864.8 757.6 " +
        "L 1869.4 758.0 L 1875.4 758.0 L 1883.0 758.8 L 1889.6 761.2 L 1894.6 763.1 L 1897.5 766.0 " +
        "L 1901.3 766.0 L 1900.4 763.5 L 1909.3 764.9 L 1915.3 764.3 L 1924.7 764.7 L 1930.1 765.4 " +
        "L 1932.7 767.2 L 1939.8 768.3 L 1945.9 769.6 L 1949.3 770.3 L 1945.7 771.3 L 1944.5 772.7 " +
        "L 1942.6 773.9 L 1942.9 775.5 L 1938.4 776.8 L 1934.4 776.8 L 1924.8 776.0 L 1929.0 778.1 " +
        "L 1925.9 778.5 L 1923.1 779.8 L 1917.5 778.5 L 1915.7 779.9 L 1914.8 780.9 L 1918.2 783.0 " +
        "L 1909.4 783.8 L 1905.9 784.2 L 1904.1 785.4 L 1892.1 787.4 L 1902.7 789.0 L 1902.7 790.9 " +
        "L 1904.6 792.2 L 1903.9 794.3 L 1908.6 796.7 L 1913.9 799.2 L 1917.9 800.4 L 1929.1 803.0 " +
        "L 1908.6 803.5 L 1902.6 803.9 L 1897.5 803.4 L 1894.8 804.9 L 1889.3 807.4 L 1896.7 809.1 " +
        "L 1892.4 811.9 L 1886.3 813.1 L 1893.8 815.2 L 1897.3 816.9 L 1903.4 818.5 L 1904.3 821.2 " +
        "L 1925.2 822.6 L 1937.6 824.6 L 1941.0 825.6 L 1952.7 826.0 L 1965.9 827.8 L 1997.8 829.9 " +
        "L 1930.7 857.0 L 1819.7 857.0 L 1708.7 857.0 L 1597.8 857.0 L 1486.8 857.0 L 1375.9 857.0 " +
        "L 1267.7 857.0 L 1156.7 857.0 L 1045.8 857.0 L 934.8 857.0 L 823.9 857.0 L 712.9 857.0 " +
        "L 604.7 857.0 L 493.8 857.0 L 382.8 857.0 L 271.8 857.0 L 160.9 857.0 L 49.9 857.0 " +
        "L 10.0 830.0 L 51.5 831.1 L 119.5 833.8 L 134.5 832.7 L 82.6 830.4 L 83.3 828.7 " +
        "L 68.5 827.2 L 33.9 822.8 L 83.7 825.0 L 95.5 825.4 L 124.8 824.1 L 146.2 817.7 " +
        "L 163.5 814.1 L 172.1 812.2 L 165.8 810.2 L 173.1 808.9 L 172.2 807.9 L 152.1 805.4 " +
        "L 142.8 801.5 L 136.5 800.7 L 118.7 799.1 L 125.6 796.3 L 137.3 795.7 L 152.9 796.6 " +
        "L 169.4 798.3 L 172.7 796.0 L 191.7 797.6 L 191.5 796.2 L 191.7 794.1 L 174.2 791.3 " +
        "L 186.3 790.6 L 205.4 788.2 L 218.6 789.4 L 234.8 785.9 L 242.5 784.2 L 255.8 785.1 " +
        "L 288.9 784.3 L 310.7 784.7 L 327.0 784.4 L 345.1 782.5 L 358.8 783.3 L 366.1 780.3 " +
        "L 368.8 783.6 L 372.7 785.0 L 380.7 783.0 L 386.4 782.8 L 381.7 785.7 L 403.4 787.1 " +
        "L 418.7 786.3 L 442.6 787.2 L 448.1 785.4 L 445.3 783.3 L 436.6 782.1 L 431.1 779.0 " +
        "L 449.0 778.8 L 426.0 776.7 L 431.0 775.8 L 459.2 776.8 L 479.7 777.1 L 496.9 777.4 " +
        "L 510.3 774.9 L 514.8 776.9 L 524.5 777.4 L 534.2 779.1 L 545.1 780.0 L 553.6 777.6 " +
        "L 561.9 778.5 L 572.0 780.2 L 587.3 779.2 L 602.5 777.8 L 624.8 774.9 L 628.9 771.1 " +
        "L 625.2 767.3 L 622.2 763.1 L 619.4 759.9 L 623.3 758.8 L 628.3 757.2 L 628.0 754.1 " +
        "L 626.9 752.4 L 630.4 749.9 L 625.2 747.7 L 628.3 748.3 L 630.7 747.9 L 634.4 746.1 " +
        "L 634.5 744.0 L 639.9 742.5 L 641.8 741.0 L 644.1 740.0 L 646.3 738.3 L 648.7 737.8 " +
        "L 652.6 736.6 L 654.6 736.7 L 657.5 736.2 L 659.2 735.2 L 661.8 733.4 L 667.6 732.2 " +
        "L 671.7 731.9 L 681.7 729.5 L 682.4 731.4 L 679.3 731.5 L 673.8 733.5 L 673.4 735.7 " +
        "L 668.7 736.0 L 663.9 736.4 L 657.4 739.1 L 656.1 740.7 L 656.6 743.3 L 663.5 742.4 " +
        "L 660.6 744.4 L 656.9 745.0 L 651.6 745.5 L 648.3 744.0 L 645.0 744.6 L 645.4 747.1 " +
        "L 639.3 747.0 L 638.6 749.1 L 636.1 749.7 L 635.4 752.5 L 638.6 753.4 L 637.9 755.2 " +
        "L 642.9 755.3 L 645.7 755.4 L 649.0 757.8 L 653.8 761.4 L 653.2 763.3 L 655.5 765.6 " +
        "L 661.3 767.7 L 657.4 769.8 L 660.6 769.9 L 660.3 771.9 L 662.8 773.1 L 661.9 774.5 " +
        "L 666.6 775.5 L 661.8 777.2 L 657.6 777.6 L 661.0 779.4 L 659.0 781.1 L 660.7 782.3 " +
        "L 656.0 784.0 L 649.2 783.9 L 643.3 786.7 L 621.9 791.3 L 610.6 793.6 L 588.4 793.6 " +
        "L 569.7 792.6 L 573.8 795.5 L 593.6 799.2 L 582.1 800.6 L 570.2 802.3 L 534.6 800.1 " +
        "L 536.2 801.5 L 547.5 805.5 L 577.0 806.6 L 576.4 809.8 L 580.7 812.7 L 586.2 813.4 " +
        "L 609.0 813.0 L 639.3 816.6 L 641.3 817.1 L 635.3 818.9 L 638.3 820.8 L 652.7 822.3 " +
        "L 659.9 823.4 L 664.2 825.9 L 693.6 821.2 L 712.6 819.2 Z"

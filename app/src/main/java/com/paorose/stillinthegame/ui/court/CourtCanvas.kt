package com.paorose.stillinthegame.ui.court

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import com.paorose.stillinthegame.data.Sport
import com.paorose.stillinthegame.ui.theme.Chalk
import com.paorose.stillinthegame.ui.theme.CourtOrange
import com.paorose.stillinthegame.ui.theme.ElectricBlue
import com.paorose.stillinthegame.ui.theme.MidnightLine
import com.paorose.stillinthegame.ui.theme.RallyYellow

/** How many pieces a field has before it's a full match. */
const val FIELD_PIECES = 8

private val Crowd = Color(0xFF59647F)

/**
 * The field of return. Each completed activity adds one piece.
 * [pieces] = pieces already built. [reveal] animates the newest one from 0 to 1.
 * Nothing is ever taken away.
 */
@Composable
fun CourtCanvas(sport: Sport, pieces: Int, reveal: Float, modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val built = pieces.coerceIn(0, FIELD_PIECES)
        // The empty field is always faintly there: the place you're going back to.
        drawPiece(sport, 1, MidnightLine, 1f, dashed = true)
        for (p in 1..built) {
            val alpha = if (p == built) reveal else 1f
            drawPiece(sport, p, Chalk, alpha, dashed = false)
        }
    }
}

private fun DrawScope.drawPiece(sport: Sport, piece: Int, line: Color, alpha: Float, dashed: Boolean) {
    if (alpha <= 0f) return
    val w = size.width
    val h = size.height
    // Field area with breathing room for lights and crowd around it.
    val left = w * 0.16f
    val right = w * 0.84f
    val top = h * 0.14f
    val bottom = h * 0.86f
    val fw = right - left
    val fh = bottom - top
    val cx = left + fw / 2
    val cy = top + fh / 2
    val stroke = Stroke(
        width = (w * 0.008f).coerceAtLeast(2f),
        cap = StrokeCap.Round,
        pathEffect = if (dashed) PathEffect.dashPathEffect(floatArrayOf(14f, 12f)) else null
    )
    val sw = stroke.width

    when (piece) {
        // 1 · the outline (or the track for running)
        1 -> if (sport == Sport.RUNNING) {
            drawRoundRect(line, Offset(left, top), Size(fw, fh), CornerRadius(fw / 2), alpha = alpha, style = stroke)
        } else {
            drawRect(line, Offset(left, top), Size(fw, fh), alpha = alpha, style = stroke)
        }
        // 2 · the middle line
        2 -> when (sport) {
            Sport.RUNNING -> drawRoundRect(line, Offset(left + fw * 0.12f, top + fw * 0.12f), Size(fw * 0.76f, fh - fw * 0.24f), CornerRadius(fw * 0.38f), alpha = alpha, style = stroke)
            else -> drawLine(line, Offset(left, cy), Offset(right, cy), sw, StrokeCap.Round, alpha = alpha)
        }
        // 3 · the sport's own markings
        3 -> when (sport) {
            Sport.VOLLEYBALL -> {
                drawLine(line, Offset(left, cy - fh / 6), Offset(right, cy - fh / 6), sw, alpha = alpha)
                drawLine(line, Offset(left, cy + fh / 6), Offset(right, cy + fh / 6), sw, alpha = alpha)
            }
            Sport.BASKETBALL -> {
                drawCircle(line, fw * 0.14f, Offset(cx, cy), alpha = alpha, style = stroke)
                drawRect(line, Offset(cx - fw * 0.14f, top), Size(fw * 0.28f, fh * 0.2f), alpha = alpha, style = stroke)
                drawRect(line, Offset(cx - fw * 0.14f, bottom - fh * 0.2f), Size(fw * 0.28f, fh * 0.2f), alpha = alpha, style = stroke)
            }
            Sport.FOOTBALL -> {
                drawCircle(line, fw * 0.16f, Offset(cx, cy), alpha = alpha, style = stroke)
                drawRect(line, Offset(cx - fw * 0.3f, top), Size(fw * 0.6f, fh * 0.16f), alpha = alpha, style = stroke)
                drawRect(line, Offset(cx - fw * 0.3f, bottom - fh * 0.16f), Size(fw * 0.6f, fh * 0.16f), alpha = alpha, style = stroke)
            }
            Sport.RUNNING -> {
                drawRoundRect(line, Offset(left + fw * 0.06f, top + fw * 0.06f), Size(fw * 0.88f, fh - fw * 0.12f), CornerRadius(fw * 0.44f), alpha = alpha, style = stroke)
            }
        }
        // 4 · the net, hoops, goals or finish line, in Electric Blue
        4 -> when (sport) {
            Sport.VOLLEYBALL -> {
                drawLine(ElectricBlue, Offset(left - fw * 0.06f, cy), Offset(right + fw * 0.06f, cy), sw * 2.2f, StrokeCap.Round, alpha = alpha)
                drawCircle(ElectricBlue, sw * 2.2f, Offset(left - fw * 0.06f, cy), alpha = alpha)
                drawCircle(ElectricBlue, sw * 2.2f, Offset(right + fw * 0.06f, cy), alpha = alpha)
            }
            Sport.BASKETBALL -> {
                drawCircle(ElectricBlue, fw * 0.04f, Offset(cx, top + fh * 0.05f), alpha = alpha, style = stroke)
                drawCircle(ElectricBlue, fw * 0.04f, Offset(cx, bottom - fh * 0.05f), alpha = alpha, style = stroke)
            }
            Sport.FOOTBALL -> {
                drawRect(ElectricBlue, Offset(cx - fw * 0.12f, top - fh * 0.03f), Size(fw * 0.24f, fh * 0.03f), alpha = alpha, style = stroke)
                drawRect(ElectricBlue, Offset(cx - fw * 0.12f, bottom), Size(fw * 0.24f, fh * 0.03f), alpha = alpha, style = stroke)
            }
            Sport.RUNNING -> drawLine(ElectricBlue, Offset(right - fw * 0.12f, cy), Offset(right, cy), sw * 2f, StrokeCap.Round, alpha = alpha)
        }
        // 5 · the ball (or a runner's spot), in Court Orange
        5 -> {
            val ball = if (sport == Sport.RUNNING) Offset(right - fw * 0.06f, cy + fh * 0.06f) else Offset(cx - fw * 0.18f, cy + fh * 0.12f)
            drawCircle(CourtOrange, fw * 0.045f, ball, alpha = alpha)
        }
        // 6 · teammates
        6 -> {
            val r = fw * 0.03f
            val spots = if (sport == Sport.RUNNING) {
                listOf(Offset(left + fw * 0.03f, cy), Offset(left + fw * 0.09f, cy - fh * 0.1f), Offset(right - fw * 0.09f, cy - fh * 0.12f))
            } else {
                listOf(
                    Offset(left + fw * 0.2f, cy + fh * 0.32f), Offset(cx, cy + fh * 0.36f), Offset(right - fw * 0.2f, cy + fh * 0.32f),
                    Offset(left + fw * 0.22f, cy + fh * 0.12f), Offset(right - fw * 0.22f, cy + fh * 0.12f),
                    Offset(left + fw * 0.2f, cy - fh * 0.3f), Offset(cx, cy - fh * 0.34f), Offset(right - fw * 0.2f, cy - fh * 0.3f)
                )
            }
            spots.forEach { drawCircle(Chalk, r, it, alpha = alpha) }
        }
        // 7 · the lights come on
        7 -> {
            listOf(Offset(w * 0.06f, h * 0.05f), Offset(w * 0.94f, h * 0.05f), Offset(w * 0.06f, h * 0.95f), Offset(w * 0.94f, h * 0.95f)).forEach { c ->
                drawCircle(
                    Brush.radialGradient(listOf(RallyYellow.copy(alpha = 0.55f), Color.Transparent), center = c, radius = w * 0.32f),
                    radius = w * 0.32f, center = c, alpha = alpha
                )
                drawCircle(RallyYellow, w * 0.02f, c, alpha = alpha)
            }
        }
        // 8 · the crowd: you're back in a full match
        8 -> {
            val dot = w * 0.012f
            var x = left
            while (x <= right) {
                drawCircle(Crowd, dot, Offset(x, top - h * 0.06f), alpha = alpha)
                drawCircle(Crowd, dot, Offset(x, bottom + h * 0.06f), alpha = alpha)
                x += w * 0.035f
            }
            var y = top
            while (y <= bottom) {
                drawCircle(Crowd, dot, Offset(left - w * 0.08f, y), alpha = alpha)
                drawCircle(Crowd, dot, Offset(right + w * 0.08f, y), alpha = alpha)
                y += w * 0.035f
            }
            // a few fans in team colors
            listOf(0.22f, 0.5f, 0.71f).forEach { t ->
                drawCircle(CourtOrange, dot * 1.2f, Offset(left + fw * t, top - h * 0.06f), alpha = alpha)
                drawCircle(RallyYellow, dot * 1.2f, Offset(left + fw * (1 - t), bottom + h * 0.06f), alpha = alpha)
            }
        }
    }
}

package com.startracker

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.tan

class StarOverlayView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val starPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        style = Paint.Style.FILL
    }

    private val constellationPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.CYAN
        style = Paint.Style.STROKE
        strokeWidth = 2f
        alpha = 150
    }

    private val starCatalog = StarCatalog()
    private var azimuth = 0f
    private var pitch = 0f
    private var roll = 0f

    fun updateOrientation(azimuth: Float, pitch: Float, roll: Float) {
        this.azimuth = azimuth
        this.pitch = pitch
        this.roll = roll
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val centerX = width / 2f
        val centerY = height / 2f
        val radius = minOf(width, height) / 2f - 50f

        val visibleStars = starCatalog.getVisibleStars(azimuth, pitch, roll)

        for (star in visibleStars) {
            val (x, y) = projectStar(star, centerX, centerY, radius)
            
            if (x >= 0 && x <= width && y >= 0 && y <= height) {
                val size = (star.magnitude * 2f).coerceIn(1f, 8f)
                starPaint.alpha = ((6f - star.magnitude) * 40f).toInt().coerceIn(50, 255)
                canvas.drawCircle(x, y, size, starPaint)
            }
        }

        drawConstellations(canvas, centerX, centerY, radius, visibleStars)
    }

    private fun projectStar(
        star: StarData,
        centerX: Float,
        centerY: Float,
        radius: Float
    ): Pair<Float, Float> {
        val raRad = Math.toRadians(star.rightAscension)
        val decRad = Math.toRadians(star.declination)

        val viewAzimuth = Math.toRadians(azimuth.toDouble())
        val viewPitch = Math.toRadians(pitch.toDouble())
        val viewRoll = Math.toRadians(roll.toDouble())

        val cosDec = cos(decRad)
        val x0 = cosDec * cos(raRad)
        val y0 = cosDec * sin(raRad)
        val z0 = sin(decRad)

        val cosPitch = cos(viewPitch)
        val sinPitch = sin(viewPitch)
        val cosAzimuth = cos(viewAzimuth)
        val sinAzimuth = sin(viewAzimuth)
        val cosRoll = cos(viewRoll)
        val sinRoll = sin(viewRoll)

        val x1 = x0 * cosAzimuth - y0 * sinAzimuth
        val y1 = x0 * sinAzimuth + y0 * cosAzimuth
        val z1 = z0

        val x2 = x1
        val y2 = y1 * cosPitch - z1 * sinPitch
        val z2 = y1 * sinPitch + z1 * cosPitch

        val x3 = x2 * cosRoll - y2 * sinRoll
        val y3 = x2 * sinRoll + y2 * cosRoll
        val z3 = z2

        if (z3 < 0) {
            return Pair(-1f, -1f)
        }

        val fov = 60.0
        val scale = radius / tan(Math.toRadians(fov / 2))

        val x = centerX + (x3 * scale).toFloat()
        val y = centerY - (y3 * scale).toFloat()

        return Pair(x, y)
    }

    private fun drawConstellations(
        canvas: Canvas,
        centerX: Float,
        centerY: Float,
        radius: Float,
        visibleStars: List<StarData>
    ) {
        val starMap = visibleStars.associateBy { it.id }
        
        for (constellation in starCatalog.constellations) {
            var lastPoint: Pair<Float, Float>? = null
            
            for (starId in constellation.starIds) {
                val star = starMap[starId] ?: continue
                val point = projectStar(star, centerX, centerY, radius)
                
                if (point.first >= 0 && point.first <= width && 
                    point.second >= 0 && point.second <= height) {
                    if (lastPoint != null && lastPoint.first >= 0) {
                        canvas.drawLine(
                            lastPoint.first,
                            lastPoint.second,
                            point.first,
                            point.second,
                            constellationPaint
                        )
                    }
                    lastPoint = point
                } else {
                    lastPoint = null
                }
            }
        }
    }
}

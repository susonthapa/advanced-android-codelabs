package np.com.susanthapa.paintapp

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import androidx.core.content.res.ResourcesCompat
import kotlin.math.abs

/**
 * Created by suson on 9/24/20
 */

private const val STROKE_WIDTH = 12f
private const val INSET = 40

class PaintView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    // cache the bitmap and canvas
    private lateinit var extraCanvas: Canvas
    private lateinit var extraBitmap: Bitmap

    // initialize colors and paint
    private val backgroundColor = ResourcesCompat.getColor(resources, R.color.colorBackground, null)
    private val drawColor = ResourcesCompat.getColor(resources, R.color.colorPaint, null)
    private val paint = Paint().apply {
        color = drawColor
        isAntiAlias = true
        isDither = true
        style = Paint.Style.STROKE
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
        strokeWidth = STROKE_WIDTH
    }

    private var path = Path()
    // store the last finger position
    private var currentX = 0f
    private var currentY = 0f
    // store the current touch position
    private var motionTouchEventX = 0f
    private var motionTouchEventY = 0f
    // touch tolerance
    private val touchTolerance = ViewConfiguration.get(context).scaledTouchSlop
    // frame
    private lateinit var frame: Rect

    override fun onTouchEvent(event: MotionEvent): Boolean {
        motionTouchEventX = event.x
        motionTouchEventY = event.y
        when (event.action) {
            MotionEvent.ACTION_DOWN -> touchStart()
            MotionEvent.ACTION_MOVE -> touchMove()
            MotionEvent.ACTION_UP -> touchUp()
        }
        return true
    }

    private fun touchStart() {
        path.reset()
        path.moveTo(motionTouchEventX, motionTouchEventY)
        currentX = motionTouchEventX
        currentY = motionTouchEventY
    }

    private fun touchMove() {
        val dx = abs(currentX - motionTouchEventX)
        val dy = abs(currentY - motionTouchEventY)
        if (dx >= touchTolerance || dy >= touchTolerance) {
            // render the path
            path.quadTo(currentX, currentY, motionTouchEventX, motionTouchEventY)
            currentX = motionTouchEventX
            currentY = motionTouchEventY
            // render the path in the cached bitmap
            extraCanvas.drawPath(path, paint)
        }
        invalidate()
    }

    private fun touchUp() {
        path.reset()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (::extraBitmap.isInitialized) {
            extraBitmap.recycle()
        }
        // create a frame
        frame = Rect(INSET, INSET, w- INSET, h- INSET)
        extraBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        // set the bitmap to canvas, so that canvas can draw into the bitmap instead of the screen
        extraCanvas = Canvas(extraBitmap)
        extraCanvas.drawColor(backgroundColor)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawBitmap(extraBitmap, 0f, 0f, null)
        canvas.drawRect(frame, paint)
    }

}
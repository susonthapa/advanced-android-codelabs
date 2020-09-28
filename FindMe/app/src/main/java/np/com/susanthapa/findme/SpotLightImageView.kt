package np.com.susanthapa.findme

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatImageView
import kotlin.math.floor
import kotlin.random.Random

/**
 * Created by suson on 9/25/20
 */

class SpotLightImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {

    private var paint = Paint()
    private var shouldDrawSpotLight = false
    private var gameOver = false

    private lateinit var winnerRect: RectF
    private var androidBitmapX = 0f
    private var androidBitmapY = 0f

    private val bitmapAndroid = BitmapFactory.decodeResource(resources, R.drawable.android)
    private val spotLight = BitmapFactory.decodeResource(resources, R.drawable.mask)

    private var shader: Shader
    private val shaderMatrix = Matrix()

    init {
        val bitmap = Bitmap.createBitmap(spotLight.width, spotLight.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val shaderPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        // draw black rectangle
        shaderPaint.color = Color.BLACK
        canvas.drawRect(0f, 0f, spotLight.width.toFloat(), spotLight.height.toFloat(), shaderPaint)
        // first the destination image is drawn then the source image is drawn, so the spotLight below
        // is the source and black rectangle above is the destination (refer to docs)
        shaderPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_OUT)
        canvas.drawBitmap(spotLight, 0f, 0f, shaderPaint)

        // create a shader
        shader = BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        paint.shader = shader
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val motionEventX = event.x
        val motionEventY = event.y
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                shouldDrawSpotLight = true
                if (gameOver) {
                    gameOver = false
                    setupWinnerRect()
                }
            }

            MotionEvent.ACTION_UP -> {
                shouldDrawSpotLight = false
                gameOver = winnerRect.contains(motionEventX, motionEventY)
            }
        }
        // translate the shader matrix slightly offset to user current touch position
        // so user can view that
        shaderMatrix.setTranslate(
            motionEventX - spotLight.width / 2f,
            motionEventY - spotLight.height / 2f
        )
        shader.setLocalMatrix(shaderMatrix)
        invalidate()
        return true
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        setupWinnerRect()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.drawColor(Color.WHITE)
        canvas.drawBitmap(bitmapAndroid, androidBitmapX, androidBitmapY, paint)
        if (!gameOver) {
            if (shouldDrawSpotLight) {
                canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
            } else {
                canvas.drawColor(Color.BLACK)
            }
        }
//        shaderMatrix.setTranslate(100f, 350f)
//        shader.setLocalMatrix(shaderMatrix)
        // one thing to note is that the shader location is not determined by it's drawing
        // which means the shader will drawn at the translated position irrespective of the
        // location of the rectangle below
//        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
    }

    private fun setupWinnerRect() {
        // make the bitmap within the bounds of the screen
        androidBitmapX = floor(Random.nextFloat() * (width - bitmapAndroid.width))
        androidBitmapY = floor(Random.nextFloat() * (height - bitmapAndroid.height))

        winnerRect = RectF(
            androidBitmapX,
            androidBitmapY,
            androidBitmapX + bitmapAndroid.width,
            androidBitmapY + bitmapAndroid.height
        )
    }
}
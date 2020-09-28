package np.com.susanthapa.paintapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val paint = PaintView(this)
        paint.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        setContentView(paint)
    }
}
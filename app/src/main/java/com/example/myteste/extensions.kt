package com.example.myteste

import android.content.Context
import android.content.Intent
import android.graphics.*
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.PaintDrawable
import android.graphics.drawable.shapes.OvalShape
import android.net.Uri
import android.os.Handler
import android.view.View
import android.widget.ImageView
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.core.view.size
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import androidx.viewpager.widget.ViewPager
import com.squareup.picasso.Picasso

fun getRoundedCornerImage(bitmap: Bitmap, pixels: Int): Bitmap? {
    val output =
        Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(output)
    val color = -0xbdbdbe
    val paint = Paint()
    val rect = Rect(0, 0, bitmap.width, bitmap.height)
    val rectF = RectF(rect)
    val roundPx = pixels.toFloat()
    paint.isAntiAlias = true
    canvas.drawARGB(0, 0, 0, 0)
    paint.color = color
    canvas.drawRoundRect(rectF, roundPx, roundPx, paint)
    paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
    canvas.drawBitmap(bitmap, rect, rect, paint)
    return output
}

fun ViewPager.autoScroll(viewLifecycleOwner: LifecycleOwner, interval: Long) {

    val mHandler = Handler()
    val runTask = RunTask(mHandler, this, interval)

    val lifecycleObserver = object : LifecycleObserver {
        @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        fun pause() {
            mHandler.removeCallbacksAndMessages(null)
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
        fun resume() {
            mHandler.postDelayed(runTask, interval * 2)
        }
    }

    viewLifecycleOwner.lifecycle.addObserver(lifecycleObserver)
}

class RunTask(private val viewHandler: Handler, private val viewPager: ViewPager, private val interval: Long) : Runnable {
    override fun run() {
        with(viewPager) {
            val priorityInterval = interval * 2
            val position = if (viewPager.currentItem + 1 < size) currentItem + 1 else 0
            val nextInterval = if (position == 0) priorityInterval else interval
            setCurrentItem(position, true)
            viewHandler.postDelayed(this@RunTask, nextInterval)
        }
    }

}

fun browseTo(url: String, context: Context) {
    var url = url
    if (!url.startsWith("http://") && !url.startsWith("https://")) {
        url = "http://$url"
    }
    val i = Intent(Intent.ACTION_VIEW)
    i.data = Uri.parse(url)
    startActivity( context, i,null)
}

enum class Corner {
    ALL, TOP, BOTTOM, LEFT, RIGHT, TOP_LEFT, TOP_RIGHT, BOTTOM_RIGHT, BOTTOM_LEFT
}

fun View.roundBackground(@ColorRes id: Int, radius: Int, corner: Corner = Corner.ALL) {
    background = PaintDrawable(ContextCompat.getColor(context, id)).apply {
        setCornerRadii(radii(radius.toFloat(), corner))
    }
}

fun View.roundBorder(
    @ColorRes id: Int,
    radius: Int,
    borderWidth: Int,
    corner: Corner = Corner.ALL
) {
    background = PaintDrawable(ContextCompat.getColor(context, id)).apply {
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = borderWidth.toFloat()
        setCornerRadii(radii(radius.toFloat(), corner))
    }
}

fun View.roundBorderBackground(
    @ColorRes backgroundColor: Int,
    @ColorRes borderColor: Int,
    radius: Int,
    borderWidth: Int,
    corner: Corner = Corner.ALL
) {
    val radii = radii(radius.toFloat(), corner)
    val back = PaintDrawable(ContextCompat.getColor(context, backgroundColor)).apply { setCornerRadii(radii) }
    val border = PaintDrawable(ContextCompat.getColor(context, borderColor)).apply {
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = borderWidth.toFloat()
        setCornerRadii(radii)
    }
    background = LayerDrawable(arrayOf(back, border))
}

fun ImageView.circleBackground(@ColorRes id: Int) {
    background = PaintDrawable(ContextCompat.getColor(context, id)).apply { shape = OvalShape() }
}

private fun radii(radius: Float, corner: Corner): FloatArray {
    return when (corner) {
        Corner.ALL -> FloatArray(8) { radius }
        Corner.TOP -> floatArrayOf(radius, radius, radius, radius, 0f, 0f, 0f, 0f)
        Corner.BOTTOM -> floatArrayOf(0f, 0f, 0f, 0f, radius, radius, radius, radius)
        Corner.LEFT -> floatArrayOf(radius, radius, 0f, 0f, 0f, 0f, radius, radius)
        Corner.RIGHT -> floatArrayOf(0f, 0f, radius, radius, radius, radius, 0f, 0f)
        Corner.TOP_LEFT -> floatArrayOf(radius, radius, 0f, 0f, 0f, 0f, 0f, 0f)
        Corner.TOP_RIGHT -> floatArrayOf(0f, 0f, radius, radius, 0f, 0f, 0f, 0f)
        Corner.BOTTOM_RIGHT -> floatArrayOf(0f, 0f, 0f, 0f, radius, radius, 0f, 0f)
        Corner.BOTTOM_LEFT -> floatArrayOf(0f, 0f, 0f, 0f, 0f, 0f, radius, radius)
    }
}

fun ImageView.load(resId: Int, centerCrop: Boolean = true, fit: Boolean = true) {
    Picasso.get()
        .load(resId)
        .also { if (centerCrop) it.centerCrop() }
        .also { if (fit) it.fit() }
        .into(this)
}
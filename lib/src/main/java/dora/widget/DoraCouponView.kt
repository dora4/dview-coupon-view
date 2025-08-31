package dora.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import dora.widget.couponview.R

class DoraCouponView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var title: String = ""
    private var content: String = ""
    private var bgColor: Int = Color.parseColor("#FF7043")
    private var titleColor: Int = Color.WHITE
    private var contentColor: Int = Color.WHITE

    private val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val titlePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val contentPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        style = Paint.Style.STROKE
        strokeWidth = 4f
        pathEffect = DashPathEffect(floatArrayOf(10f, 10f), 0f) // 虚线效果
    }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textSize = 48f
        textAlign = Paint.Align.CENTER
    }

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.DoraCouponView,
            0, 0
        ).apply {
            try {
                title = getString(R.styleable.DoraCouponView_dview_cv_couponTitle) ?: title
                content = getString(R.styleable.DoraCouponView_dview_cv_couponContent) ?: content
                bgColor = getColor(R.styleable.DoraCouponView_dview_cv_couponBgColor, bgColor)
                titleColor = getColor(R.styleable.DoraCouponView_dview_cv_couponTitleColor, titleColor)
                contentColor = getColor(R.styleable.DoraCouponView_dview_cv_couponContentColor, contentColor)
            } finally {
                recycle()
            }
        }
        bgPaint.color = bgColor
        titlePaint.color = titleColor
        titlePaint.textSize = 56f
        titlePaint.textAlign = Paint.Align.CENTER
        contentPaint.color = contentColor
        contentPaint.textSize = 36f
        contentPaint.textAlign = Paint.Align.CENTER
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val width = width.toFloat()
        val height = height.toFloat()
        val radius = 30f
        val rect = RectF(0f, 0f, width, height)
        canvas.drawRoundRect(rect, radius, radius, bgPaint)
        // 绘制左右两个凹槽（票口）
        val holeRadius = 20f
        canvas.drawCircle(0f, height / 2, holeRadius, Paint().apply {
            xfermode = PorterDuffXfermode(
            PorterDuff.Mode.CLEAR)
        })
        canvas.drawCircle(width, height / 2, holeRadius, Paint().apply {
            xfermode = PorterDuffXfermode(
            PorterDuff.Mode.CLEAR)
        })
        val path = Path().apply {
            moveTo(width * 0.35f, 0f)
            lineTo(width * 0.35f, height)
        }
        canvas.drawPath(path, linePaint)
        canvas.drawText(title, width * 0.18f, height / 2 + 20f, textPaint)
        textPaint.textSize = 36f
        canvas.drawText(content, width * 0.7f, height / 2f, textPaint)
    }
}

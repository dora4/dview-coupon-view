package dora.widget

import android.content.Context
import android.graphics.*
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

    private var dividerPercent: Float = 0.35f
    private var holeType: Int = 2 // 默认虚线
    private var cornerRadius: Float = 30f
    private var holeRadius: Float = 20f

    private val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val titlePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val contentPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val holePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
    }

    init {
        setLayerType(LAYER_TYPE_SOFTWARE, null) // 关闭硬件加速才能CLEAR挖洞
        context.theme.obtainStyledAttributes(attrs, R.styleable.DoraCouponView, 0, 0).apply {
            try {
                title = getString(R.styleable.DoraCouponView_dview_cv_couponTitle) ?: title
                content = getString(R.styleable.DoraCouponView_dview_cv_couponContent) ?: content
                bgColor = getColor(R.styleable.DoraCouponView_dview_cv_couponBgColor, bgColor)
                titleColor = getColor(R.styleable.DoraCouponView_dview_cv_couponTitleColor, titleColor)
                contentColor = getColor(R.styleable.DoraCouponView_dview_cv_couponContentColor, contentColor)
                dividerPercent = getFloat(R.styleable.DoraCouponView_dview_cv_dividerPercent, dividerPercent)
                holeType = getInt(R.styleable.DoraCouponView_dview_cv_holeType, holeType)
                cornerRadius = getDimension(R.styleable.DoraCouponView_dview_cv_cornerRadius, cornerRadius)
                holeRadius = getDimension(R.styleable.DoraCouponView_dview_cv_holeRadius, holeRadius)
            } finally {
                recycle()
            }
        }
        bgPaint.color = bgColor

        linePaint.color = Color.WHITE
        linePaint.style = Paint.Style.STROKE
        linePaint.strokeWidth = 4f
        linePaint.pathEffect = DashPathEffect(floatArrayOf(10f, 10f), 0f)

        titlePaint.color = titleColor
        titlePaint.textAlign = Paint.Align.CENTER
        titlePaint.textSize = 56f

        contentPaint.color = contentColor
        contentPaint.textAlign = Paint.Align.CENTER
        contentPaint.textSize = 36f
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val w = width.toFloat()
        val h = height.toFloat()

        // 背景
        canvas.drawRoundRect(RectF(0f, 0f, w, h), cornerRadius, cornerRadius, bgPaint)

        val lineX = w * dividerPercent

        // 画凹槽
        when (holeType) {
            1 -> { // 左右
                canvas.drawCircle(0f, h / 2, holeRadius, holePaint)
                canvas.drawCircle(w, h / 2, holeRadius, holePaint)
            }
            2 -> { // 虚线
                canvas.drawCircle(lineX, 0f, holeRadius, holePaint)
                canvas.drawCircle(lineX, h, holeRadius, holePaint)
            }
            3 -> { // 左右 + 虚线
                canvas.drawCircle(0f, h / 2, holeRadius, holePaint)
                canvas.drawCircle(w, h / 2, holeRadius, holePaint)
                canvas.drawCircle(lineX, 0f, holeRadius, holePaint)
                canvas.drawCircle(lineX, h, holeRadius, holePaint)
            }
        }

        // 分隔虚线
        val path = Path().apply {
            moveTo(lineX, 0f)
            lineTo(lineX, h)
        }
        canvas.drawPath(path, linePaint)

        // 绘制文字
        canvas.drawText(title, w * dividerPercent / 2, h / 2 + titlePaint.textSize / 2, titlePaint)
        canvas.drawText(content, (w + lineX) / 2, h / 2 + contentPaint.textSize / 2, contentPaint)
    }
}
package dora.widget

import android.content.Context
import android.graphics.*
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
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
    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        style = Paint.Style.STROKE
        strokeWidth = 4f
        pathEffect = DashPathEffect(floatArrayOf(10f, 10f), 0f)
    }

    private val titlePaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = 56f
        color = titleColor
    }
    private val contentPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = 36f
        color = contentColor
    }

    private var titleLayout: StaticLayout? = null
    private var contentLayout: StaticLayout? = null

    init {
        setLayerType(LAYER_TYPE_SOFTWARE, null) // 关闭硬件加速，支持CLEAR挖洞
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
        contentPaint.color = contentColor
    }

    @Suppress("DEPRECATION")
    private fun buildStaticLayout(
        text: String,
        paint: TextPaint,
        width: Int,
        align: Layout.Alignment
    ): StaticLayout {
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            StaticLayout.Builder.obtain(text, 0, text.length, paint, width)
                .setAlignment(align)
                .setLineSpacing(0f, 1f)
                .setIncludePad(false)
                .build()
        } else {
            // 旧版 API 21-22 用构造函数
            StaticLayout(
                text,
                paint,
                width,
                align,
                1f,   // spacingMult
                0f,   // spacingAdd
                false // includePad
            )
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (w > 0) {
            val titleWidth = (w * 0.3f).toInt()
            val contentWidth = (w * 0.55f).toInt()

            titleLayout = buildStaticLayout(title, titlePaint, titleWidth, Layout.Alignment.ALIGN_CENTER)
            contentLayout = buildStaticLayout(content, contentPaint, contentWidth, Layout.Alignment.ALIGN_CENTER)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val w = width.toFloat()
        val h = height.toFloat()
        val radius = 30f

        // 背景
        val rect = RectF(0f, 0f, w, h)
        canvas.drawRoundRect(rect, radius, radius, bgPaint)

        // 票口
        val holeRadius = 20f
        val holePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        }
        canvas.drawCircle(0f, h / 2, holeRadius, holePaint)
        canvas.drawCircle(w, h / 2, holeRadius, holePaint)

        // 分隔线
        val path = Path().apply {
            moveTo(w * 0.35f, 0f)
            lineTo(w * 0.35f, h)
        }
        canvas.drawPath(path, linePaint)

        // 绘制 title
        canvas.save()
        canvas.translate(w * 0.05f, h / 2 - (titleLayout?.height ?: 0) / 2f)
        titleLayout?.draw(canvas)
        canvas.restore()

        // 绘制 content
        canvas.save()
        canvas.translate(w * 0.4f, h / 2 - (contentLayout?.height ?: 0) / 2f)
        contentLayout?.draw(canvas)
        canvas.restore()
    }

    /** ========== 动态设置属性的方法 ========== */
    fun setCouponTitle(text: String) {
        this.title = text
        requestLayout()
        invalidate()
    }

    fun setCouponContent(text: String) {
        this.content = text
        requestLayout()
        invalidate()
    }

    fun setCouponBgColor(color: Int) {
        this.bgColor = color
        bgPaint.color = color
        invalidate()
    }

    fun setCouponTitleColor(color: Int) {
        this.titleColor = color
        titlePaint.color = color
        invalidate()
    }

    fun setCouponContentColor(color: Int) {
        this.contentColor = color
        contentPaint.color = color
        invalidate()
    }
}
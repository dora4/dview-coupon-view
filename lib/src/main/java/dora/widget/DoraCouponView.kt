package dora.widget

import android.content.Context
import android.graphics.*
import android.os.Build
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

    private var holeType: Int = 0 // 0=none,1=horizontal,2=vertical,3=both
    private var textOrientation: Int = 1 // 0=horizontal,1=vertical
    private var holeRadius: Float = 20f
    private var dividerGap: Float = 20f

    private val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val holePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
    }
    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        style = Paint.Style.STROKE
        strokeWidth = 2f
        pathEffect = DashPathEffect(floatArrayOf(dividerGap, dividerGap), 0f)
    }

    private val titlePaint = TextPaint(Paint.ANTI_ALIAS_FLAG)
    private val contentPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)

    private var titleLayout: StaticLayout? = null
    private var contentLayout: StaticLayout? = null

    init {
        setLayerType(LAYER_TYPE_SOFTWARE, null)

        context.theme.obtainStyledAttributes(attrs, R.styleable.DoraCouponView, 0, 0).apply {
            try {
                title = getString(R.styleable.DoraCouponView_dview_cv_couponTitle) ?: title
                content = getString(R.styleable.DoraCouponView_dview_cv_couponContent) ?: content
                bgColor = getColor(R.styleable.DoraCouponView_dview_cv_couponBgColor, bgColor)
                titleColor =
                    getColor(R.styleable.DoraCouponView_dview_cv_couponTitleColor, titleColor)
                contentColor =
                    getColor(R.styleable.DoraCouponView_dview_cv_couponContentColor, contentColor)
                holeType = getInt(R.styleable.DoraCouponView_dview_cv_holeType, 0)
                textOrientation = getInt(R.styleable.DoraCouponView_dview_cv_textOrientation, 1)
                holeRadius =
                    getDimension(R.styleable.DoraCouponView_dview_cv_holeRadius, holeRadius)
                dividerGap =
                    getDimension(R.styleable.DoraCouponView_dview_cv_dividerGap, dividerGap)
            } finally {
                recycle()
            }
        }

        bgPaint.color = bgColor
        titlePaint.apply { color = titleColor; textSize = 56f }
        contentPaint.apply { color = contentColor; textSize = 36f }
        linePaint.pathEffect = DashPathEffect(floatArrayOf(dividerGap, dividerGap), 0f)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (w <= 0) return
        val titleWidth = if (textOrientation == 1) w else (w / 3f).toInt()
        val contentWidth = if (textOrientation == 1) w else (w * 2 / 3f).toInt()

        titleLayout = buildStaticLayout(title, titlePaint, titleWidth)
        contentLayout = buildStaticLayout(content, contentPaint, contentWidth)
    }

    private fun buildStaticLayout(text: String, paint: TextPaint, width: Int): StaticLayout {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StaticLayout.Builder.obtain(text, 0, text.length, paint, width)
                .setAlignment(Layout.Alignment.ALIGN_CENTER)
                .setLineSpacing(0f, 1f)
                .setIncludePad(false)
                .build()
        } else {
            @Suppress("DEPRECATION")
            StaticLayout(text, paint, width, Layout.Alignment.ALIGN_CENTER, 1f, 0f, false)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val w = width.toFloat()
        val h = height.toFloat()
        val radius = 30f

        // 背景
        canvas.drawRoundRect(RectF(0f, 0f, w, h), radius, radius, bgPaint)

        // 绘制凹槽
        if (holeType == 1 || holeType == 3) drawHorizontalHoles(canvas, w, h)
        if (holeType == 2 || holeType == 3) drawVerticalHoles(canvas, w, h)

        // 绘制文字
        if (textOrientation == 1) { // vertical
            canvas.save()
            canvas.translate(
                w / 2 - (titleLayout?.width ?: 0) / 2f,
                h / 6f - (titleLayout?.height ?: 0) / 2
            )
            titleLayout?.draw(canvas)
            canvas.restore()

            canvas.save()
            canvas.translate(
                w / 2 - (contentLayout?.width ?: 0) / 2f,
                h * 2 / 3f - (contentLayout?.height ?: 0)
            )
            contentLayout?.draw(canvas)
            canvas.restore()
        } else { // horizontal
            canvas.save()
            canvas.translate(
                w / 6f - (titleLayout?.width ?: 0) / 2,
                h / 2 - (titleLayout?.height ?: 0) / 2f
            )
            titleLayout?.draw(canvas)
            canvas.restore()

            canvas.save()
            canvas.translate(
                w * 2 / 3f - (contentLayout?.width ?: 0),
                h / 2 - (contentLayout?.height ?: 0) / 2f
            )
            contentLayout?.draw(canvas)
            canvas.restore()
        }
    }

    private fun drawHorizontalHoles(canvas: Canvas, w: Float, h: Float) {
        // 左右半圆
        canvas.drawCircle(0f, h / 3, holeRadius, holePaint)
        canvas.drawCircle(w, h / 3, holeRadius, holePaint)

        // 虚线连接左半圆和右半圆
        val path = Path()
        path.moveTo(holeRadius, h / 3)
        path.lineTo(w - holeRadius, h / 3)
        canvas.drawPath(path, linePaint)
    }

    private fun drawVerticalHoles(canvas: Canvas, w: Float, h: Float) {
        // 上下半圆
        canvas.drawCircle(w / 3, 0f, holeRadius, holePaint)
        canvas.drawCircle(w / 3, h, holeRadius, holePaint)

        // 虚线连接上半圆和下半圆
        val path = Path()
        path.moveTo(w / 3, holeRadius)
        path.lineTo(w / 3, h - holeRadius)
        canvas.drawPath(path, linePaint)
    }

    /** ===== 动态设置属性 ===== */
    fun setHoleType(type: Int) {
        holeType = type; invalidate()
    }

    fun setTextOrientation(orientation: Int) {
        textOrientation = orientation; requestLayout(); invalidate()
    }

    fun setHoleRadius(radius: Float) {
        holeRadius = radius; invalidate()
    }

    fun setDividerGap(gap: Float) {
        dividerGap = gap; linePaint.pathEffect =
            DashPathEffect(floatArrayOf(dividerGap, dividerGap), 0f); invalidate()
    }

    fun setCouponTitle(text: String) {
        title = text; requestLayout(); invalidate()
    }

    fun setCouponContent(text: String) {
        content = text; requestLayout(); invalidate()
    }

    fun setCouponBgColor(color: Int) {
        bgColor = color; bgPaint.color = color; invalidate()
    }

    fun setCouponTitleColor(color: Int) {
        titleColor = color; titlePaint.color = color; invalidate()
    }

    fun setCouponContentColor(color: Int) {
        contentColor = color; contentPaint.color = color; invalidate()
    }
}

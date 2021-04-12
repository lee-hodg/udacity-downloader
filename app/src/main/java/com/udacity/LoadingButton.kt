package com.udacity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.core.content.withStyledAttributes

import timber.log.Timber
import kotlin.properties.Delegates


class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var progress = 0F  // must be float for drawRect
    private var angle = 0F  // must be float for drawArc
    private var widthSize = 0
    private var heightSize = 0

    private var buttonText: String = resources.getString(R.string.button_name)

    private var loadingColor = 0
    private var circleColor = 0
    private var customBackgroundColor = 0

    private val paintText = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = resources.getDimension(R.dimen.default_text_size)
        textAlign = Paint.Align.CENTER
        color = Color.WHITE
    }

    private val paintShapes = Paint().apply {
        isAntiAlias = true
        color = Color.RED
    }


    private var buttonAnimator = ValueAnimator()
    private var circleAnimator = ValueAnimator()

    // this will be a listener for when button state changes from one thing to the other
    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { property, oldValue, newValue ->
        when(newValue) {
            ButtonState.Loading -> {
                //start the loading animation
                buttonText =  resources.getString(R.string.button_loading)
                animateButton()
                animateCircle()
            }
            ButtonState.Completed -> {
                // handle complete
                buttonText = resources.getString(R.string.button_name)
                stopAnimations()

            }
            ButtonState.Clicked -> {
                Timber.d("Placeholder")
            }
        }
    }


    init {
        isClickable = true

        context.withStyledAttributes(attrs, R.styleable.LoadingButton) {
            circleColor = getColor(R.styleable.LoadingButton_circleColor, 0)
            loadingColor = getColor(R.styleable.LoadingButton_loadingColor, 0)
            customBackgroundColor = getColor(R.styleable.LoadingButton_backgroundColor, 0)
        }
    }

    private fun drawButtonBar(canvas: Canvas?) {
        paintShapes.color = loadingColor
        canvas?.drawRect(
            0f,
            0f,
            progress,
            heightSize.toFloat(), paintShapes
        )
    }

    private fun animateButton() {
        // animate from 0 up to the width of the button
        buttonAnimator = ValueAnimator.ofFloat(0F, widthSize.toFloat()).apply {
            duration = 1000
            addUpdateListener { valueAnimator ->
                // update the progress variable
                progress = valueAnimator.animatedValue as Float
                valueAnimator.repeatCount = ValueAnimator.INFINITE
                valueAnimator.repeatMode = ValueAnimator.REVERSE
                valueAnimator.interpolator = LinearInterpolator()

                // cause a redraw
                invalidate()
            }
            disableViewDuringAnimation(this@LoadingButton)
            start()
        }
    }

    private fun animateCircle() {
        // animate from 0 - 360 degrees
        circleAnimator = ValueAnimator.ofFloat(0F, 360F).apply {
            duration = 1000
            addUpdateListener { valueAnimator ->
                // update the angle variable
                angle = valueAnimator.animatedValue as Float
                valueAnimator.repeatCount = ValueAnimator.INFINITE
                valueAnimator.repeatMode = ValueAnimator.REVERSE
                valueAnimator.interpolator = LinearInterpolator()

                // cause a redraw
                invalidate()
            }
            // prevent clicking view during animation

            disableViewDuringAnimation(this@LoadingButton)
            start()
        }
    }

    private fun stopAnimations() {
        buttonAnimator.end()
        progress = 0F
        circleAnimator.end()
        angle = 0F
        invalidate()
    }

    private fun drawButtonText(canvas: Canvas?){
        // see https://blog.danlew.net/2013/10/03/centering_single_line_text_in_a_canvas/
        // these measure height above and below baseline of text, so together add up to
        // text's height
        val textHeight: Float = paintText.descent() + paintText.ascent()

        canvas?.drawText(
            buttonText,
            widthSize.toFloat() / 2,
            heightSize.toFloat() / 2 - textHeight / 2,
            paintText
        )
    }

    private fun drawCircle(canvas: Canvas?){
        paintShapes.color = circleColor

        val circleRadius = heightSize.toFloat()/2

        // bounding oval for the arc
        val oval = RectF(0f,
            0f,
            circleRadius*2f,
            circleRadius*2f)

        canvas?.drawArc(oval, 0F, angle, true, paintShapes)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas?.drawColor(customBackgroundColor)

        if (buttonState == ButtonState.Loading) {
            drawButtonBar(canvas)
            drawCircle(canvas)
        }

        // add the text
        drawButtonText(canvas)

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

    fun setState(state: ButtonState) {
        buttonState = state
    }

    override fun performClick(): Boolean {
        if (super.performClick()) return true
        invalidate()
        return true
    }

    private fun ValueAnimator.disableViewDuringAnimation(view: View) {
        addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator?) {
                view.isEnabled = false
            }

            override fun onAnimationEnd(animation: Animator?) {
                view.isEnabled = true
            }
        })
    }

}
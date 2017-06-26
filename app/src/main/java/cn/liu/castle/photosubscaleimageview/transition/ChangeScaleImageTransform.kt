package cn.liu.castle.photosubscaleimageview.transition

import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.TypeEvaluator
import android.content.Context
import android.graphics.Matrix
import android.graphics.Rect
import android.graphics.RectF
import android.os.Bundle
import android.transition.Transition
import android.transition.TransitionValues
import android.util.AttributeSet
import android.util.Property
import android.view.View
import android.view.ViewGroup
import cn.liu.castle.photosubscaleimageview.R
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView


/**
 * 继承默认的ImageTransform，重写捕获方法即可
 */
class ChangeScaleImageTransform : Transition {


    private val sTransitionProperties = arrayOf(PROPNAME_MATRIX, PROPNAME_BOUNDS)
    private val NULL_MATRIX_EVALUATOR = TypeEvaluator<Matrix> { fraction, startValue, endValue -> null }
    private val ANIMATED_TRANSFORM_PROPERTY = object : Property<SubsamplingScaleImageView, Matrix>(Matrix::class.java, "animateTransform") {
        override fun set(`object`: SubsamplingScaleImageView, value: Matrix) {
            `object`.animateTransform(value)
        }

        override fun get(`object`: SubsamplingScaleImageView): Matrix? {
            return null
        }
    }

    companion object {
        private val PROPNAME_MATRIX = "android:changeScaleImageTransform:matrix"
        private val PROPNAME_BOUNDS = "android:changeScaleImageTransform:bounds"
        fun addExtraProperties(view: SubsamplingScaleImageView, extra: Bundle) {
            val values = FloatArray(9)
            view.scaleImageMatrix.getValues(values)
            extra.putFloatArray(PROPNAME_MATRIX, values)
            val bound = view.getDrawingBound(Rect())
            extra.putParcelable(PROPNAME_BOUNDS, bound)
        }
    }

    constructor() : super()

    constructor(ctx: Context, attributeSet: AttributeSet) : super(ctx, attributeSet)

    override fun captureStartValues(transitionValues: TransitionValues?) {
        captureValue(transitionValues)
    }

    private fun captureValue(transitionValues: TransitionValues?) {

        //如果值为null,直接返回
        transitionValues ?: return
        val view = transitionValues.view
        //如果不可见或者类型不为ScaleImageView，那么直接返回即可
        if (view !is SubsamplingScaleImageView || view.visibility != View.VISIBLE) {
            return
        }
        //如果已经塞入了Bundle值，那么直接使用即可
        val extra = view.getTag(R.id.tag_transition_extra_properties) as Bundle?
        extra?.let {
            val floatArray = extra.getFloatArray(PROPNAME_MATRIX)
            val matrix = Matrix().apply { setValues(floatArray) }
            transitionValues.values.put(PROPNAME_BOUNDS, extra.getParcelable(PROPNAME_BOUNDS))
            transitionValues.values.put(PROPNAME_MATRIX, matrix)
            return
        }
        //图片没有准备好，返回
//        if (!view.isImageLoaded) return
        transitionValues.values.put(PROPNAME_BOUNDS, view.getDrawingBound(Rect()))
        transitionValues.values.put(PROPNAME_MATRIX, view.scaleImageMatrix ?: IDENTITY_MATRIX)
    }

    override fun captureEndValues(transitionValues: TransitionValues?) {
        captureValue(transitionValues)
    }


    override fun createAnimator(sceneRoot: ViewGroup?, startValues: TransitionValues?, endValues: TransitionValues?): Animator? {
        if (startValues == null || endValues == null) {
            return null
        }
        startValues.values[PROPNAME_BOUNDS] ?: return null
        endValues.values[PROPNAME_BOUNDS] ?: return null
        val startBounds = startValues.values[PROPNAME_BOUNDS] as Rect
        val endBounds = endValues.values[PROPNAME_BOUNDS] as Rect
        //如果开始的图片比结束的图片大，则不会开启动画
//        if (startBounds.width() <  endBounds.width() || startBounds.height() < endBounds.height()) {
//            return null
//        }

        var startMatrix: Matrix? = startValues.values[PROPNAME_MATRIX] as Matrix
        var endMatrix: Matrix? = endValues.values[PROPNAME_MATRIX] as Matrix

        val matricesEqual = startMatrix == null && endMatrix == null || startMatrix != null && startMatrix == endMatrix

        if (startBounds == endBounds && matricesEqual) {
            return null
        }

        val imageView = endValues.view as SubsamplingScaleImageView
        val drawableWidth = imageView.sWidth
        val drawableHeight = imageView.sHeight

        val animator: ObjectAnimator
        if (drawableWidth == 0 || drawableHeight == 0) {
            animator = createNullAnimator(imageView)
        } else {
            if (startMatrix == null) {
                startMatrix = IDENTITY_MATRIX
            }
            if (endMatrix == null) {
                endMatrix = IDENTITY_MATRIX
            }
            ANIMATED_TRANSFORM_PROPERTY.set(imageView, startMatrix)
            animator = createMatrixAnimator(imageView, startMatrix, endMatrix)
        }
        return animator
    }


    private fun createNullAnimator(imageView: SubsamplingScaleImageView): ObjectAnimator {
        return ObjectAnimator.ofObject<SubsamplingScaleImageView, Matrix>(imageView, ANIMATED_TRANSFORM_PROPERTY,
                NULL_MATRIX_EVALUATOR, null, null)
    }

    private fun createMatrixAnimator(imageView: SubsamplingScaleImageView, startMatrix: Matrix,
                                     endMatrix: Matrix): ObjectAnimator {
        return ObjectAnimator.ofObject(imageView, ANIMATED_TRANSFORM_PROPERTY,
                MatrixEvaluator(), startMatrix, endMatrix)
    }

    class MatrixEvaluator : TypeEvaluator<Matrix> {

        internal var mTempStartValues = FloatArray(9)

        internal var mTempEndValues = FloatArray(9)

        internal var mTempMatrix = Matrix()

        override fun evaluate(fraction: Float, startValue: Matrix, endValue: Matrix): Matrix {
            startValue.getValues(mTempStartValues)
            endValue.getValues(mTempEndValues)
            for (i in 0..8) {
                val diff = mTempEndValues[i] - mTempStartValues[i]
                mTempEndValues[i] = mTempStartValues[i] + fraction * diff
            }
            mTempMatrix.setValues(mTempEndValues)
            return mTempMatrix
        }
    }

    /** @hide
     */
    val IDENTITY_MATRIX: Matrix = object : Matrix() {
        internal fun oops() {
            throw IllegalStateException("Matrix can not be modified")
        }

        override fun set(src: Matrix) {
            oops()
        }

        override fun reset() {
            oops()
        }

        override fun setTranslate(dx: Float, dy: Float) {
            oops()
        }

        override fun setScale(sx: Float, sy: Float, px: Float, py: Float) {
            oops()
        }

        override fun setScale(sx: Float, sy: Float) {
            oops()
        }

        override fun setRotate(degrees: Float, px: Float, py: Float) {
            oops()
        }

        override fun setRotate(degrees: Float) {
            oops()
        }

        override fun setSinCos(sinValue: Float, cosValue: Float, px: Float, py: Float) {
            oops()
        }

        override fun setSinCos(sinValue: Float, cosValue: Float) {
            oops()
        }

        override fun setSkew(kx: Float, ky: Float, px: Float, py: Float) {
            oops()
        }

        override fun setSkew(kx: Float, ky: Float) {
            oops()
        }

        override fun setConcat(a: Matrix, b: Matrix): Boolean {
            oops()
            return false
        }

        override fun preTranslate(dx: Float, dy: Float): Boolean {
            oops()
            return false
        }

        override fun preScale(sx: Float, sy: Float, px: Float, py: Float): Boolean {
            oops()
            return false
        }

        override fun preScale(sx: Float, sy: Float): Boolean {
            oops()
            return false
        }

        override fun preRotate(degrees: Float, px: Float, py: Float): Boolean {
            oops()
            return false
        }

        override fun preRotate(degrees: Float): Boolean {
            oops()
            return false
        }

        override fun preSkew(kx: Float, ky: Float, px: Float, py: Float): Boolean {
            oops()
            return false
        }

        override fun preSkew(kx: Float, ky: Float): Boolean {
            oops()
            return false
        }

        override fun preConcat(other: Matrix): Boolean {
            oops()
            return false
        }

        override fun postTranslate(dx: Float, dy: Float): Boolean {
            oops()
            return false
        }

        override fun postScale(sx: Float, sy: Float, px: Float, py: Float): Boolean {
            oops()
            return false
        }

        override fun postScale(sx: Float, sy: Float): Boolean {
            oops()
            return false
        }

        override fun postRotate(degrees: Float, px: Float, py: Float): Boolean {
            oops()
            return false
        }

        override fun postRotate(degrees: Float): Boolean {
            oops()
            return false
        }

        override fun postSkew(kx: Float, ky: Float, px: Float, py: Float): Boolean {
            oops()
            return false
        }

        override fun postSkew(kx: Float, ky: Float): Boolean {
            oops()
            return false
        }

        override fun postConcat(other: Matrix): Boolean {
            oops()
            return false
        }

        override fun setRectToRect(src: RectF, dst: RectF, stf: ScaleToFit): Boolean {
            oops()
            return false
        }

        override fun setPolyToPoly(src: FloatArray, srcIndex: Int, dst: FloatArray, dstIndex: Int,
                                   pointCount: Int): Boolean {
            oops()
            return false
        }

        override fun setValues(values: FloatArray) {
            oops()
        }
    }


}

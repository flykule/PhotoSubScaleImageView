package cn.liu.castle.photosubscaleimageview.transition

import android.animation.Animator
import android.content.Context
import android.graphics.Rect
import android.transition.ChangeImageTransform
import android.transition.TransitionValues
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView

class ChangeScaleImageTransform : ChangeImageTransform {

    private val PROPNAME_MATRIX = "android:changeImageTransform:matrix"
    private val PROPNAME_BOUNDS = "android:changeImageTransform:bounds"
    private val sTransitionProperties = arrayOf(PROPNAME_MATRIX, PROPNAME_BOUNDS)

    constructor() : super()

    constructor(ctx: Context, attributeSet: AttributeSet) : super(ctx, attributeSet)

    override fun captureStartValues(transitionValues: TransitionValues?) {
        captureValue(transitionValues)
    }

    private fun captureValue(transitionValues: TransitionValues?) {
        //如果值为null,直接返回
        transitionValues ?: return
        //如果不可见或者不为目标ScaleImageView，那么直接返回即可
        val view = transitionValues.view
        if (view !is SubsamplingScaleImageView && view.visibility == View.VISIBLE) {
            return
        }
        val scaleImageView: SubsamplingScaleImageView = view as SubsamplingScaleImageView
        //图片没有准备好，返回
        if (!scaleImageView.isReady) return
        transitionValues.values.put(PROPNAME_BOUNDS, scaleImageView.getDrawingBound(Rect()))
        transitionValues.values.put(PROPNAME_MATRIX, scaleImageView.matrix)
    }

    override fun captureEndValues(transitionValues: TransitionValues?) {
        captureValue(transitionValues)
    }

    override fun createAnimator(sceneRoot: ViewGroup?, startValues: TransitionValues?, endValues: TransitionValues?): Animator {

        return super.createAnimator(sceneRoot, startValues, endValues)
    }

}

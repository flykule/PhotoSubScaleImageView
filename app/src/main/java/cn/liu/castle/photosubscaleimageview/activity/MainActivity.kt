package cn.liu.castle.photosubscaleimageview.activity

import android.graphics.BitmapFactory
import android.graphics.Rect
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.transition.ChangeImageTransform
import android.util.Log
import android.view.View
import cn.liu.castle.photosubscaleimageview.AnimationUtil
import cn.liu.castle.photosubscaleimageview.FullscreenActivity
import cn.liu.castle.photosubscaleimageview.R
import cn.liu.castle.photosubscaleimageview.R.id.single_small_iv
import cn.liu.castle.photosubscaleimageview.transition.ChangeScaleImageTransform
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import org.jetbrains.anko.find
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.onClick

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //默认小图，禁止缩放
        val imageView = find<SubsamplingScaleImageView>(single_small_iv)
        //设置Stable防止头部抖动
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        imageView.apply {
            setImage(ImageSource.bitmap(BitmapFactory.decodeResource(resources,R.drawable.drawable_first)))
            isZoomEnabled = false
            //点击单个小图时候的事件

        }
        imageView.onClick {
            //通过使用Transition启动方式启动
            val transitionOptions = AnimationUtil.transitionOptions(this@MainActivity, imageView,
                    getString(R.string.single_transition_name))
            val bundle = Bundle()
            ChangeScaleImageTransform.addExtraProperties(imageView,bundle)
            val intent = intentFor<FullscreenActivity>()
            intent.putExtra(getString(R.string.single_transition_name),bundle)
            startActivity(intent, transitionOptions)
        }

    }
}

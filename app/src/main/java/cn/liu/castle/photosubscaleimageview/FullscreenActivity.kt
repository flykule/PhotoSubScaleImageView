package cn.liu.castle.photosubscaleimageview

import android.app.SharedElementCallback
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import org.jetbrains.anko.find
import android.content.Intent
import android.support.v4.app.ActivityCompat.setEnterSharedElementCallback
import android.view.View


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class FullscreenActivity : AppCompatActivity() {
    val mScaleImageView by lazy { find<SubsamplingScaleImageView>(R.id.full_screen_iv) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fullscreen)
        postponeEnterTransition()
        mScaleImageView.apply {
            transitionName = getString(cn.liu.castle.photosubscaleimageview.R.string.single_transition_name)
            setImage(ImageSource.resource(R.drawable.drawable_first))
            //等待加载完成再跳转
        }
        setEnterSharedElementCallback(object : SharedElementCallback() {
            //在共享动画启动时通过这个方法设置起始值
            override fun onSharedElementStart(sharedElementNames: MutableList<String>,
                                              sharedElements: MutableList<View>, sharedElementSnapshots: MutableList<View>) {
                val intent = intent
                val value = sharedElementNames.size - 1
                for (i in 0..value) {
                    val name = sharedElementNames[i]
                    if (intent.hasExtra(name)) {
                        sharedElements[i].setTag(R.id.tag_transition_extra_properties, intent.getBundleExtra(name))
                    }
                }
            }
            //在捕捉结束值的时候在此处进行，因此必须清空
            override fun onSharedElementEnd(sharedElementNames: MutableList<String>?, sharedElements: MutableList<View>, sharedElementSnapshots: MutableList<View>?) {
                super.onSharedElementEnd(sharedElementNames, sharedElements, sharedElementSnapshots)
                for (view in sharedElements) {
                    view.setTag(R.id.tag_transition_extra_properties, null)
                }
            }
        })
        mScaleImageView.post { startPostponedEnterTransition() }


    }

}

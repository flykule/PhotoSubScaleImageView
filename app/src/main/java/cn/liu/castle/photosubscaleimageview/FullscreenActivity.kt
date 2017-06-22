package cn.liu.castle.photosubscaleimageview

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import org.jetbrains.anko.find

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
            post { startPostponedEnterTransition() }
        }
    }

}

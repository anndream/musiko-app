
package app.musiko.glide

import android.graphics.drawable.Drawable
import android.widget.ImageView
import app.musiko.appthemehelper.util.ATHUtil
import app.musiko.App
import app.musiko.R
import app.musiko.glide.palette.BitmapPaletteTarget
import app.musiko.glide.palette.BitmapPaletteWrapper
import app.musiko.util.color.MediaNotificationProcessor
import com.bumptech.glide.request.animation.GlideAnimation

abstract class MusikoColoredTarget(view: ImageView) : BitmapPaletteTarget(view) {

    protected val defaultFooterColor: Int
        get() = ATHUtil.resolveColor(getView().context, R.attr.colorControlNormal)

    abstract fun onColorReady(colors: MediaNotificationProcessor)

    override fun onLoadFailed(e: Exception?, errorDrawable: Drawable?) {
        super.onLoadFailed(e, errorDrawable)
        val colors = MediaNotificationProcessor(App.getContext(), errorDrawable)
        onColorReady(colors)
    }

    override fun onResourceReady(
        resource: BitmapPaletteWrapper?,
        glideAnimation: GlideAnimation<in BitmapPaletteWrapper>?
    ) {
        super.onResourceReady(resource, glideAnimation)
        resource?.let { bitmapWrap ->
            MediaNotificationProcessor(App.getContext()).getPaletteAsync({
                onColorReady(it)
            }, bitmapWrap.bitmap)
        }
    }
}

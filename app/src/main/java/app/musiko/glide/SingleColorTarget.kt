
package app.musiko.glide

import android.graphics.drawable.Drawable
import android.widget.ImageView
import app.musiko.appthemehelper.util.ATHUtil
import app.musiko.R
import app.musiko.glide.palette.BitmapPaletteTarget
import app.musiko.glide.palette.BitmapPaletteWrapper
import app.musiko.util.ColorUtil
import com.bumptech.glide.request.animation.GlideAnimation

abstract class SingleColorTarget(view: ImageView) : BitmapPaletteTarget(view) {

    private val defaultFooterColor: Int
        get() = ATHUtil.resolveColor(view.context, R.attr.colorControlNormal)

    abstract fun onColorReady(color: Int)

    override fun onLoadFailed(e: Exception?, errorDrawable: Drawable?) {
        super.onLoadFailed(e, errorDrawable)
        onColorReady(defaultFooterColor)
    }

    override fun onResourceReady(
        resource: BitmapPaletteWrapper?,
        glideAnimation: GlideAnimation<in BitmapPaletteWrapper>?
    ) {
        super.onResourceReady(resource, glideAnimation)
        resource?.let {
            onColorReady(
                ColorUtil.getColor(
                    it.palette,
                    ATHUtil.resolveColor(view.context, R.attr.colorPrimary)
                )
            )
        }
    }
}

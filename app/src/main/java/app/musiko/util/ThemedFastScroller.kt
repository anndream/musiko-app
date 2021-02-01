
package app.musiko.util

import android.view.ViewGroup
import app.musiko.appthemehelper.ThemeStore.Companion.accentColor
import app.musiko.appthemehelper.util.ColorUtil.isColorLight
import app.musiko.appthemehelper.util.MaterialValueHelper.getPrimaryTextColor
import app.musiko.appthemehelper.util.TintHelper
import app.musiko.views.PopupBackground
import me.zhanghai.android.fastscroll.FastScroller
import me.zhanghai.android.fastscroll.FastScrollerBuilder
import me.zhanghai.android.fastscroll.PopupStyles
import me.zhanghai.android.fastscroll.R

object ThemedFastScroller {
    fun create(view: ViewGroup): FastScroller {
        val context = view.context
        val color = accentColor(context)
        val textColor = getPrimaryTextColor(context, isColorLight(color))
        val fastScrollerBuilder = FastScrollerBuilder(view)
        fastScrollerBuilder.useMd2Style()
        fastScrollerBuilder.setPopupStyle { popupText ->
            PopupStyles.MD2.accept(popupText)
            popupText.background = PopupBackground(context)
            popupText.setTextColor(textColor)
        }

        fastScrollerBuilder.setThumbDrawable(
            TintHelper.createTintedDrawable(
                context,
                R.drawable.afs_md2_thumb,
                color
            )
        )
        return fastScrollerBuilder.build()
    }
}

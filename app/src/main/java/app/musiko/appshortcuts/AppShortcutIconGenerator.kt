
package app.musiko.appshortcuts

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.graphics.drawable.Icon
import android.graphics.drawable.LayerDrawable
import android.os.Build
import android.util.TypedValue
import androidx.annotation.RequiresApi
import app.musiko.appthemehelper.ThemeStore
import app.musiko.R
import app.musiko.util.PreferenceUtil
import app.musiko.util.MusikoUtil

@RequiresApi(Build.VERSION_CODES.N_MR1)
object AppShortcutIconGenerator {
    fun generateThemedIcon(context: Context, iconId: Int): Icon {
        return if (PreferenceUtil.isColoredAppShortcuts) {
            generateUserThemedIcon(context, iconId)
        } else {
            generateDefaultThemedIcon(context, iconId)
        }
    }

    private fun generateDefaultThemedIcon(context: Context, iconId: Int): Icon {

        return generateThemedIcon(
            context,
            iconId,
            context.getColor(R.color.app_shortcut_default_foreground),
            context.getColor(R.color.app_shortcut_default_background)
        )
    }

    private fun generateUserThemedIcon(context: Context, iconId: Int): Icon {

        val typedColorBackground = TypedValue()
        context.theme.resolveAttribute(android.R.attr.colorBackground, typedColorBackground, true)


        return generateThemedIcon(
            context, iconId, ThemeStore.accentColor(context), typedColorBackground.data
        )
    }

    private fun generateThemedIcon(
        context: Context,
        iconId: Int,
        foregroundColor: Int,
        backgroundColor: Int
    ): Icon {

        val vectorDrawable = MusikoUtil.getTintedVectorDrawable(context, iconId, foregroundColor)
        val backgroundDrawable = MusikoUtil.getTintedVectorDrawable(
            context, R.drawable.ic_app_shortcut_background, backgroundColor
        )


        val layerDrawable = LayerDrawable(arrayOf(backgroundDrawable, vectorDrawable))


        return Icon.createWithBitmap(drawableToBitmap(layerDrawable))
    }

    private fun drawableToBitmap(drawable: Drawable): Bitmap {
        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }
}

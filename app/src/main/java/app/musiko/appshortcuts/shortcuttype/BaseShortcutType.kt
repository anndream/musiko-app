
package app.musiko.appshortcuts.shortcuttype

import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.os.Build
import android.os.Bundle
import app.musiko.appshortcuts.AppShortcutLauncherActivity

@TargetApi(Build.VERSION_CODES.N_MR1)
abstract class BaseShortcutType(internal var context: Context) {

    internal abstract val shortcutInfo: ShortcutInfo


    internal fun getPlaySongsIntent(shortcutType: Long): Intent {
        val intent = Intent(context, AppShortcutLauncherActivity::class.java)
        intent.action = Intent.ACTION_VIEW
        val b = Bundle()
        b.putLong(AppShortcutLauncherActivity.KEY_SHORTCUT_TYPE, shortcutType)
        intent.putExtras(b)
        return intent
    }

    companion object {
        internal const val ID_PREFIX = "app.musiko.appshortcuts.id."
        val id: String
            get() = ID_PREFIX + "invalid"
    }
}

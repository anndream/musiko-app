
package app.musiko.fragments.settings

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceManager
import app.musiko.appthemehelper.common.prefs.supportv7.ATEPreferenceFragmentCompat
import app.musiko.preferences.*
import app.musiko.util.NavigationUtil

/**
 * @author Hemanth S (h4h13).
 */

abstract class AbsSettingsFragment : ATEPreferenceFragmentCompat() {

    internal fun showProToastAndNavigate(message: String) {
        Toast.makeText(requireContext(), "$message is Pro version feature.", Toast.LENGTH_SHORT)
            .show()
        NavigationUtil.goToProVersion(requireActivity())
    }

    internal fun setSummary(preference: Preference, value: Any?) {
        val stringValue = value.toString()
        if (preference is ListPreference) {
            val index = preference.findIndexOfValue(stringValue)
            preference.setSummary(if (index >= 0) preference.entries[index] else null)
        } else {
            preference.summary = stringValue
        }
    }

    abstract fun invalidateSettings()

    protected fun setSummary(preference: Preference?) {
        preference?.let {
            setSummary(
                it, PreferenceManager
                    .getDefaultSharedPreferences(it.context)
                    .getString(it.key, "")
            )
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setDivider(ColorDrawable(Color.TRANSPARENT))
        listView.overScrollMode = View.OVER_SCROLL_NEVER
        listView.setPadding(0, 0, 0, 0)
        listView.setPaddingRelative(0, 0, 0, 0)
        invalidateSettings()
    }

    override fun onDisplayPreferenceDialog(preference: Preference?) {
        when (preference) {
            is LibraryPreference -> {
                val fragment = LibraryPreferenceDialog.newInstance()
                fragment.show(childFragmentManager, preference.key)
            }
            is NowPlayingScreenPreference -> {
                val fragment = NowPlayingScreenPreferenceDialog.newInstance()
                fragment.show(childFragmentManager, preference.key)
            }
            is AlbumCoverStylePreference -> {
                val fragment = AlbumCoverStylePreferenceDialog.newInstance()
                fragment.show(childFragmentManager, preference.key)
            }
            is BlacklistPreference -> {
                val fragment = BlacklistPreferenceDialog.newInstance()
                fragment.show(childFragmentManager, preference.key)
            }
            else -> super.onDisplayPreferenceDialog(preference)
        }
    }
}

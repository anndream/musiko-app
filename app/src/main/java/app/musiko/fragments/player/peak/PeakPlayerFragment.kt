
package app.musiko.fragments.player.peak

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import app.musiko.appthemehelper.util.ATHUtil
import app.musiko.appthemehelper.util.ToolbarContentTintHelper
import app.musiko.R
import app.musiko.extensions.hide
import app.musiko.extensions.show
import app.musiko.fragments.base.AbsPlayerFragment
import app.musiko.fragments.player.PlayerAlbumCoverFragment
import app.musiko.helper.MusicPlayerRemote
import app.musiko.util.PreferenceUtil
import app.musiko.util.color.MediaNotificationProcessor
import kotlinx.android.synthetic.main.fragment_peak_player.*



class PeakPlayerFragment : AbsPlayerFragment(R.layout.fragment_peak_player) {

    private lateinit var controlsFragment: PeakPlayerControlFragment
    private var lastColor: Int = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpPlayerToolbar()
        setUpSubFragments()
        title.isSelected = true
    }

    private fun setUpSubFragments() {
        controlsFragment =
            childFragmentManager.findFragmentById(R.id.playbackControlsFragment) as PeakPlayerControlFragment

        val coverFragment =
            childFragmentManager.findFragmentById(R.id.playerAlbumCoverFragment) as PlayerAlbumCoverFragment
        coverFragment.setCallbacks(this)
    }

    private fun setUpPlayerToolbar() {
        playerToolbar.apply {
            inflateMenu(R.menu.menu_player)
            setNavigationOnClickListener { requireActivity().onBackPressed() }
            setOnMenuItemClickListener(this@PeakPlayerFragment)
            ToolbarContentTintHelper.colorizeToolbar(
                this,
                ATHUtil.resolveColor(context, R.attr.colorControlNormal),
                requireActivity()
            )
        }
    }

    override fun playerToolbar(): Toolbar {
        return playerToolbar
    }

    override fun onShow() {
    }

    override fun onHide() {
    }

    override fun onBackPressed(): Boolean {
        return false
    }

    override fun toolbarIconColor(): Int {
        return ATHUtil.resolveColor(requireContext(), R.attr.colorControlNormal)
    }

    override val paletteColor: Int
        get() = lastColor

    override fun onColorChanged(color: MediaNotificationProcessor) {
        lastColor = color.primaryTextColor
        libraryViewModel.updateColor(color.primaryTextColor)
        controlsFragment.setColor(color)
    }

    override fun onFavoriteToggled() {
    }

    private fun updateSong() {
        val song = MusicPlayerRemote.currentSong
        title.text = song.title
        text.text = song.artistName

        if (PreferenceUtil.isSongInfo) {
            songInfo.text = getSongInfo(song)
            songInfo.show()
        } else {
            songInfo.hide()
        }
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        updateSong()
    }

    override fun onPlayingMetaChanged() {
        super.onPlayingMetaChanged()
        updateSong()
    }
}

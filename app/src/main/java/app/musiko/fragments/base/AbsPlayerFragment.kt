
package app.musiko.fragments.base

import android.content.ContentUris
import android.content.Intent
import android.graphics.drawable.Drawable
import android.media.MediaMetadataRetriever
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.appcompat.widget.Toolbar
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import app.musiko.EXTRA_ALBUM_ID
import app.musiko.EXTRA_ARTIST_ID
import app.musiko.R
import app.musiko.activities.tageditor.AbsTagEditorActivity
import app.musiko.activities.tageditor.SongTagEditorActivity
import app.musiko.db.PlaylistEntity
import app.musiko.db.SongEntity
import app.musiko.db.toSongEntity
import app.musiko.dialogs.*
import app.musiko.extensions.hide
import app.musiko.extensions.whichFragment
import app.musiko.fragments.ReloadType
import app.musiko.fragments.player.PlayerAlbumCoverFragment
import app.musiko.helper.MusicPlayerRemote
import app.musiko.interfaces.IPaletteColorHolder
import app.musiko.model.Song
import app.musiko.model.lyrics.Lyrics
import app.musiko.repository.RealRepository
import app.musiko.service.MusicService
import app.musiko.util.*
import kotlinx.android.synthetic.main.shadow_statusbar_toolbar.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.get
import java.io.FileNotFoundException

abstract class AbsPlayerFragment(@LayoutRes layout: Int) : AbsMainActivityFragment(layout),
    Toolbar.OnMenuItemClickListener, IPaletteColorHolder, PlayerAlbumCoverFragment.Callbacks {

    private var playerAlbumCoverFragment: PlayerAlbumCoverFragment? = null

    override fun onMenuItemClick(
        item: MenuItem
    ): Boolean {
        val song = MusicPlayerRemote.currentSong
        when (item.itemId) {
            R.id.action_toggle_favorite -> {
                toggleFavorite(song)
                return true
            }
            R.id.action_share -> {
                SongShareDialog.create(song).show(childFragmentManager, "SHARE_SONG")
                return true
            }
            R.id.action_go_to_drive_mode -> {
                NavigationUtil.gotoDriveMode(requireActivity())
                return true
            }
            R.id.action_delete_from_device -> {
                DeleteSongsDialog.create(song).show(childFragmentManager, "DELETE_SONGS")
                return true
            }
            R.id.action_add_to_playlist -> {
                lifecycleScope.launch(IO) {
                    val playlists = get<RealRepository>().fetchPlaylists()
                    withContext(Main) {
                        AddToPlaylistDialog.create(playlists, song)
                            .show(childFragmentManager, "ADD_PLAYLIST")
                    }
                }
                return true
            }
            R.id.action_clear_playing_queue -> {
                MusicPlayerRemote.clearQueue()
                return true
            }
            R.id.action_save_playing_queue -> {
                CreatePlaylistDialog.create(ArrayList(MusicPlayerRemote.playingQueue))
                    .show(childFragmentManager, "ADD_TO_PLAYLIST")
                return true
            }
            R.id.action_tag_editor -> {
                val intent = Intent(activity, SongTagEditorActivity::class.java)
                intent.putExtra(AbsTagEditorActivity.EXTRA_ID, song.id)
                startActivity(intent)
                return true
            }
            R.id.action_details -> {
                SongDetailDialog.create(song).show(childFragmentManager, "SONG_DETAIL")
                return true
            }
            R.id.action_go_to_album -> {
                mainActivity.collapsePanel()
                requireActivity().findNavController(R.id.fragment_container).navigate(
                    R.id.albumDetailsFragment,
                    bundleOf(EXTRA_ALBUM_ID to song.albumId)
                )
                return true
            }
            R.id.action_go_to_artist -> {
                mainActivity.collapsePanel()
                requireActivity().findNavController(R.id.fragment_container).navigate(
                    R.id.artistDetailsFragment,
                    bundleOf(EXTRA_ARTIST_ID to song.artistId)
                )
                return true
            }
            R.id.now_playing -> {
                NavigationUtil.goToPlayingQueue(requireActivity())
                return true
            }
            R.id.action_show_lyrics -> {
                NavigationUtil.goToLyrics(requireActivity())
                return true
            }
            R.id.action_equalizer -> {
                NavigationUtil.openEqualizer(requireActivity())
                return true
            }
            R.id.action_sleep_timer -> {
                SleepTimerDialog().show(parentFragmentManager, TAG)
                return true
            }
            R.id.action_set_as_ringtone -> {
                if (RingtoneManager.requiresDialog(requireActivity())) {
                    RingtoneManager.getDialog(requireActivity())
                }
                val ringtoneManager = RingtoneManager(requireActivity())
                ringtoneManager.setRingtone(song)
                return true
            }
            R.id.action_go_to_genre -> {
                val retriever = MediaMetadataRetriever()
                val trackUri =
                    ContentUris.withAppendedId(
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        song.id.toLong()
                    )
                retriever.setDataSource(activity, trackUri)
                var genre: String? =
                    retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE)
                if (genre == null) {
                    genre = "Not Specified"
                }
                Toast.makeText(context, genre, Toast.LENGTH_SHORT).show()
                return true
            }
        }
        return false
    }

    abstract fun playerToolbar(): Toolbar?

    abstract fun onShow()

    abstract fun onHide()

    abstract fun onBackPressed(): Boolean

    abstract fun toolbarIconColor(): Int

    override fun onServiceConnected() {
        updateIsFavorite()
        updateLyrics()
    }

    override fun onPlayingMetaChanged() {
        updateIsFavorite()
        updateLyrics()
    }

    protected open fun toggleFavorite(song: Song) {
        lifecycleScope.launch(IO) {
            val playlist: PlaylistEntity? = libraryViewModel.favoritePlaylist()
            if (playlist != null) {
                val songEntity = song.toSongEntity(playlist.playListId)
                val isFavorite = libraryViewModel.isFavoriteSong(songEntity).isNotEmpty()
                if (isFavorite) {
                    libraryViewModel.removeSongFromPlaylist(songEntity)
                } else {
                    libraryViewModel.insertSongs(listOf(song.toSongEntity(playlist.playListId)))
                }
            }
            libraryViewModel.forceReload(ReloadType.Playlists)
            requireContext().sendBroadcast(Intent(MusicService.FAVORITE_STATE_CHANGED))
        }
    }

    fun updateIsFavorite() {
        lifecycleScope.launch(IO) {
            val playlist: PlaylistEntity? = libraryViewModel.favoritePlaylist()
            if (playlist != null) {
                val song: SongEntity =
                    MusicPlayerRemote.currentSong.toSongEntity(playlist.playListId)
                val isFavorite: Boolean = libraryViewModel.isFavoriteSong(song).isNotEmpty()
                withContext(Main) {
                    val icon =
                        if (isFavorite) R.drawable.ic_favorite else R.drawable.ic_favorite_border
                    val drawable: Drawable? = MusikoUtil.getTintedVectorDrawable(
                        requireContext(),
                        icon,
                        toolbarIconColor()
                    )
                    if (playerToolbar() != null) {
                        playerToolbar()?.menu?.findItem(R.id.action_toggle_favorite)
                            ?.setIcon(drawable)?.title =
                            if (isFavorite) getString(R.string.action_remove_from_favorites)
                            else getString(R.string.action_add_to_favorites)
                    }
                }
            }
        }
    }

    private fun updateLyrics() {
        setLyrics(null)
        lifecycleScope.launch(IO) {
            val song = MusicPlayerRemote.currentSong
            val lyrics = try {
                var data: String? = LyricUtil.getStringFromFile(song.title, song.artistName)
                if (TextUtils.isEmpty(data)) {
                    data = MusicUtil.getLyrics(song)
                    if (TextUtils.isEmpty(data)) {
                        null
                    } else {
                        Lyrics.parse(song, data)
                    }
                } else Lyrics.parse(song, data!!)
            } catch (err: FileNotFoundException) {
                null
            }
            withContext(Main) {
                setLyrics(lyrics)
            }
        }
    }

    open fun setLyrics(l: Lyrics?) {
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (PreferenceUtil.isFullScreenMode &&
            view.findViewById<View>(R.id.status_bar) != null
        ) {
            view.findViewById<View>(R.id.status_bar).visibility = View.GONE
        }
        playerAlbumCoverFragment = whichFragment(R.id.playerAlbumCoverFragment)
        playerAlbumCoverFragment?.setCallbacks(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            statusBarShadow?.hide()
    }

    companion object {
        val TAG: String = AbsPlayerFragment::class.java.simpleName
        const val VISIBILITY_ANIM_DURATION: Long = 300
    }

    protected fun getUpNextAndQueueTime(): String {
        val duration = MusicPlayerRemote.getQueueDurationMillis(MusicPlayerRemote.position)

        return MusicUtil.buildInfoString(
            resources.getString(R.string.up_next),
            MusicUtil.getReadableDurationString(duration)
        )
    }
}

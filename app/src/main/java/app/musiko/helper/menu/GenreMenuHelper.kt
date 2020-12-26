
package app.musiko.helper.menu

import android.view.MenuItem
import androidx.fragment.app.FragmentActivity
import app.musiko.R
import app.musiko.dialogs.AddToPlaylistDialog
import app.musiko.helper.MusicPlayerRemote
import app.musiko.model.Genre
import app.musiko.model.Song
import app.musiko.repository.GenreRepository
import app.musiko.repository.RealRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.KoinComponent
import org.koin.core.get
import org.koin.core.inject

object GenreMenuHelper : KoinComponent {
    private val genreRepository by inject<GenreRepository>()
    fun handleMenuClick(activity: FragmentActivity, genre: Genre, item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_play -> {
                MusicPlayerRemote.openQueue(getGenreSongs(genre), 0, true)
                return true
            }
            R.id.action_play_next -> {
                MusicPlayerRemote.playNext(getGenreSongs(genre))
                return true
            }
            R.id.action_add_to_playlist -> {
                CoroutineScope(Dispatchers.IO).launch {
                    val playlists = get<RealRepository>().fetchPlaylists()
                    withContext(Dispatchers.Main) {
                        AddToPlaylistDialog.create(playlists, getGenreSongs(genre))
                            .show(activity.supportFragmentManager, "ADD_PLAYLIST")
                    }
                }
                return true
            }
            R.id.action_add_to_current_playing -> {
                MusicPlayerRemote.enqueue(getGenreSongs(genre))
                return true
            }
        }
        return false
    }

    private fun getGenreSongs(genre: Genre): List<Song> {
        return genreRepository.songs(genre.id)
    }
}

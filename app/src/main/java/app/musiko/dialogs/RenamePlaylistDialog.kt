
package app.musiko.dialogs

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import app.musiko.EXTRA_PLAYLIST_ID
import app.musiko.R
import app.musiko.db.PlaylistEntity
import app.musiko.extensions.accentColor
import app.musiko.extensions.colorButtons
import app.musiko.extensions.extraNotNull
import app.musiko.extensions.materialDialog
import app.musiko.fragments.LibraryViewModel
import app.musiko.fragments.ReloadType
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class RenamePlaylistDialog : DialogFragment() {

    private val libraryViewModel by sharedViewModel<LibraryViewModel>()

    companion object {
        fun create(playlistEntity: PlaylistEntity): RenamePlaylistDialog {
            return RenamePlaylistDialog().apply {
                arguments = bundleOf(
                    EXTRA_PLAYLIST_ID to playlistEntity
                )
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val playlistEntity = extraNotNull<PlaylistEntity>(EXTRA_PLAYLIST_ID).value
        val layout = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_playlist, null)
        val inputEditText: TextInputEditText = layout.findViewById(R.id.actionNewPlaylist)
        val nameContainer: TextInputLayout = layout.findViewById(R.id.actionNewPlaylistContainer)
        nameContainer.accentColor()
        inputEditText.setText(playlistEntity.playlistName)
        return materialDialog(R.string.rename_playlist_title)
            .setView(layout)
            .setNegativeButton(android.R.string.cancel, null)
            .setPositiveButton(R.string.action_rename) { _, _ ->
                val name = inputEditText.text.toString()
                if (name.isNotEmpty()) {
                    libraryViewModel.renameRoomPlaylist(playlistEntity.playListId, name)
                    libraryViewModel.forceReload(ReloadType.Playlists)
                } else {
                    nameContainer.error = "Playlist name should'nt be empty"
                }
            }
            .create()
            .colorButtons()
    }
}

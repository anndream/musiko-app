package app.musiko.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import app.musiko.R
import app.musiko.extensions.toFormattedDuration
import app.musiko.extensions.toSpanned
import app.musiko.goPreferences
import app.musiko.helpers.DialogHelper
import app.musiko.helpers.MusicOrgHelper
import app.musiko.models.Album
import app.musiko.models.Music
import app.musiko.models.SavedMusic
import app.musiko.player.MediaPlayerHolder
import app.musiko.ui.UIControlInterface

class LovedSongsAdapter(
        private val context: Context,
        private val lovedSongsDialog: MaterialDialog,
        private val mediaPlayerHolder: MediaPlayerHolder,
        private val uiControlInterface: UIControlInterface,
        private val deviceSongs: MutableList<Music>,
        private val deviceAlbumsByArtist: MutableMap<String, List<Album>>?
) :
        RecyclerView.Adapter<LovedSongsAdapter.LoveHolder>() {

    private var mLovedSongs = goPreferences.lovedSongs?.toMutableList()

    fun swapSongs(lovedSongs: MutableList<SavedMusic>?) {
        mLovedSongs = lovedSongs
        notifyDataSetChanged()
        uiControlInterface.onLovedSongsUpdate(false)
        if (mLovedSongs?.isEmpty()!!) {
            lovedSongsDialog.dismiss()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LoveHolder {
        return LoveHolder(
                LayoutInflater.from(parent.context).inflate(
                        R.layout.music_item,
                        parent,
                        false
                )
        )
    }

    override fun getItemCount(): Int {
        return mLovedSongs?.size!!
    }

    override fun onBindViewHolder(holder: LoveHolder, position: Int) {
        holder.bindItems(mLovedSongs?.get(holder.adapterPosition))
    }

    inner class LoveHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(lovedSong: SavedMusic?) {

            val title = itemView.findViewById<TextView>(R.id.title)
            val duration = itemView.findViewById<TextView>(R.id.duration)
            val subtitle = itemView.findViewById<TextView>(R.id.subtitle)

            title.text = lovedSong?.title
            duration.text = context.getString(
                    R.string.loved_song_subtitle,
                    lovedSong?.startFrom?.toLong()?.toFormattedDuration(
                            isAlbum = false,
                            isSeekBar = false
                    ),
                    lovedSong?.duration?.toFormattedDuration(isAlbum = false, isSeekBar = false)
            ).toSpanned()
            subtitle.text =
                    context.getString(R.string.artist_and_album, lovedSong?.artist, lovedSong?.album)

            itemView.apply {
                setOnClickListener {
                    mediaPlayerHolder.isSongFromLovedSongs =
                            Pair(true, lovedSong?.startFrom!!)
                    MusicOrgHelper.getSongForRestore(lovedSong, deviceSongs)
                            .apply {
                                uiControlInterface.onSongSelected(
                                        this,
                                        MusicOrgHelper.getAlbumSongs(
                                                artist,
                                                album,
                                                deviceAlbumsByArtist
                                        ),
                                        lovedSong.launchedBy
                                )
                            }
                }
                setOnLongClickListener {
                    DialogHelper.showDeleteLovedSongDialog(
                            context,
                            lovedSong,
                            this@LovedSongsAdapter
                    )
                    return@setOnLongClickListener true
                }
            }
        }
    }
}

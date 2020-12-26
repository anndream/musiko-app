package app.musiko.interfaces

import app.musiko.model.Album
import app.musiko.model.Artist
import app.musiko.model.Genre

interface IHomeClickListener {
    fun onAlbumClick(album: Album)

    fun onArtistClick(artist: Artist)

    fun onGenreClick(genre: Genre)
}
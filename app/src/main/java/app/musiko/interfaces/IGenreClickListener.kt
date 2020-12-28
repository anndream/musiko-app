package app.musiko.interfaces

import android.view.View
import app.musiko.model.Genre

interface IGenreClickListener {
    fun onClickGenre(genre: Genre, view: View)
}

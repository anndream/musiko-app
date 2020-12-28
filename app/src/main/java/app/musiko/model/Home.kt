

package app.musiko.model

import androidx.annotation.StringRes
import app.musiko.HomeSection

data class Home(
    val arrayList: List<Any>,
    @HomeSection
    val homeSection: Int,
    @StringRes
    val titleRes: Int
)

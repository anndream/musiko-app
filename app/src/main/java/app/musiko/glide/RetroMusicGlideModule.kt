
package app.musiko.glide

import android.content.Context
import app.musiko.glide.artistimage.ArtistImage
import app.musiko.glide.artistimage.Factory
import app.musiko.glide.audiocover.AudioFileCover
import app.musiko.glide.audiocover.AudioFileCoverLoader
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.module.GlideModule
import java.io.InputStream

class MusikoGlideModule : GlideModule {
    override fun applyOptions(context: Context, builder: GlideBuilder) {
    }

    override fun registerComponents(context: Context, glide: Glide) {
        glide.register(
            AudioFileCover::class.java,
            InputStream::class.java,
            AudioFileCoverLoader.Factory()
        )
        glide.register(ArtistImage::class.java, InputStream::class.java, Factory(context))
    }
}


package app.musiko.views

import android.content.Context
import android.util.AttributeSet
import app.musiko.R
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.ShapeAppearanceModel


class MusikoShapeableImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = -1
) : ShapeableImageView(context, attrs, defStyle) {


    init {
        val typedArray =
            context.obtainStyledAttributes(attrs, R.styleable.MusikoShapeableImageView, defStyle, -1)
        val cornerSize = typedArray.getDimension(R.styleable.MusikoShapeableImageView_retroCornerSize, 0f)
        val circleShape = typedArray.getBoolean(R.styleable.MusikoShapeableImageView_circleShape, false)
        addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
            val radius = width / 2f
            shapeAppearanceModel = ShapeAppearanceModel().withCornerSize(radius)
        }
        typedArray.recycle()
    }

    private fun updateCornerSize(cornerSize: Float) {
        shapeAppearanceModel = ShapeAppearanceModel.Builder()
            .setAllCorners(CornerFamily.ROUNDED, cornerSize)
            .build()
    }

    //For square
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec)
    }
}

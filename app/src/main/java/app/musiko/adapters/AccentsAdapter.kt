package app.musiko.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.recyclerview.widget.RecyclerView
import app.musiko.R
import app.musiko.extensions.handleViewVisibility
import app.musiko.goPreferences
import app.musiko.helpers.ThemeHelper

class AccentsAdapter(private val activity: Activity) :
        RecyclerView.Adapter<AccentsAdapter.AccentsHolder>() {

    private val mAccents = ThemeHelper.accents
    private var mSelectedAccent = goPreferences.accent

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccentsHolder {
        return AccentsHolder(
                LayoutInflater.from(parent.context).inflate(
                        R.layout.accent_item,
                        parent,
                        false
                )
        )
    }

    override fun getItemCount() = mAccents.size

    override fun onBindViewHolder(holder: AccentsHolder, position: Int) {
        holder.bindItems(mAccents[holder.adapterPosition].first)
    }

    inner class AccentsHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(color: Int) {

            itemView.apply {

                val circle = findViewById<ImageButton>(R.id.circle)
                ThemeHelper.getColor(
                        context,
                        color,
                        R.color.deep_purple
                ).apply {
                    ThemeHelper.updateIconTint(circle, this)
                    ThemeHelper.createColouredRipple(activity, this, R.drawable.ripple_oval)
                            ?.apply {
                                itemView.background = this
                            }
                }

                findViewById<ImageButton>(R.id.check).handleViewVisibility(color == mSelectedAccent)

                setOnClickListener {
                    if (mAccents[adapterPosition].first != mSelectedAccent) {
                        mSelectedAccent = mAccents[adapterPosition].first
                        goPreferences.accent = mSelectedAccent
                    }
                }
            }
        }
    }
}

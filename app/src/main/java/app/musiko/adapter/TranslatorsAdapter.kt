
package app.musiko.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import app.musiko.R
import app.musiko.extensions.hide
import app.musiko.model.Contributor
import app.musiko.util.MusikoUtil
import app.musiko.views.MusikoShapeableImageView

class TranslatorsAdapter(
    private var contributors: List<Contributor>
) : RecyclerView.Adapter<TranslatorsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_contributor,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val contributor = contributors[position]
        holder.bindData(contributor)
        holder.itemView.setOnClickListener {
            MusikoUtil.openUrl(it?.context as Activity, contributors[position].link)
        }
    }

    override fun getItemCount(): Int {
        return contributors.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.title)
        val text: TextView = itemView.findViewById(R.id.text)
        val image: MusikoShapeableImageView = itemView.findViewById(R.id.icon)

        internal fun bindData(contributor: Contributor) {
            title.text = contributor.name
            text.text = contributor.summary
            image.hide()
        }
    }
}

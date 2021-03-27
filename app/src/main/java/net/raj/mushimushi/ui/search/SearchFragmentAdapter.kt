package net.raj.mushimushi.ui.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import net.raj.mushimushi.R
import net.raj.mushimushi.models.Post

class SearchFragmentAdapter( options : FirestoreRecyclerOptions<Post>) : FirestoreRecyclerAdapter<Post, SearchFragmentAdapter.Viewholder>(
    options
) {
    class Viewholder(itemView : View) : RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Viewholder {
        return Viewholder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_post, parent, false)
        )

    }

    override fun onBindViewHolder(holder: Viewholder, position: Int, model: Post) {
        TODO("Not yet implemented")
    }
}
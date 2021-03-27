package net.raj.mushimushi.ui.comments

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import net.raj.mushimushi.R
import net.raj.mushimushi.models.Comments

class CommentAdapter(options: FirestoreRecyclerOptions<Comments>) : FirestoreRecyclerAdapter<Comments,CommentAdapter.ViewHolder>(
        options
) {

    class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val commentBox : TextView = itemView.findViewById(R.id.commentBox)
        val commentedBy : TextView = itemView.findViewById(R.id.commentedByUserTextView)
        val userCommentImage : ImageView = itemView.findViewById(R.id.userCommentImageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_comment, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: Comments) {
        holder.commentBox.text = model.text
        holder.commentedBy.text = model.user.displayName.toString()
        Glide.with(holder.userCommentImage.context).load(model.user.imageUrl).centerCrop().into(holder.userCommentImage)
        Log.d("CommentAdapter",model.text)
    }


}
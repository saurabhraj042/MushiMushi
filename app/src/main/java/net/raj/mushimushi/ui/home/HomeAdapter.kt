package net.raj.mushimushi.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import net.raj.mushimushi.R
import net.raj.mushimushi.models.Post
import net.raj.mushimushi.ui.Utils

class HomeAdapter(options : FirestoreRecyclerOptions<Post>,val listener : IPostAdapter) : FirestoreRecyclerAdapter<Post, HomeAdapter.HomeAdapterViewholder>(
    options
) {

    class HomeAdapterViewholder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val postText: TextView = itemView.findViewById(R.id.postTitle)
        val userText: TextView = itemView.findViewById(R.id.userName)
        val createdAt: TextView = itemView.findViewById(R.id.createdAt)
        val likeCount: TextView = itemView.findViewById(R.id.likeCount)
        val userImage: ImageView = itemView.findViewById(R.id.userImage)
        val likeButton: ImageView = itemView.findViewById(R.id.likeButton)
        val commentButton : ImageView = itemView.findViewById(R.id.commentButton)
        val hahaButton : ImageView = itemView.findViewById(R.id.hahaEmojiImageView)
        val hahaCount : TextView = itemView.findViewById(R.id.hahaCount)
        val angryButton : ImageView = itemView.findViewById(R.id.angryEmojiImageView)
        val angryCount : TextView = itemView.findViewById(R.id.angryCount)
        val sadButton : ImageView = itemView.findViewById(R.id.sadEmojiImageView)
        val sadCount : TextView = itemView.findViewById(R.id.sadCount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeAdapterViewholder {
        val viewHolder =  HomeAdapterViewholder(LayoutInflater.from(parent.context).inflate(R.layout.item_post, parent, false))
        viewHolder.likeButton.setOnClickListener{
            listener.onReactionClicked(snapshots.getSnapshot(viewHolder.adapterPosition).id,"like")
        }
        viewHolder.hahaButton.setOnClickListener{
            listener.onReactionClicked(snapshots.getSnapshot(viewHolder.adapterPosition).id,"haha")
        }
        viewHolder.sadButton.setOnClickListener {
            listener.onReactionClicked(snapshots.getSnapshot(viewHolder.adapterPosition).id,"sad")
        }
        viewHolder.angryButton.setOnClickListener {
            listener.onReactionClicked(snapshots.getSnapshot(viewHolder.adapterPosition).id,"angry")
        }
        viewHolder.commentButton.setOnClickListener{
            listener.onCommentButtonClicked(snapshots.getSnapshot(viewHolder.adapterPosition).id)
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: HomeAdapterViewholder, position: Int, model: Post) {
        holder.postText.text = model.textBody
        holder.userText.text = model.user.displayName

        Glide.with(holder.userImage.context).load(model.user.imageUrl)
                .apply(RequestOptions()
                        .format(DecodeFormat.PREFER_ARGB_8888)
                        .override(Target.SIZE_ORIGINAL))
                .into(holder.userImage)

        holder.likeCount.text = model.likes.size.toString()
        holder.hahaCount.text = model.hahaReaction.size.toString()
        holder.sadCount.text = model.sadReaction.size.toString()
        holder.angryCount.text = model.angryReaction.size.toString()
        holder.createdAt.text = Utils.getTimeAgo(model.createdAt)
        holder.commentButton.setImageDrawable(ContextCompat.getDrawable(holder.commentButton.context,R.drawable.ic_comment))

        val auth = Firebase.auth
        val currentUserId = auth.currentUser!!.uid
        val isLiked = model.likes.contains(currentUserId)
        if(isLiked) {
            holder.likeButton.setImageDrawable(ContextCompat.getDrawable(holder.likeButton.context, R.drawable.ic_liked))
        } else {
            holder.likeButton.setImageDrawable(ContextCompat.getDrawable(holder.likeButton.context, R.drawable.ic_unliked))
        }
    }
}

interface IPostAdapter {
    fun onReactionClicked(postId: String,type : String)
    fun onCommentButtonClicked(postId: String)
}
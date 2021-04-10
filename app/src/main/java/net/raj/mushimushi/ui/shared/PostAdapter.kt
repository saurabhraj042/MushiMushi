package net.raj.mushimushi.ui.shared

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import net.raj.mushimushi.R
import net.raj.mushimushi.models.Post

class PostAdapter(
    options: FirestoreRecyclerOptions<Post>,
    private val listener: IPostAdapter,
    private val showViewOnEmptyRecycler: View? = null,
    private val titleTextBarSavedPostView : TextView?=null,
    private val titleTextBarSearchPostView : TextView?=null,
) :
    FirestoreRecyclerAdapter<Post, PostAdapter.PostAdapterViewHolder>(
        options
    ) {

    class PostAdapterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtPost: TextView = itemView.findViewById(R.id.txt_body_post)
        val txtUserName: TextView = itemView.findViewById(R.id.txt_user_name_post)
        val txtTimestamp: TextView = itemView.findViewById(R.id.txt_timestamp_post)
        val txtLikeCount: TextView = itemView.findViewById(R.id.txt_like_count_post)
        val imgUser: ImageView = itemView.findViewById(R.id.img_user_post)
        val btLike: ImageView = itemView.findViewById(R.id.btn_like_post)
        val btComment: ImageView = itemView.findViewById(R.id.btn_comment_post)
        val btHaHa: ImageView = itemView.findViewById(R.id.btn_haha_post)
        val txtHaHaCount: TextView = itemView.findViewById(R.id.txt_haha_count_post)
        val btAngry: ImageView = itemView.findViewById(R.id.btn_angry_post)
        val txtAngryCount: TextView = itemView.findViewById(R.id.txt_angry_count_post)
        val btSad: ImageView = itemView.findViewById(R.id.btn_sad_post)
        val btSadCount: TextView = itemView.findViewById(R.id.txt_sad_count_post)
        val btDeletePost: ImageView = itemView.findViewById(R.id.btn_del_post)
        val txtCommentCount: TextView = itemView.findViewById(R.id.txt_comment_count_post)
        val btSavePost : ImageView = itemView.findViewById(R.id.btn_save_post)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostAdapterViewHolder {
        val viewHolder = PostAdapterViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_post, parent, false)
        )
        viewHolder.btLike.setOnClickListener {
            listener.onReactionBTClicked(
                snapshots.getSnapshot(viewHolder.adapterPosition).id,
                "like"
            )
        }
        viewHolder.btHaHa.setOnClickListener {
            listener.onReactionBTClicked(
                snapshots.getSnapshot(viewHolder.adapterPosition).id,
                "haha"
            )
        }
        viewHolder.btSad.setOnClickListener {
            listener.onReactionBTClicked(
                snapshots.getSnapshot(viewHolder.adapterPosition).id,
                "sad"
            )
        }
        viewHolder.btAngry.setOnClickListener {
            listener.onReactionBTClicked(
                snapshots.getSnapshot(viewHolder.adapterPosition).id,
                "angry"
            )
        }
        viewHolder.btComment.setOnClickListener {
            listener.onCommentBTClicked(snapshots.getSnapshot(viewHolder.adapterPosition).id)
        }
        viewHolder.btDeletePost.setOnClickListener {
            listener.onPostDeleteBTClicked(snapshots.getSnapshot(viewHolder.adapterPosition).id)
        }
        viewHolder.btSavePost.setOnClickListener {
            listener.onSaveBTClicked(snapshots.getSnapshot(viewHolder.adapterPosition).id)
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: PostAdapterViewHolder, position: Int, model: Post) {
        holder.txtPost.text = model.textBody
        holder.txtUserName.text = model.user.displayName
        Glide.with(holder.imgUser.context).load(model.user.imageUrl)
            .circleCrop()
            .apply(
                RequestOptions()
                    .format(DecodeFormat.PREFER_ARGB_8888)
                    .override(Target.SIZE_ORIGINAL)
            )
            .into(holder.imgUser)

        holder.txtLikeCount.text = model.likes.size.toString()
        holder.txtHaHaCount.text = model.haHaReaction.size.toString()
        holder.btSadCount.text = model.sadReaction.size.toString()
        holder.txtAngryCount.text = model.angryReaction.size.toString()
        holder.txtTimestamp.text = Utils.getTimeAgo(model.createdAt)
        holder.txtCommentCount.text = model.commentCount.toString()
        holder.btComment.setImageDrawable(
            ContextCompat.getDrawable(
                holder.btComment.context,
                R.drawable.ic_comment
            )
        )


        val auth = Firebase.auth
        val currentUserId = auth.currentUser!!.uid
        val userEmail = auth.currentUser!!.email!!

        val isLiked = model.likes.contains(currentUserId)
        if (isLiked) {
            holder.btLike.setImageDrawable(
                ContextCompat.getDrawable(
                    holder.btLike.context,
                    R.drawable.ic_liked
                )
            )
        } else {
            holder.btLike.setImageDrawable(
                ContextCompat.getDrawable(
                    holder.btLike.context,
                    R.drawable.ic_unliked
                )
            )
        }

        val isPostSavedByUser = model.savedByUsers.contains(currentUserId)
        if (isPostSavedByUser) {
            holder.btSavePost.setImageDrawable(
                ContextCompat.getDrawable(
                    holder.btSavePost.context,
                    R.drawable.ic_saved
                )
            )
        } else {
            holder.btSavePost.setImageDrawable(
                ContextCompat.getDrawable(
                    holder.btSavePost.context,
                    R.drawable.ic_save
                )
            )
        }


        val isPostOwner = model.postId.contains(userEmail)
        holder.btDeletePost.visibility = if (isPostOwner) View.VISIBLE else View.GONE
        holder.txtCommentCount.visibility = if (model.commentCount == 0) View.GONE else View.VISIBLE
        holder.txtLikeCount.visibility = if (model.likes.size != 0) View.VISIBLE else View.GONE
        holder.txtLikeCount.visibility = if (model.likes.size != 0) View.VISIBLE else View.GONE
        holder.txtHaHaCount.visibility =
            if (model.haHaReaction.size != 0) View.VISIBLE else View.GONE
        holder.btSadCount.visibility = if (model.sadReaction.size != 0) View.VISIBLE else View.GONE
        holder.txtAngryCount.visibility =
            if (model.angryReaction.size != 0) View.VISIBLE else View.GONE


    }

    override fun onDataChanged() {
        showViewOnEmptyRecycler?.let{
            it.visibility = if (itemCount == 0) View.VISIBLE else View.GONE
        }

        titleTextBarSavedPostView?.let {
            it.text = "Saved Posts (${itemCount})"
        }

        titleTextBarSearchPostView?.let {
            it.text = "Search Posts (${itemCount})"
        }
    }
}

interface IPostAdapter {
    fun onSaveBTClicked(postId: String)
    fun onPostDeleteBTClicked(postId: String)
    fun onReactionBTClicked(postId: String, type: String)
    fun onCommentBTClicked(postId: String)
}
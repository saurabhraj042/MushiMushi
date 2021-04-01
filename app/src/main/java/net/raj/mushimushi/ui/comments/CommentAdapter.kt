package net.raj.mushimushi.ui.comments

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import net.raj.mushimushi.R
import net.raj.mushimushi.models.Comments
import net.raj.mushimushi.ui.shared.Utils

class CommentAdapter(
    options: FirestoreRecyclerOptions<Comments>,
    private val listener: ICommentAdapter,
    private val viewOnEmpty: View
) :
    FirestoreRecyclerAdapter<Comments, CommentAdapter.ViewHolder>(
        options
    ) {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtComment: TextView = itemView.findViewById(R.id.txt_comment_text)
        val txtUserName: TextView = itemView.findViewById(R.id.txt_user_name_comment)
        val txtTimeStamp: TextView = itemView.findViewById(R.id.txt_timestamp)
        val txtLikeCount: TextView = itemView.findViewById(R.id.txt_like_count_comment)
        val txtDislikeCount: TextView = itemView.findViewById(R.id.txt_dislike_count)
        val imgUserImage: ImageView = itemView.findViewById(R.id.img_user_comment)
        val btnDelete: ImageView = itemView.findViewById(R.id.btn_delete_comment)
        val btnLike: ImageView = itemView.findViewById(R.id.btn_like_comment)
        val btnDislike: ImageView = itemView.findViewById(R.id.btn_dislike_comment)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewHolder = ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_comment, parent, false)
        )


        viewHolder.btnDelete.setOnClickListener {
            listener.onDeleteCommentClicked(
                snapshots.getSnapshot(viewHolder.adapterPosition).id,
                snapshots.getSnapshot(viewHolder.adapterPosition)
                    .toObject(Comments::class.java)!!.postId
            )
        }

        viewHolder.btnLike.setOnClickListener {
            listener.onReactionClicked(
                snapshots.getSnapshot(viewHolder.adapterPosition).id,
                "like"
            )
        }

        viewHolder.btnDislike.setOnClickListener {
            listener.onReactionClicked(
                snapshots.getSnapshot(viewHolder.adapterPosition).id,
                "dislike"
            )
        }

        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: Comments) {
        holder.txtComment.text = model.text
        holder.txtUserName.text = model.user.displayName.toString()
        holder.txtLikeCount.text = model.likes.size.toString()
        holder.txtDislikeCount.text = model.dislikes.size.toString()
        Glide.with(holder.imgUserImage.context).load(model.user.imageUrl).centerCrop()
            .into(holder.imgUserImage)
        holder.txtTimeStamp.text = Utils.getTimeAgo(model.createdAt)

        val currentUserUid = Firebase.auth.currentUser!!.email!!.toString()
        val isCommentOwner = model.user.uid.contains(currentUserUid)

        holder.btnDelete.visibility = if (isCommentOwner) View.VISIBLE else View.GONE
        holder.txtDislikeCount.visibility =
            if (model.dislikes.size == 0) View.GONE else View.VISIBLE
        holder.txtLikeCount.visibility = if (model.likes.size == 0) View.GONE else View.VISIBLE


    }

    override fun onDataChanged() {
        super.onDataChanged()
        viewOnEmpty.visibility = if (itemCount == 0) View.VISIBLE else View.GONE

    }


    interface ICommentAdapter {
        fun onDeleteCommentClicked(docId: String, postId: String)
        fun onReactionClicked(docId: String, type: String)
    }
}
package net.raj.mushimushi.ui.notification

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
import net.raj.mushimushi.models.Notification
import net.raj.mushimushi.ui.shared.Utils

class NotificationAdapter(
    options: FirestoreRecyclerOptions<Notification>,
    private val listener: INotification,
    private val titleBarTextNotificationView : TextView
) :
    FirestoreRecyclerAdapter<Notification, NotificationAdapter.ViewHolder>(
        options
    ) {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgUser: ImageView = itemView.findViewById(R.id.img_user_notification)
        val txtUserName: TextView = itemView.findViewById(R.id.txt_user_name_notification)
        val txtTypeNotification: TextView = itemView.findViewById(R.id.txt_type_notification)
        val txtTimeStamp: TextView = itemView.findViewById(R.id.txt_timestamp_notification)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewHolder = ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_notification, parent, false)
        )
        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: Notification) {
        Glide.with(holder.imgUser.context).load(model.user.imageUrl).circleCrop()
            .into(holder.imgUser)
        holder.txtUserName.text = model.user.displayName
        holder.txtTypeNotification.text = if (model.type == "comment") "commented" else "reacted"
        holder.txtTimeStamp.text = Utils.getTimeAgo(model.timeStamp).toString()
        holder.itemView.setOnClickListener {
            listener.onItemClick(model.postId)
        }
    }

    interface INotification {
        fun onItemClick(postId: String)
    }

    override fun onDataChanged() {
        titleBarTextNotificationView.text = "Notifications(${itemCount})"
    }
}
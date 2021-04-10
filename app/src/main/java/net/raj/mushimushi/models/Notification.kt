package net.raj.mushimushi.models

data class Notification(
    val postId : String = "",
    val parentPostOwner : User = User(),
    val user: User = User(),
    val type : String = "",
    val timeStamp : Long = 0L
)

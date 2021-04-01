package net.raj.mushimushi.models

data class Comments(
        val text : String = " ",
        val createdAt : Long = 0L,
        val user : User = User(),
        val postId : String = " ",
        val likes : ArrayList<String> = ArrayList(),
        val dislikes : ArrayList<String> = ArrayList(),
)

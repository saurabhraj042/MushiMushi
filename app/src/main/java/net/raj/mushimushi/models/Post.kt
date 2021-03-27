package net.raj.mushimushi.models

data class Post(
        val postId : String = "",
        val user: User = User(),
        val textBody : String = "",
        val createdAt : Long = 0L,
        val likes : ArrayList<String> = ArrayList(),
        val hahaReaction : ArrayList<String> = ArrayList(),
        val angryReaction : ArrayList<String> = ArrayList(),
        val sadReaction : ArrayList<String> = ArrayList()
)

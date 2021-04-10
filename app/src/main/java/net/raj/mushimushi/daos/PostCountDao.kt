package net.raj.mushimushi.daos

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import net.raj.mushimushi.models.PostCount

class PostCountDao {

    private val db = FirebaseFirestore.getInstance()
    private val postCountCollection = db.collection("postCount")

    suspend fun updatePostCount(type : String){
        val postCountObject = postCountCollection.document("COUNT").get().await().toObject(PostCount::class.java)
        if (postCountObject != null) {
            when(type){
                "add" -> {
                    postCountObject.postCount++
                }
                "del" -> {
                    postCountObject.postCount--
                }
            }
            postCountCollection.document("COUNT").set(postCountObject)
        }else{
            postCountCollection.document("COUNT").set(PostCount(0))

        }

    }

    fun getPostCountCollection(): CollectionReference {
        return postCountCollection
    }

}
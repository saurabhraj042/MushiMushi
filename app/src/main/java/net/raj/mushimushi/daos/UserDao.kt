package net.raj.mushimushi.daos

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import net.raj.mushimushi.models.User

class UserDao {
    private val db = FirebaseFirestore.getInstance()
    private val userCollection = db.collection("users")

    fun addUser(user: User) {
        userCollection.document(user.uid).set(user)
    }

    fun getUserById(uId: String): Task<DocumentSnapshot> {
        return userCollection.document(uId).get()
    }

}
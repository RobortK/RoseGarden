package edu.rosehulman.caoz.rosegarden

import android.os.Parcelable
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.android.parcel.Parcelize
import java.time.Duration

@Parcelize
data class Task(var title: String = "", var date:String ="", var startTime:String="", var hour:Int=0, var minute:Int =30, var isDone:Boolean = false): Parcelable {
    @get: Exclude
    var id = ""
    @ServerTimestamp
    var LastTouched: Timestamp?= null
    companion object{
        const val  LAST_TOUCHED_KEY = "lastTouched"
        fun fromSnapShot(snapshot: DocumentSnapshot): Task{
            val task = snapshot.toObject(Task::class.java)!!
            task.id = snapshot.id
            return task
        }
    }
}
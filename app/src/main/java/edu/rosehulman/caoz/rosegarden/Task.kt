package edu.rosehulman.caoz.rosegarden

import android.os.Parcelable
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Task(var title: String = "", var date: List<Int> = listOf(0, 0, 0), var startTime: List<Int> = listOf(0, 0), var hour:Int=0, var minute:Int =30, var stage:Int = 0): Parcelable {
    @get: Exclude
    var id = ""

    var totalTime = startTime[0]*60 +startTime[1]
    companion object{
        const val  START_TIME = "totalTime"
        fun fromSnapShot(snapshot: DocumentSnapshot): Task{
            val task = snapshot.toObject(Task::class.java)!!
            task.id = snapshot.id
            return task
        }
    }
}
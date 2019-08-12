package edu.rosehulman.caoz.rosegarden

import android.content.Context
import edu.rosehulman.caoz.rosegarden.TaskFragment

private const val ALARM_SET_TIME_ID = "backgrounded_time"
private const val PREVIOUS_TIMER_LENGTH_SECONDS_ID = "previous_timer_length_seconds"
private const val TIMER_STATE_ID = "timer.timer_state"
private const val SECONDS_REMAINING_ID = "seconds_remaining"

class PrefUtil(val id: String) {
    //companion object {

        fun getPreviousTimerLengthSeconds(context: Context): Long {
            val preferences = context.getSharedPreferences(id, Context.MODE_PRIVATE)

            return preferences.getLong(PREVIOUS_TIMER_LENGTH_SECONDS_ID,0)
        }


        fun setPreviousTimerLengthSeconds(seconds: Long, context: Context) {
            val editor = context.getSharedPreferences(id, Context.MODE_PRIVATE).edit()
            editor.putLong(PREVIOUS_TIMER_LENGTH_SECONDS_ID, seconds)
            editor.apply()
        }




        fun getTimerState(context: Context): TaskFragment.TimerState {
            val preferences = context.getSharedPreferences(id, Context.MODE_PRIVATE)
            val ordinal = preferences.getInt(TIMER_STATE_ID, 0)
            return TaskFragment.TimerState.values()[ordinal]
        }

        fun setTimerState(state: TaskFragment.TimerState, context: Context) {
            val editor = context.getSharedPreferences(id, Context.MODE_PRIVATE).edit()
            val ordinal = state.ordinal
            editor.putInt(TIMER_STATE_ID, ordinal)
            editor.apply()
        }




        fun getSecondsRemaining(context: Context): Long {
            val preferences = context.getSharedPreferences(id, Context.MODE_PRIVATE)
            return preferences.getLong(SECONDS_REMAINING_ID, 0)
        }

        fun setSecondsRemaining(seconds: Long, context: Context) {
            val editor = context.getSharedPreferences(id, Context.MODE_PRIVATE).edit()
            editor.putLong(SECONDS_REMAINING_ID, seconds)
            editor.apply()
        }



        fun getAlarmSetTime(context: Context): Long{
            val preferences = context.getSharedPreferences(id, Context.MODE_PRIVATE)
            return  preferences.getLong(ALARM_SET_TIME_ID, 0)
        }

        fun setAlarmSetTime(time: Long, context: Context){
            val editor =context.getSharedPreferences(id, Context.MODE_PRIVATE).edit()
            editor.putLong(ALARM_SET_TIME_ID, time)
            editor.apply()
        }
  //  }
}
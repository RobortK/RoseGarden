package edu.rosehulman.caoz.rosegarden

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class TimerExpiredReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        PrefUtil.setTimerState(TaskFragment.TimerState.Stopped, context)
        PrefUtil.setAlarmSetTime(0, context)
    }
}

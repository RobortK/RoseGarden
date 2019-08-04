package edu.rosehulman.caoz.rosegarden

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class TimerExpiredReceiver(val prefUtil: PrefUtil) : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        prefUtil.setTimerState(TaskFragment.TimerState.Stopped, context)
        prefUtil.setAlarmSetTime(0, context)
    }
}

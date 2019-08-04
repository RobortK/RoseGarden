package edu.rosehulman.caoz.rosegarden


import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import kotlinx.android.synthetic.main.fragment_task.*
import kotlinx.android.synthetic.main.fragment_task.view.*
import kotlinx.android.synthetic.main.fragment_task.view.fab_pause
import kotlinx.android.synthetic.main.fragment_task.view.fab_start
import kotlinx.android.synthetic.main.fragment_task.view.fab_stop
import java.util.*


private const val ARG_TASK = "Task"



class TaskFragment : Fragment() {

    private var task: Task? = null
    private lateinit var timer: CountDownTimer
    private var timerLengthSeconds: Long = 0
    private var timerState = TimerState.Stopped
    private var secondsRemaining: Long = 0
    private lateinit var prefUtil:PrefUtil
    companion object {


        @JvmStatic
        fun newInstance(task: Task) =
            TaskFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_TASK,task)
                }
            }
    }

    enum class TimerState{
        Stopped, Paused, Running
    }


    @RequiresApi(Build.VERSION_CODES.KITKAT)
    fun setAlarm(context: Context, nowSeconds: Long, secondsRemaining: Long): Long {
        val wakeUpTime = (nowSeconds + secondsRemaining) * 1000
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, TimerExpiredReceiver(prefUtil)::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0)
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, wakeUpTime, pendingIntent)
        prefUtil.setAlarmSetTime(nowSeconds, context)
        return wakeUpTime
    }

    fun removeAlarm(context: Context){
        val intent = Intent(context, TimerExpiredReceiver(prefUtil)::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)
        prefUtil.setAlarmSetTime(0, context)
    }

    val nowSeconds: Long
        get() = Calendar.getInstance().timeInMillis / 1000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            task = it.getParcelable<Task>(ARG_TASK)

        }
        prefUtil = PrefUtil(task!!.id)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =inflater.inflate(R.layout.fragment_task, container, false)
        view.task_name.text = task!!.title
        var minuteStr =
            if(task!!.minute>=10){
                task!!.minute.toString()
        }
        else{
                "0"+  task!!.minute.toString()
            }
        view.due_time.text = "${if (task!!.hour == 0)minuteStr+" Mins"  else task!!.hour.toString()+"Hours "+minuteStr+"Mins"}"

        view.fab_start.setOnClickListener{v ->
            startTimer()
            timerState =  TimerState.Running
            updateButtons()
        }

        view.fab_pause.setOnClickListener { v ->
            timer.cancel()
            timerState = TimerState.Paused
            updateButtons()
        }

        view.fab_stop.setOnClickListener { v ->
            timer.cancel()
            onTimerFinished()
        }



        return view
    }

    override fun onResume() {
        super.onResume()
        initTimer()
        removeAlarm(context!!)
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onPause() {
        super.onPause()

        if (timerState == TimerState.Running){
            timer.cancel()
            val wakeUpTime = setAlarm(context!!, nowSeconds, secondsRemaining)
            //TODO: start background timer and show notification
        }
        else if (timerState == TimerState.Paused){
            //TODO: show notification
        }

        prefUtil.setPreviousTimerLengthSeconds(timerLengthSeconds, context!!)
        prefUtil.setSecondsRemaining(secondsRemaining, context!!)
        prefUtil.setTimerState(timerState, context!!)
    }

    private fun initTimer(){
        timerState = prefUtil.getTimerState(context!!)

        //we don't want to change the length of the timer which is already running
        //if the length was changed in settings while it was backgrounded
        if (timerState == TimerState.Stopped)
            setNewTimerLength()
        else
            setPreviousTimerLength()

        secondsRemaining = if (timerState == TimerState.Running || timerState == TimerState.Paused)
            prefUtil.getSecondsRemaining(context!!)
        else
            timerLengthSeconds

        val alarmSetTime = prefUtil.getAlarmSetTime(context!!)

        if (alarmSetTime > 0)
            secondsRemaining -= nowSeconds - alarmSetTime

        if (secondsRemaining <= 0)
            onTimerFinished()
        else if (timerState == TimerState.Running)
            startTimer()

        updateButtons()
        updateCountdownUI()
    }

    private fun onTimerFinished(){
        timerState = TimerState.Stopped

        //set the length of the timer to be the one set in SettingsActivity
        //if the length was changed when the timer was running
        setNewTimerLength()

        progressBar.progress = 0

        prefUtil.setSecondsRemaining(timerLengthSeconds, context!!)
        secondsRemaining = timerLengthSeconds

        updateButtons()
        updateCountdownUI()
    }

    private fun startTimer(){
        timerState = TimerState.Running

        timer = object : CountDownTimer(secondsRemaining * 1000, 1000) {
            override fun onFinish() = onTimerFinished()

            override fun onTick(millisUntilFinished: Long) {
                secondsRemaining = millisUntilFinished / 1000
                updateCountdownUI()
            }
        }.start()
    }

    private fun setNewTimerLength(){
        val lengthInMinutes = task!!.hour*60+task!!.minute
        timerLengthSeconds = (lengthInMinutes * 60L)
        progressBar.max = timerLengthSeconds.toInt()
    }

    private fun setPreviousTimerLength(){
        timerLengthSeconds = prefUtil.getPreviousTimerLengthSeconds(context!!)
        progressBar.max = timerLengthSeconds.toInt()
    }

    private fun updateCountdownUI(){
        val totalMinutes = secondsRemaining / 60
        val  hoursUntilFinished = totalMinutes/60
        val minutesUntilFinished = totalMinutes%60
        val secondsInMinuteUntilFinished = secondsRemaining - totalMinutes* 60
        val secondsStr = secondsInMinuteUntilFinished.toString()
        val minutesStr = minutesUntilFinished.toString()
        val hoursStr = hoursUntilFinished.toString()
        time_remain.text = "${if (hoursUntilFinished == 0L) "" else hoursStr+":"} " +
                "${if (minutesStr.length == 2) minutesStr else "0" + minutesStr}:" +
                "${if (secondsStr.length == 2) secondsStr else "0" + secondsStr}"
        progressBar.progress = (timerLengthSeconds - secondsRemaining).toInt()
    }

    private fun updateButtons(){
        when (timerState) {
            TimerState.Running ->{
                fab_start.isEnabled = false
                fab_pause.isEnabled = true
                fab_stop.isEnabled = true
            }
            TimerState.Stopped -> {
                fab_start.isEnabled = true
                fab_pause.isEnabled = false
                fab_stop.isEnabled = false
            }
            TimerState.Paused -> {
                fab_start.isEnabled = true
                fab_pause.isEnabled = false
                fab_stop.isEnabled = true
            }
        }
    }




}

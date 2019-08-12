package edu.rosehulman.caoz.rosegarden


import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.util.Log
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
import java.text.ParsePosition
import java.util.*




class TaskFragment(var adapter:taskAdapter, var task:Task, var position: Int) : Fragment() {

    //private var task: Task? = null
    private var timer: CountDownTimer? = null
    private var timerLengthSeconds: Long = 0
    private var timerState = TimerState.Stopped
    private var secondsRemaining: Long = 0
    private lateinit var prefUtil:PrefUtil
    private val handler = Handler()
    //private var position: Int? = null

//    companion object {
//
//
//        @JvmStatic
//        fun newInstance(task: Task, position: Int) =
//            TaskFragment().apply {
//                arguments = Bundle().apply {
//                    putParcelable(ARG_TASK,task)
//                    putInt(ARG_INDEX,position)
//                }
//            }
//    }

    enum class TimerState{
        Stopped, Paused, Running, Done
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
//        arguments?.let {
//            task = it.getParcelable<Task>(ARG_TASK)
//            position = it.getInt(ARG_INDEX)
//        }

        prefUtil = PrefUtil(task.id)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view =inflater.inflate(R.layout.fragment_task, container, false)
        view.task_name.text = task.title
        val minuteStr =
            if(task.minute>=10){
                task.minute.toString()
        }
        else{
                "0"+  task.minute.toString()
            }
        view.due_time.text = "${if (task.hour == 0)""
        else if(task.hour == 1)task!!.hour.toString()+ "Hour "
        else task.hour.toString()+ "Hours " }" +
                "${if (task.minute <=1) task.minute.toString()+ "Min"
                else task.minute.toString()+ "Mins " }"

        view.Button_Done.setOnClickListener{v ->
            if(task.stage==2){
                startTimer()
                view.Button_Done.setImageResource(R.drawable.not_done)
                timer!!.cancel()
                onTimerRest()
               // updateButtons()
                task.stage=0
            }
            else{

                if (timer!=null) {
                    timer!!.cancel()
                }
                timerState = TimerState.Done
                progressBar.progress = progressBar.max
                Button_Done.setImageResource(R.drawable.done)
                updateButtons()
                time_remain.text = "0:00"
                task.stage=2
            }

            adapter.markDone(task.stage, position)
        }

        view.fab_start.setOnClickListener{v ->
            startTimer()
            timerState =  TimerState.Running
            updateButtons()
        }

        view.fab_pause.setOnClickListener { v ->
            timer!!.cancel()
            timerState = TimerState.Paused
            updateButtons()
        }

        view.fab_stop.setOnClickListener { v ->
            timer!!.cancel()
            onTimerRest()
            task.stage=0
            adapter.markDone(task.stage, position)
        }
        if(task.stage==2){
            timerState = TimerState.Done

            Thread(Runnable {
                    handler.post {
                        view.progressBar.max=100
                        view.progressBar.progress = 100
                        view.time_remain.text= "0:00"
                    }
            }).start()
            view.Button_Done.setImageResource(R.drawable.done)
            view.fab_start.isEnabled = false
            view.fab_pause.isEnabled = false
            view.fab_stop.isEnabled = false
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
            timer!!.cancel()
            val wakeUpTime = setAlarm(context!!, nowSeconds, secondsRemaining)

        }
        else if (timerState == TimerState.Paused){

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
        timerState = TimerState.Done
        progressBar.progress = progressBar.max
        adapter.markDone(2, position)
        Button_Done.setImageResource(R.drawable.done)
        updateButtons()
        time_remain.text = "0:00"
    }


    private fun onTimerRest(){
        timerState = TimerState.Stopped
        setNewTimerLength()
        progressBar.progress = 0
        prefUtil.setSecondsRemaining(timerLengthSeconds, context!!)
        secondsRemaining = timerLengthSeconds
        updateButtons()
        updateCountdownUI()
    }

    private fun startTimer(){
        timerState = TimerState.Running
        adapter.markDone(1,position)
        timer = object : CountDownTimer(secondsRemaining * 1000, 1000) {
            override fun onFinish() = onTimerFinished()

            override fun onTick(millisUntilFinished: Long) {
                secondsRemaining = millisUntilFinished / 1000
                updateCountdownUI()
            }

        }.start()

    }


    private fun setNewTimerLength(){
        val lengthInMinutes = task.hour*60+task.minute
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
                fab_stop.isEnabled = false
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
            TimerState.Done ->{
                fab_start.isEnabled = false
                fab_pause.isEnabled = false
                fab_stop.isEnabled = false
            }
        }
    }




}

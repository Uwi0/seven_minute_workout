package com.kakapo.a7minutesworkout.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.widget.Toast
import com.kakapo.a7minutesworkout.R
import com.kakapo.a7minutesworkout.model.Constants
import com.kakapo.a7minutesworkout.model.ExerciseModel
import kotlinx.android.synthetic.main.activity_exercise.*
import java.util.*
import kotlin.collections.ArrayList

class ExerciseActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private var restTimer: CountDownTimer? = null
    private var restProgress = 0
    private var restTimeDuration: Long = 10
    private var exerciseTimer: CountDownTimer? = null
    private var exerciseProgress = 0
    private var exerciseTimerDuration: Long = 30

    private var exerciseList: ArrayList<ExerciseModel>? = null
    private var currentExercisePosition = -1

    private var textToSpeech: TextToSpeech? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercise)

        setSupportActionBar(toolbar_exercise__activity)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        toolbar_exercise__activity.setNavigationOnClickListener {
            onBackPressed()
        }

        textToSpeech = TextToSpeech(this, this)

        exerciseList = Constants.defaultExerciseList()
        setupRestView()
    }

    override fun onDestroy() {
        if(restTimer != null){
            restTimer!!.cancel()
            restProgress = 0
        }

        if(exerciseTimer != null){
            exerciseTimer!!.cancel()
            exerciseProgress = 0
        }

        if(textToSpeech != null){
            textToSpeech!!.stop()
            textToSpeech!!.shutdown()
        }

        super.onDestroy()
    }

    override fun onInit(status: Int) {
        if(status == TextToSpeech.SUCCESS){
            val result = textToSpeech!!.setLanguage(Locale.US)
            if(result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED){
                Log.e("TextToSpeech", "The language specified is not supported")
            }
        }else{
            Log.e("TextToSpeech", "Initialized Failed")
        }
    }

    private fun setRestProgressBar(){
        progressbar.progress = restProgress
        restTimer = object : CountDownTimer(restTimeDuration * 1000, 1000){
            override fun onTick(millisUntilFinished: Long) {
                restProgress++
                progressbar.progress = restTimeDuration.toInt() - restProgress
                tv_timer.text = (restTimeDuration - restProgress).toString()
            }

            override fun onFinish() {
                currentExercisePosition++
                setupExerciseView()
            }

        }.start()
    }

    private fun setExerciseProgressBar(){
        progressbar_exercise.progress = exerciseProgress
        exerciseTimer = object : CountDownTimer(exerciseTimerDuration * 1000, 1000){
            override fun onTick(millisUntilFinished: Long) {
                exerciseProgress++
                progressbar_exercise.progress = exerciseTimerDuration.toInt() - exerciseProgress
                tv_exercise_timer.text = (exerciseTimerDuration - exerciseProgress).toString()
            }

            override fun onFinish() {
                if(currentExercisePosition < exerciseList?.size!! - 1){
                    setupRestView()
                }else{
                    Toast.makeText(
                            this@ExerciseActivity,
                            "Congratulations! you have completed the 7 minutes workout",
                            Toast.LENGTH_SHORT
                    ).show()
                }
            }

        }.start()
    }


    private fun setupRestView(){

        ll_rest_view.visibility = View.VISIBLE
        ll_exercise_view.visibility = View.GONE

        if (restTimer != null){
            restTimer!!.cancel()
            restProgress = 0
        }

        tv_upcoming_exercise_name.text = exerciseList!![currentExercisePosition + 1].name
        setRestProgressBar()
    }

    private fun setupExerciseView(){

        ll_rest_view.visibility = View.GONE
        ll_exercise_view.visibility = View.VISIBLE

        if (exerciseTimer != null){
            exerciseTimer!!.cancel()
            exerciseProgress = 0
        }
        speakOut(exerciseList!![currentExercisePosition].name)

        setExerciseProgressBar()
        iv_image_exercise.setImageResource(exerciseList!![currentExercisePosition].image)
        tv_exercise_name.text = exerciseList!![currentExercisePosition].name
    }

    private fun speakOut(text: String){
        textToSpeech!!.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
    }

}
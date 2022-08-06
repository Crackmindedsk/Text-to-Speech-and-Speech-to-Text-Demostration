package com.sharlene.speechtotext

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.graphics.blue
import androidx.core.graphics.red
import java.util.*

class MainActivity : AppCompatActivity() {

    private var btn: Button? = null
    private var txt:TextView? = null
    private var txtsph:TextToSpeech? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Toast.makeText(applicationContext,"Click on the text to hear out aloud",Toast.LENGTH_LONG).show()

        btn = findViewById(R.id.button)
        txt=findViewById(R.id.text)
        txtsph= TextToSpeech(applicationContext, TextToSpeech.OnInitListener {status ->
            if(status != TextToSpeech.ERROR){
                txtsph!!.language= Locale.US
            }
        })

        txt!!.setOnClickListener{
            val text=txt!!.text.toString()
            txtsph!!.speak(text,TextToSpeech.QUEUE_FLUSH,null)
        }


//        speakOut()

        btn!!.setOnClickListener { v->
//            speakOut()
            checkAudioPermission()
            // changing the color of mic icon, which
            // indicates that it is currently listening
//            btn.setColor(ContextCompat.getColor(this, R.color.purple_700))
            btn!!.highlightColor.blue
            // #FF0E87E7
            startSpeechToText()
        }
    }

    private fun speakOut(){
        val text= txt!!.text.toString()
        txtsph!!.speak(text,TextToSpeech.QUEUE_FLUSH,null,"")
    }

    public override fun onDestroy() {
        if(txtsph !=null){
            txtsph!!.stop()
            txtsph!!.shutdown()
        }
        super.onDestroy()
    }

    private fun startSpeechToText() {
       val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,Locale.getDefault())
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Speak Now")
        try {
            startActivityForResult(intent, REQUEST_CODE_STT)
        }catch (e:ActivityNotFoundException){
            e.printStackTrace()
            Toast.makeText(applicationContext,"Your device does not support ",Toast.LENGTH_LONG).show()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            REQUEST_CODE_STT -> {
                if (resultCode == Activity.RESULT_OK && data != null){
                    val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    if (!result.isNullOrEmpty()){
                        val recognizeTxt=result[0]
                        if(recognizeTxt.contains("4")){
                            txtsph!!.speak("Congratulations! Correct answer",TextToSpeech.QUEUE_FLUSH,null,"")
                            Toast.makeText(applicationContext,"Correct answer",Toast.LENGTH_SHORT).show()
                        }else{
                            Toast.makeText(applicationContext,"Incorrect answer",Toast.LENGTH_SHORT).show()
                            txtsph!!.speak("Sorry, Try again",TextToSpeech.QUEUE_FLUSH,null,"")
                        }
//                        txt!!.setText(recognizeTxt)
                    }
                }
            }
        }
    }

    private fun checkAudioPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {  // M = 23
            if (ContextCompat.checkSelfPermission(this, "android.permission.RECORD_AUDIO") != PackageManager.PERMISSION_GRANTED) {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:com.sharlene.speechtotext"))
                startActivity(intent)
                Toast.makeText(this, "Allow Microphone Permission", Toast.LENGTH_SHORT).show()
            }
        }
    }
    companion object {
        private const val REQUEST_CODE_STT = 1
    }

}